import { Component } from '@angular/core';
import { IStep } from '../../models/tagalongstories.model';
import { FormsModule } from '@angular/forms';
import { MoveHandler } from '../../models/tagalongstories.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-addstep',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './addstep.component.html',
  styleUrl: './addstep.component.css'
})
export class AddstepComponent {
  public steps: IStep[] = [];
  public duration = [5, 10, 15];
  public moves = [
    'emote_hurra',
    'essen',
    'gehen',
    'hand_heben',
    'highfive_links',
    'highfive_rechts',
    'klatschen',
    'strecken',
    'umher_sehen',
    'winken',
  ];
  public moveNames = [
    'Hurra',
    'Essen',
    'Gehen',
    'Hand heben',
    'Highfive links',
    'Highfive rechts',
    'Klatschen',
    'Strecken',
    'Umher sehen',
    'Winken',
  ];

  // Nur im Dropdown ausgeblendet; moves/moveNames bleiben unverändert (MoveHandler mappt darüber)
  private readonly hiddenMoves = ['emote_hurra'];

  isMoveSelectable(move: string, current: unknown): boolean {
    return !this.hiddenMoves.includes(move) || move === current;
  }

  addNewStep(){
    this.steps.push({
      id: 0, // Temporary ID (can be updated later)
      duration: 0,
      image: 'https://fakeimg.pl/600x400?text=Bild+Hochladen',
      index: this.steps.length + 1, // Set index dynamically
      text: '',
      move: '',
      game: 0
    })
    
  }

  saveToDb(){
    const moveHandler = new MoveHandler();
    this.steps.forEach(item => {
      const move = moveHandler.getMove(item.move as string);
      if (move) {
        item.move = move;
      }
    });
    
    console.log(this.steps);
  }

  onFileSelected(event: Event, step: IStep) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      const reader = new FileReader();

      reader.onload = () => {
        step.image = reader.result as string; // Set base64 string as image source
      };

      reader.readAsDataURL(file);
    }
  }
}
