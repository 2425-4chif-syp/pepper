import { CommonModule } from '@angular/common';
import { authHeaders } from '../auth.interceptor';
import { AuthSrcDirective } from '../auth-src.directive';
import { pictureUrl } from '../image-url';
import { Component, ElementRef, HostListener, ViewChild, inject, signal } from '@angular/core';
import { ImageServiceService } from '../service/image-service.service';
import { ImageModel } from '../models/image.model';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { ImageDto } from '../models/imageDto.model';
import { ImageJson } from '../models/image-json.model';

@Component({
  selector: 'app-picture-overview',
  imports: [AuthSrcDirective, CommonModule, RouterModule],
  templateUrl: './picture-overview.component.html',
  styleUrl: './picture-overview.component.css'
})
export class PictureOverviewComponent {

  constructor(private router: Router) {}

  imagesService = inject(ImageServiceService);
  readonly pictureUrl = pictureUrl;
  images = signal<ImageJson[]>([]);
  standartImages = signal<ImageJson[]>([])

  tmpImages = this.images()
  activeButton = signal<string>("All")

  aktiverFilter = this.showAllImages

  showAllImages(){
    this.aktiverFilter = this.showAllImages
    this.activeButton.set('All')
    this.images.set(this.standartImages())
  }

  showImageOfStories(){
    let saveArr: ImageJson[] = []
    this.activeButton.set('Stories')

    this.aktiverFilter = this.showImageOfStories

    for (const element of this.standartImages()) {
      if(element.person == null){
        saveArr.push(element)
      }
    }
    console.log(saveArr)
    this.images.set(saveArr)
  }

  showImageOfPersons(){
    let saveArr: ImageJson[] = []
    this.activeButton.set('People')

    this.aktiverFilter = this.showImageOfPersons

    for (const element of this.standartImages()) {
      if(element.person != null){
        saveArr.push(element)
      }
    }
    console.log(saveArr)
    this.images.set(saveArr)
  }

  ngOnInit(): void {
    this.loadImages();
    this.aktiverFilter;
  }
  transformImageUrl(originalUrl: string): string {
    if (!originalUrl) return '';
    
    try {
      const transformedUrl = originalUrl.replace(
        'vm107.htl-leonding.ac.at:8080', 
        'backend:8080'
      );
      
      return encodeURIComponent(transformedUrl);
    } catch (error) {
      console.error('Error transforming URL:', error);
      return originalUrl;
    }
  }

  loadImages(): void {
    this.imagesService.getImageNew().subscribe(
      {
        next: data=>{
            // Map and then reverse so newest images (assumed at the end) appear first
            const encodedImages = data.items.map(image => ({
              ...image,
              href: this.transformImageUrl(image.href),
              originalHref: image.href 
            })).reverse();
            this.standartImages.set(encodedImages);
            this.aktiverFilter(); // aktiven Filter (z. B. nach dem Löschen) beibehalten
          console.log(this.images());
        },
        error: err=>{
          "Laden fehlgeschlagen" + err.message;
        },
      }
    )
  };

  goToUpload() {
    this.router.navigate(['/imageUpload']);
  }

  selectedImage = signal<ImageJson | null>(null);
  deleting = signal(false);
  deleteError = signal<string | null>(null);

  // Fokus beim Öffnen auf den Schließen-Button, damit Tastatur/Screenreader im Dialog landen
  @ViewChild('closeButton') set closeButton(button: ElementRef<HTMLButtonElement> | undefined) {
    button?.nativeElement.focus();
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    if (this.selectedImage()) this.closePreview();
  }

  openPreview(image: ImageJson) {
    this.selectedImage.set(image);
    console.log(this.selectedImage());
  }

  closePreview() {
    this.selectedImage.set(null);
    this.deleteError.set(null);
  }

  downloadImage() {
    const image = this.selectedImage();
    if (!image || !image.href) return;

    const originalUrl = (image as any).originalHref || decodeURIComponent(image.href);

  fetch(apiPathOf(originalUrl), { headers: authHeaders() })
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
      a.download = (image.description?.replace(/\s+/g, '_') || 'image') + '.jpg';

      document.body.appendChild(a);
      a.click();

      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      
      console.log('Bild erfolgreich heruntergeladen:', image.description);
    })
    .catch(error => {
      console.error('Fehler beim Herunterladen des Bildes:', error);

      const a = document.createElement('a');
      a.href = originalUrl;
      a.download = (image.description?.replace(/\s+/g, '_') || 'image') + '.jpg';
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

// Backend-hrefs sind absolut (http://localhost:8080/api/...); über den eigenen Origin laden, damit Proxy/nginx greift
function apiPathOf(url: string): string {
  try {
    const parsed = new URL(url, window.location.origin);
    return parsed.pathname.startsWith('/api/') ? parsed.pathname + parsed.search : url;
  } catch {
    return url;
  }
}
