export interface ITagalongStory {
  id: number;
  name: string;
  icon: string;
  storyIconBase64?: string; // Fallback für alte Base64-Daten
  storyIcon?: { 
    id: number;
    description?: string; 
  }; // Referenz auf Image-Server
  gameType: IGameType;
  enabled: boolean;
}

export interface IStep {
  id?: number;
  duration: number;
  move?: string | IMove;
  text: string;
  image: string | IImage;
  index: number;
  game?: number | null;
}

export interface IGameType{
  id: string;
  name: string;
}

export interface IMove{
  id: number;
  name: string;
  description: string;
}

export interface IImage{
  person: string;
  image: string;
  url: string;
  description: string;
}

export class MoveHandler {
  // Define private properties dynamically
  private movesMap: { [key: string]: IMove } = {};

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

  constructor() {
    this.generateMoves();
  }

  private generateMoves(): void {
    this.moves.forEach((move, index) => {
      const moveName = this.moveNames[index];
      this.movesMap[move] = {
        id: index + 1, // Assign unique ID (starting from 1)
        name: move,
        description: moveName,
      };
    });
  }

  // Method to retrieve a move by name
  public getMove(name: string): IMove | undefined {
    return this.movesMap[name];
  }
}

export class StepHandler {
  
}