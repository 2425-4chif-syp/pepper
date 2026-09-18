import { Component } from '@angular/core';

@Component({
  selector: 'app-image-overview',
  standalone: true,
  imports: [],
  templateUrl: './image-overview.component.html',
  styleUrl: './image-overview.component.css'
})
export class ImageOverviewComponent {
  public images: { image: string; name: string }[] = []
}
