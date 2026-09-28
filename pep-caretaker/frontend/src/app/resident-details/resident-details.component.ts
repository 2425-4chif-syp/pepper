import { Component, inject, OnInit, signal } from '@angular/core';
import { AuthSrcDirective } from '../auth-src.directive';
import { SheetComponent } from '../ui/sheet/sheet.component';
import { pictureUrl } from '../image-url';
import { ActivatedRoute, Params, Router, RouterLink } from '@angular/router';
import { Person } from '../models/person.model';
import { ResidentServiceService } from '../service/resident-service.service';
import { ImageServiceService } from '../service/image-service.service';
import { ImageModel } from '../models/image.model';
import { ImageDto } from '../models/imageDto.model';
import { PersonDto } from '../models/person-dto.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-resident-details',
  imports: [AuthSrcDirective, RouterLink, CommonModule, FormsModule, SheetComponent],
  templateUrl: './resident-details.component.html',
  styleUrl: './resident-details.component.css'
})
export class ResidentDetailsComponent implements OnInit {
  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {}
  
  isEditing = signal<boolean>(false);
  editFirstName = signal<string>('');
  editLastName = signal<string>('')
  editDob = signal<string>('');
  editRoomNo = signal<string>('')
  editIsWorker = signal<boolean>(false);
  editPassword = signal<string>('');
  editRole = signal<string>('');
  selectedRole = signal<string>('');
  selectedImage = signal<string | null>(null);
  imageFile = signal<File | null>(null);
  currentProfileImage = signal<string | null>(null);

  startEditing() {
    const resident = this.residentOfId();
    if (resident) {
      this.editFirstName.set(resident.firstName);
      this.editLastName.set(resident.lastName);
      this.editRole.set(resident.isWorker ? 'worker' : 'resident');
      this.editRoomNo.set(resident.roomNo);
      this.editDob.set(resident.dob);
      this.editPassword.set(''); // Passwort wird nicht vorgefüllt aus Sicherheitsgründen
      this.editIsWorker.set(resident.isWorker);
    }
    this.isEditing.set(true);
  }

  cancelEditing() {
    this.isEditing.set(false);
  }

  onRoleChange(event: any) {
    const selectedValue = event.target.value;
    this.editIsWorker.set(selectedValue === 'worker');
  }
  residents = signal<Person[]>([]);
  residentService = inject(ResidentServiceService);
  actId: number = 0;
  residentOfId = signal<Person | undefined>(undefined);

  imageService = inject(ImageServiceService)
  images = signal<ImageDto[]>([]);

  imagesOfPerson = signal<ImageDto[]>([]);
  // Modal preview for gallery images
  selectedGalleryImage = signal<ImageDto | null>(null);

  openGalleryImage(image: ImageDto) {
    this.selectedGalleryImage.set(image);
  }

  closeGalleryImage() {
    this.selectedGalleryImage.set(null);
  }

  ngOnInit() {  
    this.activatedRoute.params.subscribe(
      (params: Params) => {
      this.actId=Number(params['id']);
      }
    );
    this.getResidentById(this.actId);
  }

  getResidentById(id: number) {
    this.residentService.getResidentById(id).subscribe({
      next: data => {
        this.residentOfId.set(data);
        this.loadImages();
      },
      error: error => {
        console.error("Laden der Person fehlgeschlagen." + error.name);
      }
    });
  }

  loadImages(){
    this.imageService.getImageById(this.actId).subscribe({
      next: data => {
        this.imagesOfPerson.set(data);
        // Setze das erste Bild als aktuelles Profilbild
        if (data.length > 0) {
          const first = data[0];
          if (first && first.id) {
            // Use Imagor URL for faster/responsive delivery
            this.currentProfileImage.set(this.getImagorUrl(first.id, 800));
          } else {
            this.currentProfileImage.set('data:image/png;base64,' + first.base64Image);
          }
        }
      },
      error: error => {
        console.error("Laden der Bilder fehlgeschlagen." + error.name);
      }
    });
  }    

  /**
   * Build an Imagor URL for an image id. Uses backend host encoding similar to other components.
   * width: pixel width to fit into (height auto)
   */
  getImagorUrl(imageId: number | undefined, width = 800): string {
    if (!imageId) return '';
    return pictureUrl(imageId, width);
  }

  onImageSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      this.imageFile.set(file);
      
      // Preview erstellen
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.selectedImage.set(e.target.result);
      };
      reader.readAsDataURL(file);
    }
  }

  removeNewImage() {
    this.selectedImage.set(null);
    this.imageFile.set(null);
  }

  uploadNewImage() {
    const base64Image = this.selectedImage()?.split(',')[1];
    if (!base64Image) return;

    const imageData: ImageModel = {
      personId: this.actId,
      base64Image: base64Image,
      description: 'Profilbild'
    };

    this.imageService.uploadImage(imageData).subscribe({
      next: () => {
        console.log('Bild erfolgreich hochgeladen');
        this.selectedImage.set(null);
        this.imageFile.set(null);
        this.loadImages();
      },
      error: error => {
        console.error('Fehler beim Hochladen des Bildes:', error);
      }
    });
  }

  saveChanges(){
    const updatedPerson: Person = {
      id: this.actId,
      firstName: this.editFirstName(),
      lastName: this.editLastName(),
      dob: this.editDob(),
      roomNo: this.editRoomNo(),
      isWorker: this.editIsWorker(),
      password: this.editPassword()
    };

    this.residentService.updatePerson(this.actId, updatedPerson).subscribe({
      next: (data) => {
        console.log("Person erfolgreich aktualisiert:", data);
        this.residentOfId.set(data);
        this.isEditing.set(false);
        
        // Wenn ein neues Bild ausgewählt wurde, hochladen
        if (this.selectedImage()) {
          this.uploadNewImage();
        } else {
          this.getResidentById(this.actId);
        }
      },
      error: (error) => {
        console.error("Aktualisierung der Person fehlgeschlagen." + error.name);
      }
    });
  }
}
