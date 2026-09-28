import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterOutlet, RouterModule } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { RoleService } from './role.service';
import { InactivityService } from './inactivity.service';
import { ThemeService } from './service/theme.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterModule, FormsModule],
  providers: [AuthGuard, RoleService, InactivityService],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'PepperAngular';

  private theme = inject(ThemeService);

  constructor(
    private authGuard: AuthGuard, 
    private roleService: RoleService,
    private inactivityService: InactivityService
  ) {}

  ngOnInit(): void {
    // Inaktivitäts-Monitoring starten wenn User eingeloggt ist
    const token = localStorage.getItem('kc_token');
    if (token) {
      this.inactivityService.startMonitoring();
    }
  }

  public isDarkmodeActive(): boolean {
    return this.theme.isDark();
  }

  public onDarkmode(): void {
    this.theme.toggle();
  }

// ✅ Logout-Funktion die sicherstellt, dass User zum Login zurückkehrt
  public logout(): void {
    console.log('Logging out...');
    
    // Inaktivitäts-Monitoring stoppen
    this.inactivityService.stopMonitoring();
    
    // Tokens löschen
    localStorage.removeItem('kc_token');
    localStorage.removeItem('kc_refresh_token');
    
    // Keycloak Logout (führt zur Login-Seite)
    this.authGuard.logout();
  }

  // ✅ User-Info für Anzeige
  public currentUser(): string {
    const userInfo = this.roleService.getUserInfo();
    return userInfo?.preferred_username || userInfo?.name || 'User';
  }
}
