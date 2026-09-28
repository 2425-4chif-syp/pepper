// Production (vm107) — used by `ng build` / CI. Dev overrides live in environment.development.ts.
export const environment = {
  production: true,
  keycloak: {
    url: 'https://vm107.htl-leonding.ac.at/auth',
    realm: 'pepper',
    clientId: 'angular-frontend',
  },
  // relative, so nginx (prod) or the ng-serve proxy (dev) decides which backend answers
  apiUrl: '/api/',
  // imagor verkleinert Bilder serverseitig: eine Kachel lädt dann ~400px statt des 1280x800-Originals.
  //
  // Damit das funktioniert, muss imagor den Bearer-Token an das Backend weiterreichen - der
  // Bild-Endpoint ist geschützt. nginx schickt den Header bereits an imagor
  // (`proxy_set_header Authorization $http_authorization;`), imagor selbst braucht dafür aber noch
  //     HTTP_LOADER_FORWARD_HEADERS=Authorization
  // auf dem imagor-Container auf vm107 (siehe docker/docker-compose.yaml für die lokale Variante).
  //
  // Solange das dort nicht gesetzt ist, antwortet imagor mit einem Fehler - AuthSrcDirective fällt
  // dann automatisch auf den direkten Backend-Pfad zurück, die Bilder erscheinen also trotzdem.
  // Auf `null` setzen, um imagor ganz zu überspringen.
  imagor: { url: '/imagor', sourceBaseUrl: 'http://backend:8080/api/' } as { url: string; sourceBaseUrl: string } | null,
};
