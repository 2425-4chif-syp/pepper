import { Directive, ElementRef, Input, OnChanges, OnDestroy, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { environment } from '../environments/environment';

const FALLBACK_SRC = 'assets/images/imageNotFound.png';

/**
 * `<img [appAuthSrc]="url">` statt `[src]` für Bilder vom eigenen Backend.
 *
 * Ein normales `<img src>` kann keinen Bearer-Token mitschicken, die Bild-Endpoints sind aber
 * geschützt. Zeigt die URL auf unsere API (relativ oder als absolute Backend-href), wird das Bild
 * über HttpClient geladen (authInterceptor hängt den Token an) und als Blob-URL angezeigt.
 * Andere URLs (data:, assets/, externe Hosts) werden unverändert als src gesetzt.
 */
@Directive({
  selector: 'img[appAuthSrc]',
  standalone: true,
})
export class AuthSrcDirective implements OnChanges, OnDestroy {
  @Input() appAuthSrc: string | null | undefined;

  private http = inject(HttpClient);
  private img = inject<ElementRef<HTMLImageElement>>(ElementRef).nativeElement;
  private objectUrl?: string;
  private request?: Subscription;

  ngOnChanges(): void {
    this.release();
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

    this.img.removeAttribute('src'); // kein altes Bild stehen lassen, während das neue lädt
    this.request = this.http.get(apiPath, { responseType: 'blob' }).subscribe({
      next: blob => {
        this.objectUrl = URL.createObjectURL(blob);
        this.img.src = this.objectUrl;
      },
      error: () => {
        this.img.src = FALLBACK_SRC;
      },
    });
  }

  ngOnDestroy(): void {
    this.release();
  }

  private release(): void {
    this.request?.unsubscribe();
    this.request = undefined;
    if (this.objectUrl) {
      URL.revokeObjectURL(this.objectUrl);
      this.objectUrl = undefined;
    }
  }
}

/**
 * Liefert den same-origin-Pfad (z. B. `/api/image/picture/3`), wenn die URL auf unsere API zeigt.
 * Absolute Backend-hrefs (`http://localhost:8080/api/...`, `http://backend:8080/api/...`) werden
 * ebenfalls auf den Pfad reduziert, damit sie über Proxy/nginx inkl. Token laufen.
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
  if (!parsed.pathname.startsWith(environment.apiUrl)) return null;
  const sameOrigin = parsed.origin === window.location.origin;
  // fremde Hosts nur, wenn es erkennbar eine Backend-href ist (Port 8080 bzw. Container "backend")
  const knownBackend = parsed.port === '8080' || parsed.hostname === 'backend';
  return sameOrigin || knownBackend ? path : null;
}
