import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { RoleService } from '../role.service';

interface HomeTile {
  title: string;
  description: string;
  route: string;
  image: string;
  accent: string;
}

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css'
})
export class HomePageComponent implements OnInit {
  tiles: HomeTile[] = [];

  // md: Kacheln als Zeilen über die Bildschirmhöhe, ab lg nebeneinander
  // (volle Klassennamen, damit Tailwind sie findet)
  get gridCols(): string {
    return ['lg:grid-cols-1', 'lg:grid-cols-2', 'lg:grid-cols-3'][Math.min(this.tiles.length, 3) - 1] ?? '';
  }
  userName = '';

  constructor(private roleService: RoleService, private router: Router) {}

  ngOnInit() {
    // Residents sollten nie hier ankommen (AuthGuard blockt) – zur Sicherheit trotzdem umleiten
    if (this.isResident()) {
      this.router.navigate(['/my-pictures']);
      return;
    }

    const userInfo = this.roleService.getUserInfo();
    this.userName = userInfo?.preferred_username || '';

    const staff = this.isAdmin() || this.isCaretaker();
    this.tiles = [
      staff && {
        title: 'Mitmachgeschichten',
        description: 'Geschichten erstellen, bearbeiten und für Pepper freigeben.',
        route: '/tagalongstory',
        image: 'assets/images/TagAlong.jpg',
        accent: 'border-t-success',
      },
      this.isAdmin() && {
        title: 'Bewohner',
        description: 'Bewohnerinnen und Bewohner anlegen und verwalten.',
        route: '/residents',
        image: 'assets/images/PersonenEintrag.png',
        accent: 'border-t-info',
      },
      staff && {
        title: 'Bilder',
        description: 'Fotos hochladen, ansehen und herunterladen.',
        route: '/pictures',
        image: 'assets/images/Bilder.png',
        accent: 'border-t-error',
      },
    ].filter((t): t is HomeTile => !!t);
  }

  isAdmin(): boolean {
    return this.roleService.hasRole('admin');
  }

  isCaretaker(): boolean {
    return this.roleService.hasRole('caretaker');
  }

  isResident(): boolean {
    return this.roleService.isResident();
  }
}
