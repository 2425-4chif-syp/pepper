import { Routes } from '@angular/router';
import { AuthGuard } from './auth.guard';

// Alle Routen werden lazy geladen: jede Seite bringt ihr eigenes CSS mit,
// das soll nicht im Initial-Bundle landen.
export const routes: Routes = [
  // HomePage - nur für Admin & Caretaker (Residents werden zu /my-pictures umgeleitet)
  {path: '', loadComponent: () => import('./home-page/home-page.component').then(m => m.HomePageComponent), canActivate: [AuthGuard], data: { roles: ['admin', 'caretaker'] }},
  {path: 'home', loadComponent: () => import('./home-page/home-page.component').then(m => m.HomePageComponent), canActivate: [AuthGuard], data: { roles: ['admin', 'caretaker'] }},
  {path: 'tagalongstory', loadComponent: () => import('./tagalongstory/tagalongstory.component').then(m => m.TagalongstoryComponent), canActivate: [AuthGuard], data: { roles: ['admin', 'caretaker'] }},
  {path: 'previewScreen/:id', loadComponent: () => import('./preview-screen/preview-screen.component').then(m => m.PreviewScreenComponent), canActivate: [AuthGuard], data: { roles: ['admin', 'caretaker'] }},

  // Bewohner - nur eigene Bilder
  {path: 'my-pictures', loadComponent: () => import('./my-pictures/my-pictures.component').then(m => m.MyPicturesComponent), canActivate: [AuthGuard], data: { roles: ['resident'] }},

  // Story-Management - Admin & Caretaker
  {path: 'createstory', loadComponent: () => import('./createstory/createstory.component').then(m => m.CreatestoryComponent), canActivate: [AuthGuard], data: { roles: ['admin', 'caretaker'] }},
  {path: 'createstory/:id', loadComponent: () => import('./createstory/createstory.component').then(m => m.CreatestoryComponent), canActivate: [AuthGuard], data: { roles: ['admin', 'caretaker'] }},
  {path: 'editstory/:id', loadComponent: () => import('./editstory/editstory.component').then(m => m.EditstoryComponent), canActivate: [AuthGuard], data: { roles: ['admin', 'caretaker'] }},
  {path: 'addstep/:id', loadComponent: () => import('./addstep/addstep.component').then(m => m.AddstepComponent), canActivate: [AuthGuard], data: { roles: ['admin', 'caretaker'] }},

  // Bilder-Management - Admin & Caretaker
  {path: 'pictures', loadComponent: () => import('./picture-overview/picture-overview.component').then(m => m.PictureOverviewComponent), canActivate: [AuthGuard], data: { roles: ['admin', 'caretaker'] }},
  {path: 'imageUpload', loadComponent: () => import('./imageupload/imageupload.component').then(m => m.ImageuploadComponent), canActivate: [AuthGuard], data: { roles: ['admin', 'caretaker'] }},

  // Bewohner-Management - nur Admin
  {path: 'residents', loadComponent: () => import('./residents/residents.component').then(m => m.ResidentsComponent), canActivate: [AuthGuard], data: { roles: ['admin'] }},
  {path: 'residentDetails/:id', loadComponent: () => import('./resident-details/resident-details.component').then(m => m.ResidentDetailsComponent), canActivate: [AuthGuard], data: { roles: ['admin'] }},
  {path: 'residentAdd', loadComponent: () => import('./add-resident/add-resident.component').then(m => m.AddResidentComponent), canActivate: [AuthGuard], data: { roles: ['admin'] }},
  {path: 'person-entry', loadComponent: () => import('./person-entry/person-entry.component').then(m => m.PersonEntryComponent), canActivate: [AuthGuard], data: { roles: ['admin'] }},

  // Unbekannte URL -> zurück zur Startseite. Residents werden von dort
  // durch AuthGuard.checkAccess() weiter auf /my-pictures geschickt.
  {path: '**', redirectTo: ''}
];
