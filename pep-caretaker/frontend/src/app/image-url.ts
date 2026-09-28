import { environment } from '../environments/environment';

/** Pfad des Bild-Endpoints ohne jede Zwischenstation - immer erreichbar, immer Originalgrösse. */
export function picturePath(imageId: number | string | undefined | null): string {
  if (imageId === undefined || imageId === null || imageId === '') return '';
  return environment.apiUrl + `image/picture/${imageId}`;
}

/**
 * URL zum Anzeigen eines gespeicherten Bildes.
 *
 * Ist in der Umgebung ein imagor konfiguriert, läuft das Bild über den Resizer und kommt in
 * der angeforderten Breite zurück - für eine 12rem hohe Kachel muss kein 1280x800-Original
 * übertragen werden. Ohne imagor bleibt es beim direkten Endpoint.
 *
 * Schlägt der imagor-Weg fehl, fällt {@link AuthSrcDirective} über {@link directFallbackFor}
 * automatisch auf den direkten Pfad zurück - imagor ist damit eine Optimierung, keine Abhängigkeit.
 */
export function pictureUrl(imageId: number | string | undefined | null, width = 800): string {
  if (imageId === undefined || imageId === null || imageId === '') return '';
  const path = `image/picture/${imageId}`;
  if (!environment.imagor) return environment.apiUrl + path;
  const source = encodeURIComponent(environment.imagor.sourceBaseUrl + path);
  return `${environment.imagor.url}/unsafe/fit-in/${width}x0/${source}?ngsw-bypass=true`;
}

/**
 * Macht aus einer imagor-URL wieder den direkten Backend-Pfad.
 * Liefert `null`, wenn die URL gar nicht über imagor läuft (dann gibt es nichts zu ersetzen).
 */
export function directFallbackFor(url: string): string | null {
  const imagor = environment.imagor;
  if (!imagor || !url.startsWith(imagor.url)) return null;

  // .../unsafe/fit-in/400x0/<urlencodierte Quelle>?ngsw-bypass=true
  const match = /\/unsafe\/(?:[^/]+\/)*?([^/?]+)(?:\?|$)/.exec(url);
  if (!match) return null;

  let source: string;
  try {
    source = decodeURIComponent(match[1]);
  } catch {
    return null;
  }

  const index = source.indexOf('/image/picture/');
  if (index === -1) return null;
  // "http://backend:8080/api/image/picture/12" -> "image/picture/12" -> "/api/image/picture/12"
  return environment.apiUrl + source.slice(index + 1);
}
