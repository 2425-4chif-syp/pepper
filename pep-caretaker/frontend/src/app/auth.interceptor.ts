import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../environments/environment';

/** Attaches the Keycloak bearer token to calls against our own backend. */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('kc_token');
  if (!token || !req.url.startsWith(environment.apiUrl)) return next(req);
  return next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
};

/** Same header for plain `fetch()` calls, which bypass HttpClient interceptors. */
export function authHeaders(): Record<string, string> {
  const token = localStorage.getItem('kc_token');
  return token ? { Authorization: `Bearer ${token}` } : {};
}
