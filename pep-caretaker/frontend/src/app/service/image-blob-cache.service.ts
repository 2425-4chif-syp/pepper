import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, ReplaySubject, Subject, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

/** Gleichzeitige Bild-Requests. HTTP/1.1 erlaubt pro Origin ohnehin nur ~6. */
const MAX_PARALLEL = 6;

/** Wieviele fertige Blobs maximal im Speicher gehalten werden, bevor die ältesten fallen. */
const MAX_CACHED = 120;

/**
 * Prüft, ob die Antwort wirklich ein Bild ist.
 *
 * Nicht jeder Fehler kommt als Fehlerstatus zurück: Läuft die Anfrage versehentlich in den
 * SPA-Fallback (dev-server ohne passenden Proxy-Eintrag, nginx ohne /imagor/-Block), antwortet
 * der Server mit HTTP 200 und index.html. Ohne diese Prüfung landet dieses HTML als Blob im
 * <img> - das Bild bleibt leer, und weil kein Fehler auftrat, greift auch der Fallback nicht.
 * Ein leerer Typ bleibt erlaubt, manche Quellen liefern keinen Content-Type.
 */
function looksLikeImage(blob: Blob): boolean {
  const type = blob.type.toLowerCase();
  if (!type || type.startsWith('image/')) return true;
  return !(type.startsWith('text/') || type.startsWith('application/json'));
}

interface CacheEntry {
  /** Fertige Blob-URL, sobald geladen. */
  objectUrl?: string;
  /** Wieviele <img> dieses Bild gerade anzeigen. */
  refCount: number;
  lastUsed: number;
  /** Liefert die Blob-URL - auch an alle, die währenddessen dazukommen. */
  readonly load$: ReplaySubject<string>;
}

/**
 * Hält geladene Bild-Blobs zentral, statt sie pro `<img>` zu besitzen.
 *
 * Vorher besass jede Direktiven-Instanz ihren eigenen Blob und gab ihn beim Zerstören frei.
 * Ein Filterklick auf der Bilderübersicht hat damit jedes `<img>` neu erzeugt, jeden Blob
 * verworfen und alles erneut heruntergeladen. Hier gilt stattdessen:
 *
 * - gleiche URL, ein Request (parallele Anfragen teilen sich das Ergebnis),
 * - Referenzzählung statt sofortigem Freigeben: kurzzeitig ungenutzte Bilder bleiben liegen,
 * - höchstens {@link MAX_PARALLEL} Requests gleichzeitig,
 * - höchstens {@link MAX_CACHED} Blobs im Speicher, älteste zuerst raus.
 */
@Injectable({ providedIn: 'root' })
export class ImageBlobCacheService {
  private http = inject(HttpClient);

  private entries = new Map<string, CacheEntry>();
  private queue$ = new Subject<string>();

  constructor() {
    this.queue$
      .pipe(mergeMap(url => this.fetch(url), MAX_PARALLEL))
      .subscribe();
  }

  /**
   * Fordert die Blob-URL zu `url` an und meldet eine Nutzung an.
   * Jedes erfolgreiche `acquire` braucht später ein {@link release} mit derselben URL.
   */
  acquire(url: string): Observable<string> {
    let entry = this.entries.get(url);

    if (!entry) {
      entry = { refCount: 0, lastUsed: Date.now(), load$: new ReplaySubject<string>(1) };
      this.entries.set(url, entry);
      this.queue$.next(url);
    }

    entry.refCount++;
    entry.lastUsed = Date.now();

    return entry.objectUrl ? of(entry.objectUrl) : entry.load$.asObservable();
  }

  /** Meldet ab. Der Blob bleibt zunächst im Cache und fällt erst beim Aufräumen weg. */
  release(url: string): void {
    const entry = this.entries.get(url);
    if (!entry) return;
    entry.refCount = Math.max(0, entry.refCount - 1);
    entry.lastUsed = Date.now();
    this.trim();
  }

  /** Entfernt einen Eintrag sofort - z. B. wenn das Bild serverseitig gelöscht wurde. */
  invalidate(url: string): void {
    const entry = this.entries.get(url);
    if (!entry) return;
    this.entries.delete(url);
    if (entry.objectUrl) URL.revokeObjectURL(entry.objectUrl);
  }

  private fetch(url: string): Observable<unknown> {
    return new Observable(subscriber => {
      const entry = this.entries.get(url);
      if (!entry) {
        subscriber.complete();
        return;
      }

      const fail = (err: unknown) => {
        // Eintrag entfernen, damit ein späterer Versuch erneut laden darf.
        if (this.entries.get(url) === entry) this.entries.delete(url);
        entry.load$.error(err);
        subscriber.complete();
      };

      const sub = this.http.get(url, { responseType: 'blob' }).subscribe({
        next: blob => {
          // Wurde der Eintrag zwischenzeitlich verworfen, den Blob nicht behalten.
          if (this.entries.get(url) !== entry) {
            subscriber.complete();
            return;
          }
          if (!looksLikeImage(blob)) {
            fail(new Error(`Antwort von ${url} ist kein Bild (${blob.type || 'ohne Content-Type'})`));
            return;
          }
          entry.objectUrl = URL.createObjectURL(blob);
          entry.load$.next(entry.objectUrl);
          entry.load$.complete();
          this.trim();
          subscriber.complete();
        },
        error: fail,
      });

      return () => sub.unsubscribe();
    });
  }

  /** Gibt die ältesten ungenutzten Blobs frei, sobald der Cache über die Grenze wächst. */
  private trim(): void {
    if (this.entries.size <= MAX_CACHED) return;

    const evictable = [...this.entries.entries()]
      .filter(([, entry]) => entry.refCount === 0 && entry.objectUrl)
      .sort((a, b) => a[1].lastUsed - b[1].lastUsed);

    let excess = this.entries.size - MAX_CACHED;
    for (const [url, entry] of evictable) {
      if (excess <= 0) break;
      this.entries.delete(url);
      URL.revokeObjectURL(entry.objectUrl!);
      excess--;
    }
  }
}
