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
  // imagor would resize images server-side, but it fetches them from the backend without a token and the
  // picture endpoint requires one. Uploads are already cropped to 1280x800, so images come straight from
  // apiUrl (loaded with the token by AuthSrcDirective). Set { url, sourceBaseUrl } to re-enable imagor.
  imagor: null as { url: string; sourceBaseUrl: string } | null,
};
