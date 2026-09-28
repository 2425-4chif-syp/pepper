import { CommonModule } from '@angular/common';
import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
  ViewChild,
} from '@angular/core';

/**
 * Modaler Dialog, den sich Bilderübersicht und Bewohner-Details teilen.
 *
 * Vorher hatte jede Seite ihr eigenes, fast identisches Markup. Hier liegt das Verhalten
 * einmal: Escape und Klick auf den Hintergrund schliessen, der Fokus wandert beim Öffnen
 * hinein, bleibt per Tab darin gefangen und kehrt beim Schliessen dorthin zurück, wo er
 * herkam.
 *
 * Die Bewegung kommt aus `sheet.component.css`: Ein- und Ausgang laufen denselben Weg,
 * Blur und Skalierung laufen gemeinsam - die Fläche kommt als Material an, statt nur
 * eingeblendet zu werden. Bei `prefers-reduced-motion` bleibt eine kurze Überblendung.
 */
@Component({
  selector: 'app-sheet',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sheet.component.html',
  styleUrl: './sheet.component.css',
})
export class SheetComponent implements AfterViewInit, OnDestroy {
  /** Überschrift des Dialogs. Wird auch als Beschriftung für Screenreader verwendet. */
  @Input() heading = '';

  /** Beschriftung des Schliessen-Buttons. */
  @Input() closeLabel = 'Schliessen';

  @Output() closed = new EventEmitter<void>();

  @ViewChild('panel') panel?: ElementRef<HTMLElement>;

  private previouslyFocused: HTMLElement | null = null;

  ngAfterViewInit(): void {
    this.previouslyFocused = document.activeElement as HTMLElement | null;
    this.focusFirst();
  }

  ngOnDestroy(): void {
    this.previouslyFocused?.focus?.();
  }

  close(): void {
    this.closed.emit();
  }

  /** Nur ein Klick auf den Hintergrund schliesst - nicht einer, der im Panel begonnen hat. */
  onBackdropPointerDown(event: MouseEvent): void {
    if (event.target === event.currentTarget) this.close();
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Escape') {
      event.stopPropagation();
      this.close();
      return;
    }
    if (event.key === 'Tab') this.trapFocus(event);
  }

  private focusable(): HTMLElement[] {
    const root = this.panel?.nativeElement;
    if (!root) return [];
    return Array.from(
      root.querySelectorAll<HTMLElement>(
        'a[href], button:not([disabled]), input:not([disabled]), select:not([disabled]), textarea:not([disabled]), [tabindex]:not([tabindex="-1"])',
      ),
    ).filter(element => element.offsetParent !== null);
  }

  private focusFirst(): void {
    const [first] = this.focusable();
    (first ?? this.panel?.nativeElement)?.focus();
  }

  private trapFocus(event: KeyboardEvent): void {
    const elements = this.focusable();
    if (elements.length === 0) return;

    const first = elements[0];
    const last = elements[elements.length - 1];
    const active = document.activeElement;

    if (event.shiftKey && active === first) {
      event.preventDefault();
      last.focus();
    } else if (!event.shiftKey && active === last) {
      event.preventDefault();
      first.focus();
    }
  }
}
