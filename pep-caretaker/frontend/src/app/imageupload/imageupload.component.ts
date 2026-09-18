import { Component, ElementRef, HostListener, OnDestroy, OnInit, ViewChild, computed, inject, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import Cropper from 'cropperjs';
import { ImageServiceService } from '../service/image-service.service';
import { ResidentServiceService } from '../service/resident-service.service';
import { Person } from '../models/person.model';

// Pepper-Tablet: alle Bilder werden auf dieses Format zugeschnitten
const OUTPUT_WIDTH = 1280;
const OUTPUT_HEIGHT = 800;
const ASPECT_RATIO = OUTPUT_WIDTH / OUTPUT_HEIGHT;
// Zoom in Prozent relativ zu "ganzes Bild sichtbar"
const MIN_ZOOM = 100;
const MAX_ZOOM = 400;
const ZOOM_STEP = 25;

interface CropSuggestion {
  label: string;
  x: number;
  y: number;
  width: number;
  height: number;
  thumbnail: string;
}

interface StatusMessage {
  kind: 'success' | 'error';
  text: string;
}

type StoryImageType = 'title' | 'scene';

@Component({
  selector: 'app-imageupload',
  standalone: true,
  imports: [RouterModule, FormsModule],
  templateUrl: './imageupload.component.html',
  styleUrl: './imageupload.component.css'
})
export class ImageuploadComponent implements OnInit, OnDestroy {
  private imagesService = inject(ImageServiceService);
  private personService = inject(ResidentServiceService);
  private router = inject(Router);

  readonly outputWidth = OUTPUT_WIDTH;
  readonly outputHeight = OUTPUT_HEIGHT;
  readonly minZoom = MIN_ZOOM;
  readonly maxZoom = MAX_ZOOM;
  readonly zoomStep = ZOOM_STEP;

  // gesetzt, wenn die Seite aus dem Geschichten-Editor aufgerufen wurde
  readonly storyImageType: StoryImageType | null = this.readStoryImageType();
  readonly pageTitle =
    this.storyImageType === 'title' ? 'Titelbild wählen'
    : this.storyImageType === 'scene' ? 'Szenenbild wählen'
    : 'Bild hochladen';

  imageUrl = signal<string | null>(null);
  naturalSize = signal<{ width: number; height: number } | null>(null);
  cropSize = signal<{ width: number; height: number } | null>(null);
  zoomPercent = signal(MIN_ZOOM);
  suggestions = signal<CropSuggestion[]>([]);
  activeSuggestion = signal<number | null>(null);
  isDragging = signal(false);
  saving = signal(false);
  status = signal<StatusMessage | null>(null);

  description = signal('');
  selectedPersonId = signal<number | null>(null);
  persons = signal<Person[]>([]);

  isUpscaled = computed(() => {
    const size = this.cropSize();
    return !!size && size.width < OUTPUT_WIDTH - 1;
  });

  canSave = computed(() =>
    !!this.cropSize() && !this.saving() && (this.storyImageType !== null || this.description().trim().length > 0)
  );

  @ViewChild('editor') private editorRef?: ElementRef<HTMLDivElement>;
  @ViewChild('image') private imageRef?: ElementRef<HTMLImageElement>;
  @ViewChild('preview') private previewRef?: ElementRef<HTMLDivElement>;
  @ViewChild('fileInput') private fileInputRef?: ElementRef<HTMLInputElement>;

  private cropper?: Cropper;
  // Zoom-Faktor, bei dem das ganze Bild in den Editor passt (= 100 %)
  private fitRatio = 1;
  // true während wir den Cropper selbst verändern, damit das nicht als Nutzeraktion zählt
  private applying = false;
  private resizeTimer?: ReturnType<typeof setTimeout>;
  // Editorgröße beim Erstellen des Croppers, um echte Größenänderungen zu erkennen
  private editorSize = { width: 0, height: 0 };

  ngOnInit(): void {
    if (this.storyImageType) return; // Personen werden nur beim normalen Upload gebraucht

    this.personService.getResidents().subscribe({
      next: persons => this.persons.set(
        [...persons].sort((a, b) => `${a.lastName} ${a.firstName}`.localeCompare(`${b.lastName} ${b.firstName}`, 'de'))
      ),
      error: err => this.showError('Bewohner konnten nicht geladen werden', err),
    });
  }

  ngOnDestroy(): void {
    this.cropper?.destroy();
    this.revokeImageUrl();
    clearTimeout(this.resizeTimer);
  }

  // #region Datei auswählen

  openFilePicker(): void {
    this.fileInputRef?.nativeElement.click();
  }

  onFileInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file) this.loadFile(file);
    input.value = ''; // gleiche Datei erneut auswählbar
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragging.set(true);
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.isDragging.set(false);
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragging.set(false);
    const file = event.dataTransfer?.files?.[0];
    if (file) this.loadFile(file);
  }

  private loadFile(file: File): void {
    if (!file.type.startsWith('image/')) {
      this.status.set({ kind: 'error', text: `„${file.name}“ ist kein Bild. Bitte eine Bilddatei (z. B. JPG oder PNG) wählen.` });
      return;
    }
    this.status.set(null);
    this.cropper?.destroy();
    this.cropper = undefined;
    this.cropSize.set(null);
    this.suggestions.set([]);
    this.revokeImageUrl();
    // Das <img> lädt die neue URL, danach startet onImageLoaded() den Cropper
    this.imageUrl.set(URL.createObjectURL(file));
  }

  // #endregion

  // #region Cropper

  onImageLoaded(): void {
    const img = this.imageRef?.nativeElement;
    if (!img) return;

    this.naturalSize.set({ width: img.naturalWidth, height: img.naturalHeight });
    this.suggestions.set(this.buildSuggestions(img));
    this.createCropper();
  }

  /** Erstellt den Cropper; mit `restore` wird ein zuvor gewählter Ausschnitt (in Bildpixeln) exakt wiederhergestellt. */
  private createCropper(restore?: { data: Cropper.Data; activeSuggestion: number | null }): void {
    const img = this.imageRef?.nativeElement;
    const editor = this.editorRef?.nativeElement;
    if (!img || !editor) return;

    this.cropper?.destroy();
    this.editorSize = { width: editor.clientWidth, height: editor.clientHeight };
    this.cropper = new Cropper(img, {
      aspectRatio: ASPECT_RATIO,
      viewMode: 1,
      dragMode: 'move',
      autoCropArea: 1,
      // Der Browser dreht Handyfotos bereits anhand der EXIF-Daten, Cropper soll das nicht nochmal tun
      checkOrientation: false,
      background: false,
      toggleDragModeOnDblclick: false,
      wheelZoomRatio: 0.1,
      // Cropper skaliert bei reinen Höhenänderungen den Ausschnitt falsch → Größenänderungen selbst behandeln
      responsive: false,
      preview: this.previewRef?.nativeElement,
      ready: () => {
        this.fitRatio = this.computeFitRatio();
        this.zoomPercent.set(MIN_ZOOM);
        if (restore) {
          this.withoutUserTracking(() => {
            this.cropper!.rotateTo(restore.data.rotate);
            this.fitCanvas();
            this.cropper!.setData({ x: restore.data.x, y: restore.data.y, width: restore.data.width, height: restore.data.height });
          });
          this.activeSuggestion.set(restore.activeSuggestion);
        } else {
          this.activeSuggestion.set(0); // autoCropArea 1 = Vorschlag "Ganzes Bild"
        }
      },
      crop: event => {
        this.cropSize.set({ width: Math.round(event.detail.width), height: Math.round(event.detail.height) });
      },
      cropstart: () => this.activeSuggestion.set(null),
      zoom: event => this.onCropperZoom(event),
    });
  }

  private onCropperZoom(event: Cropper.ZoomEvent): void {
    const min = this.fitRatio * MIN_ZOOM / 100;
    const max = this.fitRatio * MAX_ZOOM / 100;
    const { ratio, oldRatio } = event.detail;

    if (ratio < min * 0.999 || ratio > max * 1.001) {
      event.preventDefault();
      const clamped = ratio < min ? min : max;
      if (Math.abs(oldRatio - clamped) > 1e-6) this.cropper?.zoomTo(clamped);
      return;
    }

    this.zoomPercent.set(Math.round(ratio / this.fitRatio * 100));
    if (!this.applying) this.activeSuggestion.set(null);
  }

  setZoom(percent: number): void {
    const clamped = Math.min(MAX_ZOOM, Math.max(MIN_ZOOM, percent));
    this.cropper?.zoomTo(this.fitRatio * clamped / 100);
  }

  zoomBy(delta: number): void {
    this.setZoom(this.zoomPercent() + delta);
  }

  onZoomInput(event: Event): void {
    this.setZoom(Number((event.target as HTMLInputElement).value));
  }

  rotate(degrees: number): void {
    if (!this.cropper) return;
    this.withoutUserTracking(() => {
      this.cropper!.rotate(degrees);
      this.fitCanvas();
      this.maximizeCropBox();
    });
    this.activeSuggestion.set(null);
  }

  reset(): void {
    this.applySuggestion(0);
  }

  applySuggestion(index: number): void {
    const suggestion = this.suggestions()[index];
    if (!this.cropper || !suggestion) return;
    this.withoutUserTracking(() => {
      this.cropper!.rotateTo(0);
      this.fitCanvas();
      this.cropper!.setData({
        x: suggestion.x,
        y: suggestion.y,
        width: suggestion.width,
        height: suggestion.height,
      });
    });
    this.activeSuggestion.set(index);
  }

  onEditorKeydown(event: KeyboardEvent): void {
    if (!this.cropper) return;
    const step = event.shiftKey ? 50 : 10;
    switch (event.key) {
      case 'ArrowLeft': this.cropper.move(-step, 0); break;
      case 'ArrowRight': this.cropper.move(step, 0); break;
      case 'ArrowUp': this.cropper.move(0, -step); break;
      case 'ArrowDown': this.cropper.move(0, step); break;
      case '+':
      case '=': this.zoomBy(ZOOM_STEP); break;
      case '-': this.zoomBy(-ZOOM_STEP); break;
      case '0': this.reset(); break;
      default: return;
    }
    event.preventDefault();
  }

  @HostListener('window:resize')
  onWindowResize(): void {
    clearTimeout(this.resizeTimer);
    this.resizeTimer = setTimeout(() => {
      const editor = this.editorRef?.nativeElement;
      if (!this.cropper || !editor) return;
      const changed = Math.abs(editor.clientWidth - this.editorSize.width) > 1
        || Math.abs(editor.clientHeight - this.editorSize.height) > 1;
      if (!changed) return;
      // gewählten Ausschnitt in Bildpixeln merken und im neu vermessenen Editor wiederherstellen
      this.createCropper({ data: this.cropper.getData(), activeSuggestion: this.activeSuggestion() });
    }, 150);
  }

  private computeFitRatio(): number {
    const container = this.cropper!.getContainerData();
    const canvas = this.cropper!.getCanvasData(); // naturalWidth/-Height berücksichtigen die Drehung
    return Math.min(container.width / canvas.naturalWidth, container.height / canvas.naturalHeight);
  }

  // ganzes Bild sichtbar und zentriert (= 100 %)
  private fitCanvas(): void {
    const cropper = this.cropper!;
    this.fitRatio = this.computeFitRatio();
    cropper.zoomTo(this.fitRatio);
    const container = cropper.getContainerData();
    const canvas = cropper.getCanvasData();
    cropper.setCanvasData({ left: (container.width - canvas.width) / 2, top: (container.height - canvas.height) / 2 });
    this.zoomPercent.set(MIN_ZOOM);
  }

  private maximizeCropBox(): void {
    const canvas = this.cropper!.getCanvasData();
    const width = Math.min(canvas.width, canvas.height * ASPECT_RATIO);
    const height = width / ASPECT_RATIO;
    this.cropper!.setCropBoxData({
      left: canvas.left + (canvas.width - width) / 2,
      top: canvas.top + (canvas.height - height) / 2,
      width,
      height,
    });
  }

  private withoutUserTracking(fn: () => void): void {
    this.applying = true;
    try {
      fn();
    } finally {
      this.applying = false;
    }
  }

  // #endregion

  // #region Vorschläge

  /** 16:10-Ausschnitte an typischen Stellen des Originalbilds, jeweils mit kleinem Vorschaubild. */
  private buildSuggestions(img: HTMLImageElement): CropSuggestion[] {
    const W = img.naturalWidth;
    const H = img.naturalHeight;
    const maxWidth = Math.min(W, H * ASPECT_RATIO);
    const wider = W / H > ASPECT_RATIO * 1.05;
    const taller = W / H < ASPECT_RATIO / 1.05;

    const region = (label: string, scale: number, centerX: number, centerY: number) => {
      const width = maxWidth * scale;
      const height = width / ASPECT_RATIO;
      const x = Math.min(Math.max(centerX * W - width / 2, 0), W - width);
      const y = Math.min(Math.max(centerY * H - height / 2, 0), H - height);
      return { label, x: Math.round(x), y: Math.round(y), width: Math.round(width), height: Math.round(height) };
    };

    const candidates = [
      region('Ganzes Bild', 1, 0.5, 0.5),
      ...(wider ? [region('Links', 1, 0, 0.5), region('Rechts', 1, 1, 0.5)] : []),
      ...(taller ? [region('Oben', 1, 0.5, 0), region('Unten', 1, 0.5, 1)] : []),
      region('Nahaufnahme', 0.6, 0.5, 0.5),
      region('Nahaufnahme oben', 0.6, 0.5, 0.3),
    ];

    // nahezu gleiche Ausschnitte nur einmal anbieten
    const unique = candidates.filter((c, i) => !candidates.slice(0, i).some(prev =>
      prev.width === c.width && Math.abs(prev.x - c.x) < W * 0.03 && Math.abs(prev.y - c.y) < H * 0.03
    ));

    const canvas = document.createElement('canvas');
    canvas.width = 320;
    canvas.height = 200;
    const ctx = canvas.getContext('2d')!;
    return unique.map(c => {
      ctx.drawImage(img, c.x, c.y, c.width, c.height, 0, 0, canvas.width, canvas.height);
      return { ...c, thumbnail: canvas.toDataURL('image/jpeg', 0.8) };
    });
  }

  // #endregion

  // #region Speichern / Herunterladen

  private exportDataUrl(): string {
    return this.cropper!.getCroppedCanvas({
      width: OUTPUT_WIDTH,
      height: OUTPUT_HEIGHT,
      fillColor: '#fff',
      imageSmoothingEnabled: true,
      imageSmoothingQuality: 'high',
    }).toDataURL('image/jpeg', 0.92);
  }

  save(): void {
    if (!this.canSave() || !this.cropper) return;
    const dataUrl = this.exportDataUrl();

    if (this.storyImageType) {
      sessionStorage.setItem(this.storyImageType === 'title' ? 'croppedTitleImage' : 'croppedSceneImage', dataUrl);
      this.router.navigate(['/createstory']);
      return;
    }

    const description = this.description().trim();
    this.saving.set(true);
    this.imagesService.uploadImage({
      description,
      personId: this.selectedPersonId(),
      base64Image: dataUrl.split(',')[1],
    }).subscribe({
      next: () => {
        this.saving.set(false);
        this.clearImage();
        this.description.set('');
        this.selectedPersonId.set(null);
        this.status.set({ kind: 'success', text: `Bild „${description}“ wurde gespeichert.` });
      },
      error: err => {
        this.saving.set(false);
        this.showError('Speichern fehlgeschlagen', err);
      },
    });
  }

  download(): void {
    if (!this.cropper) return;
    const name = this.description().trim().toLowerCase().replace(/[^a-z0-9äöüß]+/g, '-').replace(/^-|-$/g, '') || 'pepper-bild';
    const link = document.createElement('a');
    link.href = this.exportDataUrl();
    link.download = `${name}-${OUTPUT_WIDTH}x${OUTPUT_HEIGHT}.jpg`;
    document.body.appendChild(link); // Firefox braucht den Link im DOM
    link.click();
    link.remove();
  }

  cancel(): void {
    if (this.storyImageType) {
      sessionStorage.removeItem('pendingStoryState');
      this.router.navigate(['/createstory']);
    } else {
      this.router.navigate(['/pictures']);
    }
  }

  private clearImage(): void {
    this.cropper?.destroy();
    this.cropper = undefined;
    this.revokeImageUrl();
    this.imageUrl.set(null);
    this.naturalSize.set(null);
    this.cropSize.set(null);
    this.suggestions.set([]);
    this.activeSuggestion.set(null);
  }

  // #endregion

  private revokeImageUrl(): void {
    const url = this.imageUrl();
    if (url) URL.revokeObjectURL(url);
  }

  private showError(prefix: string, err: any): void {
    const detail = err?.error?.message || err?.message || 'Unbekannter Fehler';
    this.status.set({ kind: 'error', text: `${prefix}: ${detail}` });
  }

  private readStoryImageType(): StoryImageType | null {
    const pending = sessionStorage.getItem('pendingStoryState');
    if (!pending) return null;
    try {
      return JSON.parse(pending).imageType === 'scene' ? 'scene' : 'title';
    } catch {
      return 'title';
    }
  }
}
