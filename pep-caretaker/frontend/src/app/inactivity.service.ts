import { Injectable, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { AuthGuard } from './auth.guard';

@Injectable({
  providedIn: 'root'
})
export class InactivityService {
  private inactivityTimeout: any;
  private readonly INACTIVITY_TIME = 10 * 60 * 1000; // 10 Minuten in Millisekunden
  private isMonitoring = false;

  constructor(
    private router: Router,
    private authGuard: AuthGuard,
    private ngZone: NgZone
  ) {}

  /**
   * Startet die Überwachung der Benutzeraktivität
   */
  startMonitoring(): void {
    if (this.isMonitoring) return;
    
    this.isMonitoring = true;
    console.log('Inactivity monitoring started - timeout: 10 minutes');
    
    // Initiales Timeout setzen
    this.resetTimer();
    
    // Event Listener für Benutzeraktivität
    // NgZone.runOutsideAngular verhindert unnötige Change Detection
    this.ngZone.runOutsideAngular(() => {
      window.addEventListener('mousedown', () => this.resetTimer());
      window.addEventListener('keypress', () => this.resetTimer());
      window.addEventListener('scroll', () => this.resetTimer());
      window.addEventListener('touchstart', () => this.resetTimer());
      window.addEventListener('click', () => this.resetTimer());
    });
  }

  /**
   * Stoppt die Überwachung der Benutzeraktivität
   */
  stopMonitoring(): void {
    if (!this.isMonitoring) return;
    
    this.isMonitoring = false;
    this.clearTimer();
    console.log('Inactivity monitoring stopped');
  }

  /**
   * Setzt den Inaktivitäts-Timer zurück
   */
  private resetTimer(): void {
    this.clearTimer();
    
    // Neuen Timer setzen
    this.inactivityTimeout = setTimeout(() => {
      this.ngZone.run(() => {
        this.handleInactivity();
      });
    }, this.INACTIVITY_TIME);
  }

  /**
   * Löscht den aktuellen Timer
   */
  private clearTimer(): void {
    if (this.inactivityTimeout) {
      clearTimeout(this.inactivityTimeout);
      this.inactivityTimeout = null;
    }
  }

  /**
   * Wird aufgerufen, wenn der User 10 Minuten inaktiv war
   */
  private handleInactivity(): void {
    console.log('User inactive for 10 minutes - logging out...');
    
    // Monitoring stoppen
    this.stopMonitoring();
    
    alert('Sie wurden aufgrund von Inaktivität abgemeldet.');

    // Keycloak-Session beenden und zum Login zurückkehren
    this.authGuard.logout();
  }
}
