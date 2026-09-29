import { HttpInterceptorFn } from '@angular/common/http';
import { API_URL } from '../constants';

/**
 * Haengt den Keycloak-Token an jede Anfrage an das Mealplan-Backend.
 *
 * Das Backend prueft seit der Einfuehrung von quarkus-oidc jeden Aufruf. Vorher
 * lag der Token nur im localStorage und wurde nie mitgeschickt - die Anmeldung
 * war reine Anzeige im Browser, die API selbst war offen.
 *
 * Nur Anfragen an das eigene Backend bekommen den Header: ein Token, der an
 * eine fremde Herkunft geht, ist ein Leck.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const apiPrefix = `${API_URL}/api/`;
  if (!req.url.startsWith(apiPrefix)) {
    return next(req);
  }

  const token = localStorage.getItem('kc_token');
  if (!token) {
    return next(req);
  }

  return next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
};
