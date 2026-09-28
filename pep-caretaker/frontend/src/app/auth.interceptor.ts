import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../environments/environment';

/** true, wenn die URL auf unser Backend oder den vorgelagerten Bild-Resizer zeigt. */
function isOwnBackend(url: string): boolean {
  if (url.startsWith(environment.apiUrl)) return true;
  const imagor = environment.imagor?.url;
  // imagor holt das Bild selbst beim Backend - dafür braucht es den Token des Aufrufers.
  // nginx reicht den Authorization-Header an imagor weiter.
  return !!imagor && url.startsWith(imagor);
}

/** Attaches the Keycloak bearer token to calls against our own backend. */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('kc_token');
  if (!token || !isOwnBackend(req.url)) return next(req);
  return next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
};

/** Same header for plain `fetch()` calls, which bypass HttpClient interceptors. */
export function authHeaders(): Record<string, string> {
  const token = localStorage.getItem('kc_token');
  return token ? { Authorization: `Bearer ${token}` } : {};
}
