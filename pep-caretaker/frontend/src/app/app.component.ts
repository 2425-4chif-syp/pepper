import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterOutlet, RouterModule } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { RoleService } from './role.service';
import { InactivityService } from './inactivity.service';

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

  private isDarkmode:boolean = false;
  
  public isDarkmodeActive(): boolean {
    return this.isDarkmode;
  }

  public onDarkmode():void{
    this.isDarkmode = !this.isDarkmode;
    const theme = this.isDarkmode ? 'dark' : 'light';
    document.getElementById("appComp")?.setAttribute('data-theme', theme);
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
