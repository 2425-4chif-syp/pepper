import { Component, inject, signal, computed } from '@angular/core';
import { AuthSrcDirective } from '../auth-src.directive';
import { pictureUrl } from '../image-url';
import { Person } from '../models/person.model';
import { ResidentServiceService } from '../service/resident-service.service';
import { ImageServiceService } from '../service/image-service.service';
import { error } from 'console';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PersonDto } from '../models/person-dto.model';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

// Erweitere das Person-Interface mit der Memory-Eigenschaft
interface PersonWithMemory extends Person {
  memoryActive: boolean;
}

@Component({
  selector: 'app-residents',
  imports: [AuthSrcDirective, FormsModule, CommonModule, RouterLink],
  templateUrl: './residents.component.html',
  styleUrl: './residents.component.css'
})
export class ResidentsComponent {

  residents = signal<Person[]>([]);
  profileImages = signal<Map<number, string>>(new Map());
  filterMode = signal<'all' | 'workers' | 'residents'>('all');
  residentService = inject(ResidentServiceService);
  imageService = inject(ImageServiceService);
  dob = signal<string>('');
  firstName = signal<string>('');
  lastName = signal<string>('');
  roomNo = signal<string>('');
  isWorker = signal<boolean>(false);
  password = signal<string>('');

  // Computed: Sortiert und gefiltert
  sortedAndFilteredResidents = computed(() => {
    let filtered = this.residents();
    
    // Filter anwenden
    const mode = this.filterMode();
    if (mode === 'workers') {
      filtered = filtered.filter(p => p.isWorker === true);
    } else if (mode === 'residents') {
      filtered = filtered.filter(p => p.isWorker === false);
    }
    
    // Sortierung: Erst Hilfskräfte, dann Bewohner, jeweils alphabetisch
    return filtered.sort((a, b) => {
      // 1. Nach isWorker sortieren (Hilfskräfte zuerst)
      if (a.isWorker && !b.isWorker) return -1;
      if (!a.isWorker && b.isWorker) return 1;
      
      // 2. Alphabetisch nach Nachname, dann Vorname
      const lastNameCompare = (a.lastName || '').localeCompare(b.lastName || '');
      if (lastNameCompare !== 0) return lastNameCompare;
      return (a.firstName || '').localeCompare(b.firstName || '');
    });
  });

  // Zähler für Filter-Buttons
  workersCount = computed(() => this.residents().filter(p => p.isWorker === true).length);
  residentsCount = computed(() => this.residents().filter(p => p.isWorker === false).length + 1);

  ngOnInit() {
    this.getAllResidents();
  }

  getAllResidents(){
    this.residentService.getResidents().subscribe({
      next: data => {
        this.residents.set(data);
        console.log('Loaded residents:', this.residents());
        this.loadProfileImages(data);
      },
      error: error=> {
        console.error("Laden der Personen fehlgeschlagen." + error.name);
      }
    });
  }

  setFilter(mode: 'all' | 'workers' | 'residents') {
    this.filterMode.set(mode);
  }

  loadProfileImages(persons: Person[]) {
    const imageRequests = persons.map(person => 
      this.imageService.getImageById(person.id).pipe(
        map(images => ({ personId: person.id, images })),
        catchError(() => of({ personId: person.id, images: [] }))
      )
    );

    forkJoin(imageRequests).subscribe(results => {
      const imageMap = new Map<number, string>();
      results.forEach(result => {
        if (result.images.length > 0) {
          // Verwende Base64-Daten direkt als Data-URL
          const base64Image = result.images[0].base64Image;
          imageMap.set(result.personId, pictureUrl(result.images[0]?.id));
        }
      });
      this.profileImages.set(imageMap);
    });
  }

  getProfileImage(personId: number): string {
    return this.profileImages().get(personId) || 'assets/default-avatar.svg';
  }

  onMemoryFilterChange(event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
  }

  deletePerson(id: number){
    this.residentService.deletePerson(id).subscribe({
      next: () => {
        console.log(`Person mit ID ${id} wurde gelöscht.`);
        this.getAllResidents();

      },
      error: error => {
        window.location.reload();
        console.error("Löschen der Person fehlgeschlagen." + error.name);
      }
    });
  }

  
}
