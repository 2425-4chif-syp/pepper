// Opt-in: local UI against the deployed vm107 Keycloak + backend — `npm run start:remote`.
// Everything you do here (uploads, edits, deletes) hits PRODUCTION data.
import { environment as prod } from './environment';

export const environment = {
  ...prod,
  production: false,
  keycloak: { ...prod.keycloak, clientId: 'angular-frontend-local' },
};
