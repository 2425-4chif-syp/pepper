import { Directive, ElementRef, Input, OnChanges, OnDestroy, inject } from '@angular/core';
import { Subscription } from 'rxjs';
import { environment } from '../environments/environment';
import { directFallbackFor } from './image-url';
import { ImageBlobCacheService } from './service/image-blob-cache.service';

const FALLBACK_SRC = 'assets/images/imageNotFound.png';

/** Wie früh vor dem Sichtbarwerden geladen wird - reicht für flüssiges Scrollen. */
const PRELOAD_MARGIN = '400px';

let observer: IntersectionObserver | null = null;
/** Konstruktor, mit dem der aktuelle Observer gebaut wurde. */
let observerSource: unknown = null;
const watched = new WeakMap<Element, () => void>();

/** Ein gemeinsamer Observer für alle Bilder statt einer Instanz pro `<img>`. */
function observe(element: Element, onVisible: () => void): void {
  if (typeof IntersectionObserver === 'undefined') {
    onVisible();
    return;
  }
  // Wurde der globale Konstruktor ausgetauscht (spät geladenes Polyfill, Test-Stub),
  // ist der gemerkte Observer nicht mehr der richtige - dann neu anlegen.
  if (observer && observerSource !== IntersectionObserver) {
    observer.disconnect();
    observer = null;
  }
  observerSource = IntersectionObserver;
  observer ??= new IntersectionObserver(
    entries => {
      for (const entry of entries) {
        if (!entry.isIntersecting) continue;
        observer!.unobserve(entry.target);
        watched.get(entry.target)?.();
        watched.delete(entry.target);
      }
    },
    { rootMargin: PRELOAD_MARGIN },
  );
  watched.set(element, onVisible);
  observer.observe(element);
}

function unobserve(element: Element): void {
  observer?.unobserve(element);
  watched.delete(element);
}

/**
 * `<img [appAuthSrc]="url">` statt `[src]` für Bilder vom eigenen Backend.
 *
 * Ein normales `<img src>` kann keinen Bearer-Token mitschicken, die Bild-Endpoints sind aber
 * geschützt. Zeigt die URL auf unsere API (relativ oder als absolute Backend-href), wird das Bild
 * über den {@link ImageBlobCacheService} geladen (authInterceptor hängt den Token an) und als
 * Blob-URL angezeigt. Andere URLs (data:, assets/, externe Hosts) werden unverändert gesetzt.
 *
 * Geladen wird erst, wenn das Bild in die Nähe des Sichtbereichs kommt. `loading="lazy"` kann das
 * hier nicht leisten: der Browser bekommt nie eine URL zu sehen, die er zurückstellen könnte.
 *
 * Schlägt ein Laden fehl, wird der Reihe nach probiert:
 * imagor-URL -> direkter Backend-Pfad -> Platzhalterbild.
 */
@Directive({
  selector: 'img[appAuthSrc]',
  standalone: true,
})
export class AuthSrcDirective implements OnChanges, OnDestroy {
  @Input() appAuthSrc: string | null | undefined;

  private cache = inject(ImageBlobCacheService);
  private img = inject<ElementRef<HTMLImageElement>>(ElementRef).nativeElement;

  /** URL, die aktuell beim Cache gemeldet ist - für das spätere release. */
  private held?: string;
  private request?: Subscription;
  private pending = false;

  ngOnChanges(): void {
    this.reset();

    const url = this.appAuthSrc;
    if (!url) {
      this.img.removeAttribute('src');
      return;
    }

    const apiPath = toApiPath(url);
    if (!apiPath) {
      this.img.src = url;
      return;
    }

    // Kein altes Bild stehen lassen, während das neue lädt.
    this.img.removeAttribute('src');
    this.img.style.opacity = '0';
    this.img.style.transition = 'opacity var(--dur-base, 300ms) var(--ease-standard, ease-out)';

    this.pending = true;
    observe(this.img, () => {
      if (!this.pending) return;
      this.load(apiPath, directFallbackFor(url));
    });
  }

  ngOnDestroy(): void {
    this.reset();
  }

  private load(url: string, fallbackUrl: string | null): void {
    this.request?.unsubscribe();
    this.request = this.cache.acquire(url).subscribe({
      next: objectUrl => {
        this.held = url;
        this.show(objectUrl);
      },
      error: () => {
        if (fallbackUrl && fallbackUrl !== url) {
          // imagor hat nicht geantwortet - direkt beim Backend versuchen.
          this.load(fallbackUrl, null);
          return;
        }
        this.img.style.opacity = '1';
        this.img.src = FALLBACK_SRC;
      },
    });
  }

  /** Erst einblenden, wenn das Bild wirklich dekodiert ist - sonst blitzt es halb gemalt auf. */
  private show(objectUrl: string): void {
    this.img.src = objectUrl;
    const reveal = () => (this.img.style.opacity = '1');
    this.img.decode ? this.img.decode().then(reveal, reveal) : reveal();
  }

  private reset(): void {
    this.pending = false;
    unobserve(this.img);
    this.request?.unsubscribe();
    this.request = undefined;
    if (this.held) {
      this.cache.release(this.held);
      this.held = undefined;
    }
  }
}

/**
 * Liefert den same-origin-Pfad (z. B. `/api/image/picture/3`), wenn die URL auf unsere API zeigt.
 * Absolute Backend-hrefs (`http://localhost:8080/api/...`, `http://backend:8080/api/...`) werden
 * ebenfalls auf den Pfad reduziert, damit sie über Proxy/nginx inkl. Token laufen.
 * imagor-URLs laufen ebenfalls über diesen Weg, damit der Token mitgeht.
 */
function toApiPath(url: string): string | null {
  if (url.startsWith('data:') || url.startsWith('blob:')) return null;
  let parsed: URL;
  try {
    parsed = new URL(url, window.location.origin);
  } catch {
    return null;
  }
  const path = parsed.pathname + parsed.search;
  const imagorBase = environment.imagor?.url;
  const isImagor = !!imagorBase && (parsed.pathname.startsWith(imagorBase) || url.startsWith(imagorBase));
  if (!parsed.pathname.startsWith(environment.apiUrl) && !isImagor) return null;
  const sameOrigin = parsed.origin === window.location.origin;
  // fremde Hosts nur, wenn es erkennbar eine Backend-href ist (Port 8080 bzw. Container "backend")
  const knownBackend = parsed.port === '8080' || parsed.hostname === 'backend';
  return sameOrigin || knownBackend ? path : null;
}
