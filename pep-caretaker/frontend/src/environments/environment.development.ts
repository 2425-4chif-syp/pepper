// Local development — `npm start`. Talks only to the docker/docker-compose.yaml stack
// (Keycloak :8081) and a local `./mvnw quarkus:dev` backend (:8080, via src/proxy.conf.json).
export const environment = {
  production: false,
  keycloak: {
    url: 'http://localhost:8081',
    realm: 'pepper',
    clientId: 'angular-frontend-local',
  },
  apiUrl: '/api/',
  imagor: null as { url: string; sourceBaseUrl: string } | null,
};
