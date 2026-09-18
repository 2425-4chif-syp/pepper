import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import Keycloak from 'keycloak-js';
import { environment } from '../environments/environment';
import { RoleService } from './role.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  private keycloak?: Keycloak;
  private keycloakReady?: Promise<boolean>;

  constructor(private router: Router, private roleService: RoleService) {}

  async canActivate(route: ActivatedRouteSnapshot): Promise<boolean> {
    // Einmal pro Seitenaufruf Keycloak starten – auch wenn schon ein Token gespeichert ist.
    // Access-Tokens laufen nach wenigen Minuten ab; nur eine laufende Keycloak-Instanz erneuert sie.
    this.keycloakReady ??= this.initKeycloak();
    if (!(await this.keycloakReady)) {
      return false;
    }
    return this.checkAccess(route);
  }

  private async initKeycloak(): Promise<boolean> {
    this.keycloak = new Keycloak(environment.keycloak);
    try {
      const authenticated = await this.keycloak.init({
        onLoad: 'login-required',
        pkceMethod: 'S256',
        checkLoginIframe: false,
        // gespeicherte Tokens übergeben: keycloak-js erneuert sie per Refresh-Token ohne Redirect;
        // nur wenn die Keycloak-Session abgelaufen ist, geht es zur Login-Seite
        token: localStorage.getItem('kc_token') || undefined,
        refreshToken: localStorage.getItem('kc_refresh_token') || undefined,
        idToken: localStorage.getItem('kc_id_token') || undefined,
      });

      if (!authenticated || !this.keycloak.token) {
        return false;
      }
      this.storeTokens();
      this.setupTokenRefresh();
      return true;
    } catch (error) {
      console.error('Keycloak initialization failed:', error);
      this.clearTokens();
      return false;
    }
  }

  private checkAccess(route: ActivatedRouteSnapshot): boolean {
    // Residents dürfen NUR zu /my-pictures
    if (this.roleService.isResident()) {
      if (route.routeConfig?.path !== 'my-pictures') {
        this.router.navigate(['/my-pictures']);
        return false;
      }
      return true;
    }

    const requiredRoles = route.data?.['roles'] as string[] | undefined;
    if (requiredRoles?.length && !this.roleService.hasAnyRole(requiredRoles)) {
      // nicht auf die Startseite umleiten, wenn genau die verweigert wurde (Endlosschleife)
      if (route.routeConfig?.path) this.router.navigate(['/']);
      return false;
    }
    return true;
  }

  private setupTokenRefresh(): void {
    setInterval(() => {
      this.keycloak?.updateToken(30)
        .then(refreshed => {
          if (refreshed) this.storeTokens();
        })
        .catch(() => {
          // Session abgelaufen → neu einloggen
          this.clearTokens();
          window.location.reload();
        });
    }, 10000);
  }

  private storeTokens(): void {
    if (!this.keycloak?.token) return;
    localStorage.setItem('kc_token', this.keycloak.token);
    localStorage.setItem('kc_refresh_token', this.keycloak.refreshToken || '');
    localStorage.setItem('kc_id_token', this.keycloak.idToken || '');
  }

  private clearTokens(): void {
    localStorage.removeItem('kc_token');
    localStorage.removeItem('kc_refresh_token');
    localStorage.removeItem('kc_id_token');
  }

  getToken(): string | null {
    return localStorage.getItem('kc_token');
  }

  // Beendet die Keycloak-Session (sonst loggt login-required beim Reload sofort wieder ein)
  logout(): void {
    const idToken = localStorage.getItem('kc_id_token');

    this.clearTokens();
    this.keycloak = undefined;
    this.keycloakReady = undefined;

    const { url, realm, clientId } = environment.keycloak;
    const params = new URLSearchParams({
      client_id: clientId,
      post_logout_redirect_uri: window.location.origin + '/',
    });
    // mit id_token_hint überspringt Keycloak die "Wirklich abmelden?"-Bestätigung
    if (idToken) params.set('id_token_hint', idToken);

    window.location.href = `${url}/realms/${realm}/protocol/openid-connect/logout?${params}`;
  }
}
