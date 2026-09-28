import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthSrcDirective } from '../auth-src.directive';
import { pictureUrl } from '../image-url';
import { ImageServiceService } from '../service/image-service.service';
import { RoleService } from '../role.service';
import { ResidentServiceService } from '../service/resident-service.service';
import { ImageDto } from '../models/imageDto.model';
import { catchError, of, switchMap, map } from 'rxjs';

@Component({
  selector: 'app-my-pictures',
  imports: [CommonModule, AuthSrcDirective],
  templateUrl: './my-pictures.component.html',
  styleUrl: './my-pictures.component.css'
})
export class MyPicturesComponent implements OnInit {
  imageService = inject(ImageServiceService);
  roleService = inject(RoleService);
  residentService = inject(ResidentServiceService);
  
  /** Bilder kommen ueber den Bild-Endpoint statt als Base64 im JSON - so greifen
   *  Lazy-Loading, Blob-Cache und (sofern konfiguriert) die Groessenanpassung. */
  readonly thumbUrl = (id: number | undefined) => pictureUrl(id, 600);

  myImages = signal<ImageDto[]>([]);
  loading = signal<boolean>(true);
  userName = signal<string>('');
  errorMessage = signal<string>('');

  ngOnInit() {
    this.loadUserInfo();
    this.loadMyImages();
  }

  loadUserInfo() {
    const userInfo = this.roleService.getUserInfo();
    if (userInfo) {
      this.userName.set(userInfo.preferred_username || userInfo.name || 'Bewohner');
    }
  }

  loadMyImages() {
    const userInfo = this.roleService.getUserInfo();
    if (!userInfo) {
      console.error('No user info found');
      this.errorMessage.set('Keine Benutzerinformationen gefunden.');
      this.loading.set(false);
      return;
    }

    const username = userInfo.preferred_username || userInfo.name || '';
    console.log('Loading images for username:', username);
    
    // Alle Personen laden und nach Username suchen
    this.residentService.getResidents().pipe(
      map(persons => {
        // Versuche Person zu finden:
        // 1. Username = "max.mueller" -> suche nach firstName="max" lastName="mueller"
        // 2. Username = "MaxMueller" -> suche nach firstName="Max" lastName="Mueller"
        const parts = username.toLowerCase().split(/[.\-_]/); // Split bei . - oder _
        
        let foundPerson = persons.find(p => {
          const firstName = p.firstName?.toLowerCase() || '';
          const lastName = p.lastName?.toLowerCase() || '';
          
          // Versuche verschiedene Kombinationen
          if (parts.length >= 2) {
            return (firstName === parts[0] && lastName === parts[1]) ||
                   (firstName === parts[1] && lastName === parts[0]);
          }
          
          // Fallback: ganzer Username gleich Vor- oder Nachname
          return firstName === username.toLowerCase() || lastName === username.toLowerCase();
        });
        
        if (!foundPerson) {
          throw new Error('Person not found');
        }
        
        console.log('Found person:', foundPerson.firstName, foundPerson.lastName, 'ID:', foundPerson.id);
        return foundPerson.id!;
      }),
      catchError(error => {
        // Fehler beim Finden der Person
        console.error('Error finding person:', error);
        this.errorMessage.set(`Keine Person mit dem Benutzernamen "${username}" gefunden. Bitte Administrator kontaktieren.`);
        this.loading.set(false);
        return of(null);
      }),
      switchMap(personId => {
        if (!personId) {
          return of([]); // Person nicht gefunden
        }
        // Lade Bilder für diese Person
        return this.imageService.getImageById(personId).pipe(
          catchError(error => {
            // Fehler beim Laden der Bilder = wahrscheinlich keine Bilder vorhanden
            console.log('No images found for person, showing empty state');
            return of([]); // Leeres Array = "Noch keine Bilder" Anzeige
          })
        );
      })
    ).subscribe(images => {
      this.myImages.set(images);
      this.loading.set(false);
    });
  }
}
