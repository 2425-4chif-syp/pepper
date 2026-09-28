// Local development — `npm start`. Talks only to the docker/docker-compose.yaml stack
// (Keycloak :8081, imagor :8000) and a local `./mvnw quarkus:dev` backend (:8080,
// via src/proxy.conf.json).
export const environment = {
  production: false,
  keycloak: {
    url: 'http://localhost:8081',
    realm: 'pepper',
    clientId: 'angular-frontend-local',
  },
  apiUrl: '/api/',
  // Läuft der imagor-Container aus docker/docker-compose.yaml nicht, schlägt der Aufruf fehl und
  // AuthSrcDirective lädt direkt beim Backend - die Bilder sind dann nur nicht verkleinert.
  // Auf `null` setzen, um imagor lokal ganz zu überspringen.
  imagor: { url: '/imagor', sourceBaseUrl: 'http://host.docker.internal:8080/api/' } as { url: string; sourceBaseUrl: string } | null,
};
