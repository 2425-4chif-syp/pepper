import { DOCUMENT } from '@angular/common';
import { Injectable, computed, inject, signal } from '@angular/core';

export type ThemeName = 'pepper' | 'pepper-dark';

const STORAGE_KEY = 'pepper-theme';
const LIGHT: ThemeName = 'pepper';
const DARK: ThemeName = 'pepper-dark';

/**
 * Hält das aktive DaisyUI-Theme.
 *
 * Das `data-theme`-Attribut sitzt auf `<html>` (nicht auf einem inneren div), damit auch
 * Dokumenthintergrund, Overscroll-Bereich und alles, was ausserhalb der App gerendert wird,
 * mitgefärbt wird. Zusätzlich wird `color-scheme` gesetzt, damit Scrollbars, Formular-Controls
 * und Autofill vom Browser passend gezeichnet werden.
 *
 * Reihenfolge beim Start: gespeicherte Wahl -> Systemeinstellung -> hell.
 * Solange der Nutzer nicht selbst gewählt hat, folgt die App der Systemeinstellung live.
 */
@Injectable({ providedIn: 'root' })
export class ThemeService {
  private document = inject(DOCUMENT);

  private readonly current = signal<ThemeName>(LIGHT);

  readonly theme = this.current.asReadonly();
  readonly isDark = computed(() => this.current() === DARK);

  /** true, sobald der Nutzer den Umschalter benutzt hat - dann gilt seine Wahl. */
  private userChose = false;

  constructor() {
    const stored = this.readStored();
    this.userChose = stored !== null;
    this.current.set(stored ?? (this.prefersDark() ? DARK : LIGHT));
    this.apply(this.current());
    this.watchSystem();
  }

  toggle(): void {
    this.set(this.isDark() ? LIGHT : DARK);
  }

  set(theme: ThemeName): void {
    this.userChose = true;
    this.current.set(theme);
    this.apply(theme);
    try {
      this.document.defaultView?.localStorage.setItem(STORAGE_KEY, theme);
    } catch {
      // Privater Modus / blockierter Storage: Theme gilt dann nur für diese Sitzung
    }
  }

  private apply(theme: ThemeName): void {
    const root = this.document.documentElement;
    root.setAttribute('data-theme', theme);
    root.style.colorScheme = theme === DARK ? 'dark' : 'light';
  }

  private readStored(): ThemeName | null {
    try {
      const value = this.document.defaultView?.localStorage.getItem(STORAGE_KEY);
      return value === LIGHT || value === DARK ? value : null;
    } catch {
      return null;
    }
  }

  private mediaQuery(): MediaQueryList | null {
    return this.document.defaultView?.matchMedia?.('(prefers-color-scheme: dark)') ?? null;
  }

  private prefersDark(): boolean {
    return this.mediaQuery()?.matches ?? false;
  }

  private watchSystem(): void {
    const mq = this.mediaQuery();
    if (!mq?.addEventListener) return;
    mq.addEventListener('change', event => {
      if (this.userChose) return;
      const next = event.matches ? DARK : LIGHT;
      this.current.set(next);
      this.apply(next);
    });
  }
}
