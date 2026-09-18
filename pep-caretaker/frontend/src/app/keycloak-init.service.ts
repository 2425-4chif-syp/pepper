import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import Keycloak, { KeycloakInstance } from 'keycloak-js';

@Injectable({ providedIn: 'root' })
export class KeycloakInitService {
  private kc: Keycloak;

  constructor() {
    this.kc = new Keycloak(environment.keycloak);
  }

  init(): Promise<boolean> {
    return this.kc.init({
      onLoad: 'login-required', 
      pkceMethod: 'S256',
    }).then(authenticated => {
      if (authenticated) {
        localStorage.setItem('kc_token', this.kc.token ?? '');
      }
      return authenticated;
    });
  }

  getToken(): string | undefined {
    return this.kc.token;
  }

  logout(): void {
    this.kc.logout();
  }
}