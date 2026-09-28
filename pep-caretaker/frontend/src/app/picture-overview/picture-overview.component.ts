import { CommonModule } from '@angular/common';
import { authHeaders } from '../auth.interceptor';
import { AuthSrcDirective } from '../auth-src.directive';
import { pictureUrl, picturePath } from '../image-url';
import { Component, HostListener, OnInit, computed, inject, signal } from '@angular/core';
import { ImageServiceService } from '../service/image-service.service';
import { ImageBlobCacheService } from '../service/image-blob-cache.service';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { ImageJson } from '../models/image-json.model';
import { SheetComponent } from '../ui/sheet/sheet.component';

type PictureFilter = 'All' | 'Stories' | 'People';

/** Breite der Kachel-Variante. Reicht für die Grid-Grösse inkl. Retina. */
const THUMB_WIDTH = 400;
/** Breite im geöffneten Dialog. */
const PREVIEW_WIDTH = 1600;

@Component({
  selector: 'app-picture-overview',
  imports: [AuthSrcDirective, CommonModule, RouterModule, SheetComponent],
  templateUrl: './picture-overview.component.html',
  styleUrl: './picture-overview.component.css'
})
export class PictureOverviewComponent implements OnInit {

  constructor(private router: Router) {}

  imagesService = inject(ImageServiceService);
  private blobCache = inject(ImageBlobCacheService);

  readonly thumbUrl = (id: number | undefined) => pictureUrl(id, THUMB_WIDTH);
  readonly previewUrl = (id: number | undefined) => pictureUrl(id, PREVIEW_WIDTH);

  standartImages = signal<ImageJson[]>([]);
  activeButton = signal<PictureFilter>('All');
  loading = signal(true);
  loadError = signal<string | null>(null);

  /** Gefiltert wird abgeleitet - so überlebt der aktive Filter jedes Neuladen von selbst. */
  images = computed<ImageJson[]>(() => {
    const all = this.standartImages();
    switch (this.activeButton()) {
      case 'Stories': return all.filter(image => image.person == null);
      case 'People': return all.filter(image => image.person != null);
      default: return all;
    }
  });

  showAllImages() { this.activeButton.set('All'); }
  showImageOfStories() { this.activeButton.set('Stories'); }
  showImageOfPersons() { this.activeButton.set('People'); }

  ngOnInit(): void {
    this.loadImages();
  }

  loadImages(): void {
    this.loading.set(true);
    this.loadError.set(null);
    this.imagesService.getImageNew().subscribe({
      next: data => {
        // Neueste Bilder (am Ende der Liste) zuerst anzeigen
        this.standartImages.set([...data.items].reverse());
        this.loading.set(false);
      },
      error: err => {
        this.loading.set(false);
        this.loadError.set(`Die Bilder konnten nicht geladen werden (HTTP ${err?.status ?? '?'}).`);
        console.error('Laden der Bilder fehlgeschlagen:', err);
      },
    });
  }

  goToUpload() {
    this.router.navigate(['/imageUpload']);
  }

  selectedImage = signal<ImageJson | null>(null);
  deleting = signal(false);
  deleteError = signal<string | null>(null);

  @HostListener('document:keydown.escape')
  onEscape(): void {
    if (this.selectedImage()) this.closePreview();
  }

  openPreview(image: ImageJson) {
    this.selectedImage.set(image);
  }

  closePreview() {
    this.selectedImage.set(null);
    this.deleteError.set(null);
  }

  downloadImage() {
    const image = this.selectedImage();
    if (!image) return;

    const source = picturePath(image.id);
    const fileName = (image.description?.replace(/\s+/g, '_') || 'image') + '.jpg';

    fetch(source, { headers: authHeaders() })
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.blob();
      })
      .then(blob => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = url;
        a.download = fileName;

        document.body.appendChild(a);
        a.click();

        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
      })
      .catch(error => {
        console.error('Fehler beim Herunterladen des Bildes:', error);

        const a = document.createElement('a');
        a.href = source;
        a.download = fileName;
        a.target = '_blank';
        a.click();
      });
  }

  deleteImage() {
    const image = this.selectedImage();
    if (!image || this.deleting()) return;
    if (!confirm(`Möchten Sie das Bild „${image.description || 'ohne Beschreibung'}“ wirklich löschen?`)) return;

    this.deleting.set(true);
    this.deleteError.set(null);
    this.imagesService.deleteImage(image.id).subscribe({
      next: () => {
        this.deleting.set(false);
        // Gelöschtes Bild aus dem Blob-Cache werfen, sonst bliebe es bis zum Reload sichtbar
        this.blobCache.invalidate(this.thumbUrl(image.id));
        this.blobCache.invalidate(this.previewUrl(image.id));
        this.closePreview();
        this.loadImages();
      },
      error: err => {
        this.deleting.set(false);
        console.error('Error deleting image:', err);
        this.deleteError.set(err?.error?.message || `Das Bild konnte nicht gelöscht werden (HTTP ${err?.status ?? '?'}).`);
      }
    });
  }
}
