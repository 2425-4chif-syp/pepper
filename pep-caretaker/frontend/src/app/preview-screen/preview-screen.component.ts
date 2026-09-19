// ...existing code...
import { Component, inject, signal, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { AuthSrcDirective } from '../auth-src.directive';
import { pictureUrl } from '../image-url';
import { Tas } from '../models/tas.model';
import { PreviewService } from '../service/preview.service';
import { ActivatedRoute, Params, RouterLink } from '@angular/router';
import { ImageServiceService } from '../service/image-service.service';
import { ImageDto } from '../models/imageDto.model';
import { SlicePipe } from '@angular/common';
import { ImagePreview } from '../models/image-preview.model';
import { ImageResponse } from '../models/image-response.model';

@Component({
  selector: 'app-preview-screen',
  imports: [AuthSrcDirective, RouterLink],
  templateUrl: './preview-screen.component.html',
  styleUrl: './preview-screen.component.css',
  standalone: true
})
export class PreviewScreenComponent implements OnInit, OnDestroy {
  @ViewChild('videoPlayer') videoPlayer!: ElementRef<HTMLVideoElement>;
  
  steps = signal<Tas[]>([]);
  previewService = inject(PreviewService);
  activatedRoute = inject(ActivatedRoute);
  actId = 0;

  currentIndex = signal<number>(0);
  isPlaying = signal<boolean>(true);
  progress = signal<number>(0);
  remainingSeconds = signal<number>(0);
  isMuted = signal<boolean>(false); // 🔇 Mute State
  // Chromium unter Linux hat ohne --enable-speech-dispatcher keine Stimmen → Sprachausgabe bleibt stumm
  noVoices = signal<boolean>(false);

  private sceneTimer: any = null;
  private progressTimer: any = null;

  // 🆕 Für Pause/Resume des Progress Trackings
  private sceneDurationMs = 0;
  private sceneStartTime = 0;
  private pausedElapsedTime = 0;

  // 🔊 Text-to-Speech
  private speechSynthesis: SpeechSynthesis = window.speechSynthesis;
  private currentUtterance?: SpeechSynthesisUtterance;

  // 🆕 Neue Properties für intelligentes Pause/Resume
  private currentTextToSpeak = '';
  private spokenCharacterIndex = 0;
  private speechStartTime = 0;
  private averageWordsPerMinute = 150; // Durchschnittliche Sprechgeschwindigkeit
  private isSpeechPausing = false; // Flag um zu verhindern, dass onend die Position überschreibt

  imageService = inject(ImageServiceService);
  readonly pictureUrl = pictureUrl;
  image = signal<ImagePreview | null>(null);

  getImage(id: number): void {
    if (!id) return;
    this.imageService.getImageById2(id).subscribe({
      next: (data) => {
        this.image.set(data);
      },
      error: (error) => {
        console.error('Error fetching image:', error);
      }
    });
  }

  loadCurrentImage(): void {
    const scene = this.currentScene;
    const imgId = this.image()?.id;
    if (imgId) {
      this.getImage(imgId);
    } else {
      this.image.set(null);
    }
  }

  togglePlay(): void {
    const video = this.videoPlayer?.nativeElement;
    if (!video) return;

    this.isPlaying.update(v => !v);
    
    if (this.isPlaying()) {
      // ▶️ RESUME: Nur fortsetzen, nicht neu starten
      video.play();
      this.resumeSpeech();
      
      // Starte Timer wieder (nur wenn noch nicht am Ende)
      if (!this.ended()) {
        this.resumeProgressTracking();
      }
    } else {
      // ⏸️ PAUSE: Anhalten
      video.pause();
      this.pauseSpeech();
      this.pauseProgressTracking();
    }
  }

  get currentSceneIndex(): number {
    return this.currentIndex();
  }

  openPreview(imgId: number | undefined): void {
    console.log('open preview for image id', imgId);
  }
  
  ngOnInit(){
    this.checkVoices();
    this.activatedRoute.params.subscribe(
      (params: Params) => {
        this.actId = Number(params['id']);
        this.loadSteps(this.actId);
      }
    );
  }

  ngOnDestroy(): void {
    clearTimeout(this.voiceCheckTimer);
    this.speechSynthesis.onvoiceschanged = null;
    this.clearTimers();
    this.stopSpeech(); // 🔇 Stoppe Sprachausgabe beim Verlassen
  }

  loadSteps(id: number){
    this.previewService.getTagalongStory(id).subscribe({
      next: (data: Tas | Tas[]) => {
        const arr = Array.isArray(data) ? data : [data];
        this.steps.set(arr);
        this.currentIndex.set(0);
        this.ended.set(false);
        this.loadCurrentImage();
        if (this.steps().length > 0 && this.isPlaying()) {
          this.startScene();
        }
      },
      error: (error) => {
        console.error('Error fetching steps:', error);
      }
    });
  }
  ended = signal<boolean>(false);

  startScene(): void {
    if (this.ended()) return;

    this.clearTimers();
    this.progress.set(0);
    this.pausedElapsedTime = 0; // Reset bei neuer Szene

    const scene = this.currentScene;
    console.log('Starte Szene:', this.currentScene?.image?.id, scene);
    if (!scene || !this.isPlaying()) return;

    if (this.videoPlayer?.nativeElement) {
      this.videoPlayer.nativeElement.load();
      this.videoPlayer.nativeElement.play();
    }

    // 🔊 Text vorlesen (von Anfang an)
    this.currentTextToSpeak = scene.text || '';
    this.spokenCharacterIndex = 0;
    this.speakText(this.currentTextToSpeak, 0);

    let durationMs = scene.durationInSeconds ?? 5000;
    if (durationMs < 1000) durationMs = durationMs * 1000;
    
    this.sceneDurationMs = durationMs;
    this.sceneStartTime = performance.now();

    // Set initial remaining seconds
    const totalSeconds = Math.floor(durationMs / 1000);
    this.remainingSeconds.set(totalSeconds);

    this.progressTimer = setInterval(() => {
      const elapsed = performance.now() - this.sceneStartTime;
      const progress = Math.min(elapsed / durationMs, 1);
      this.progress.set(progress);
      
      // Update remaining seconds
      const remainingSecs = Math.ceil((durationMs - elapsed) / 1000);
      this.remainingSeconds.set(Math.max(0, remainingSecs));
    }, 100);

    this.sceneTimer = setTimeout(() => {
      this.nextScene();
    }, durationMs);
  }

  nextScene(): void {
    this.clearTimers();
    this.stopSpeech(); // 🔇 Stoppe aktuelle Sprachausgabe
    if (this.videoPlayer?.nativeElement) {
      this.videoPlayer.nativeElement.pause();
    }
    const last = this.steps().length - 1;
    if (this.currentIndex() < last) {
      this.currentIndex.update(i => i + 1);
      this.ended.set(false);
      this.loadCurrentImage();
      if (this.isPlaying()) this.startScene();
    } else {
      this.ended.set(true);
      this.isPlaying.set(false);
      this.progress.set(1);
    }
  }

  prevScene(): void {
    this.clearTimers();
    this.stopSpeech(); // 🔇 Stoppe aktuelle Sprachausgabe
    if (this.ended()) this.ended.set(false);
    if (this.currentIndex() > 0) {
      this.currentIndex.update(i => i - 1);
    } else {
      this.currentIndex.set(Math.max(0, this.steps().length - 1));
    }
    this.loadCurrentImage();
    if (this.isPlaying()) this.startScene();
  }

  restartStory(): void {
    this.clearTimers();
    this.stopSpeech(); // 🔇 Stoppe aktuelle Sprachausgabe
    this.ended.set(false);
    this.currentIndex.set(0);
    this.progress.set(0);
    this.isPlaying.set(true);
    this.loadCurrentImage();
    this.startScene();
  }

  private clearTimers(): void {
    if (this.sceneTimer) { clearTimeout(this.sceneTimer); this.sceneTimer = null; }
    if (this.progressTimer) { clearInterval(this.progressTimer); this.progressTimer = null; }
  }

  get currentImageUrl(): string {
    const imageId = this.currentScene?.image?.id;
    if (!imageId) return '/assets/images/defaultIcon2.jpg';
    
    return pictureUrl(imageId);
  }

  // ⏸️ Pause Progress Tracking
  private pauseProgressTracking(): void {
    // Speichere die bereits verstrichene Zeit
    this.pausedElapsedTime = performance.now() - this.sceneStartTime;
    
    // Stoppe Timer
    if (this.sceneTimer) { clearTimeout(this.sceneTimer); this.sceneTimer = null; }
    if (this.progressTimer) { clearInterval(this.progressTimer); this.progressTimer = null; }
    
        console.log('Pciture:', this.currentScene?.image?.id);
    console.log('⏸️ Progress pausiert bei:', this.pausedElapsedTime, 'ms');
  }

  // ▶️ Resume Progress Tracking
  private resumeProgressTracking(): void {
    if (this.sceneDurationMs === 0) return;
    
    // Berechne verbleibende Zeit
    const remainingMs = this.sceneDurationMs - this.pausedElapsedTime;
    
    if (remainingMs <= 0) {
      this.nextScene();
      return;
    }
    
    // Setze neue Startzeit unter Berücksichtigung der bereits verstrichenen Zeit
    this.sceneStartTime = performance.now() - this.pausedElapsedTime;
    
    console.log('▶️ Progress fortgesetzt. Verbleibend:', remainingMs, 'ms');

    // Starte Progress Timer wieder
    this.progressTimer = setInterval(() => {
      const elapsed = performance.now() - this.sceneStartTime;
      const progress = Math.min(elapsed / this.sceneDurationMs, 1);
      this.progress.set(progress);
      
      // Update remaining seconds
      const remainingSecs = Math.ceil((this.sceneDurationMs - elapsed) / 1000);
      this.remainingSeconds.set(Math.max(0, remainingSecs));
    }, 100);

    // Starte Scene Timer mit verbleibender Zeit
    this.sceneTimer = setTimeout(() => {
      this.nextScene();
    }, remainingMs);
  }

  // 🔊 Text-to-Speech Methoden
  private speakText(text: string, startFromIndex: number = 0): void {
    // Stoppe vorherige Sprachausgabe (aber leere nicht currentTextToSpeak!)
    if (this.speechSynthesis.speaking) {
      this.isSpeechPausing = true;
      this.speechSynthesis.cancel();
    }
    this.currentUtterance = undefined;

    // Wenn stumm geschaltet, nicht vorlesen
    if (this.isMuted() || !text || text.trim() === '') {
      return;
    }

    // Schneide Text ab der gewünschten Position
    const textToSpeak = startFromIndex > 0 ? text.substring(startFromIndex) : text;

    if (textToSpeak.trim() === '') {
      return;
    }

    // Erstelle neue Sprachausgabe
    this.currentUtterance = new SpeechSynthesisUtterance(textToSpeak);
    
    // Konfiguration für deutsche Sprache
    this.currentUtterance.lang = 'de-DE'; // Deutsch
    const germanVoice = this.speechSynthesis.getVoices().find(v => v.lang.toLowerCase().startsWith('de'));
    if (germanVoice) this.currentUtterance.voice = germanVoice;
    this.currentUtterance.rate = 0.9; // Sprechgeschwindigkeit (0.1 bis 10)
    this.currentUtterance.pitch = 1; // Tonhöhe (0 bis 2)
    this.currentUtterance.volume = 1; // Lautstärke (0 bis 1)

    // 🆕 Tracking: Merke Startzeit
    this.speechStartTime = Date.now();
    this.isSpeechPausing = false; // Reset Flag

    // 🆕 Event: Beim Sprechen Position tracken (für jedes Wort)
    this.currentUtterance.onboundary = (event) => {
      if (event.name === 'word') {
        this.spokenCharacterIndex = startFromIndex + event.charIndex;
        console.log('📍 Wort-Position:', this.spokenCharacterIndex);
      }
    };

    // 🆕 Event: Am Ende - nur Position setzen wenn NICHT pausiert wird
    this.currentUtterance.onend = () => {
      if (!this.isSpeechPausing) {
        this.spokenCharacterIndex = this.currentTextToSpeak.length;
        console.log('✅ Sprachausgabe natürlich beendet');
      }
    };

    this.currentUtterance.onerror = (event) => {
      // 'interrupted'/'canceled' kommen von Pause/Szenenwechsel und sind erwartet
      if (event.error !== 'interrupted' && event.error !== 'canceled') {
        console.warn('🔇 Sprachausgabe fehlgeschlagen:', event.error,
          '| Stimmen verfügbar:', this.speechSynthesis.getVoices().length);
      }
    };

    // Starte Sprachausgabe. Chrome verwirft speak() direkt nach cancel() manchmal stillschweigend,
    // daher kurz verzögern und prüfen, dass die Äußerung inzwischen nicht ersetzt wurde.
    const utterance = this.currentUtterance;
    setTimeout(() => {
      if (this.currentUtterance === utterance) {
        this.speechSynthesis.speak(utterance);
      }
    }, 50);
    
    console.log('🔊 Spreche ab Position:', startFromIndex, '- Text:', textToSpeak.substring(0, 30) + '...');
  }

  // 🔇 Sprachausgabe stoppen (komplett, für Szenenwechsel)
  private stopSpeech(): void {
    if (this.speechSynthesis.speaking) {
      this.isSpeechPausing = true; // Verhindere dass onend die Position ändert
      this.speechSynthesis.cancel();
    }
    this.currentUtterance = undefined;
    this.spokenCharacterIndex = 0;
    this.currentTextToSpeak = ''; // Leere den Text (nur bei komplettem Stop!)
    this.isSpeechPausing = false; // Reset Flag
    console.log('🛑 Sprachausgabe komplett gestoppt');
  }

  // ⏸️ Neue Methode: Sprachausgabe pausieren
  private pauseSpeech(): void {
    if (this.speechSynthesis.speaking) {
      // Setze Flag BEVOR cancel() aufgerufen wird
      this.isSpeechPausing = true;
      
      // Schätze die Position basierend auf Zeit (als Fallback)
      const elapsedTime = Date.now() - this.speechStartTime;
      const estimatedCharsSpoken = Math.floor((elapsedTime / 1000) * (this.averageWordsPerMinute / 60) * 5); // ~5 chars pro Wort
      
      // WICHTIG: Bevorzuge die letzte bekannte Wort-Position (präziser!)
      // Nutze Zeit-Schätzung nur wenn keine Wort-Position getrackt wurde
      let pausedAt: number;
      if (this.spokenCharacterIndex > 0) {
        // Wir haben eine Wort-Position - nutze diese + ein kleiner Puffer für das nächste Wort
        const wordEndPosition = this.spokenCharacterIndex;
        // Suche das nächste Wort (nach Leerzeichen)
        const nextSpaceIndex = this.currentTextToSpeak.indexOf(' ', wordEndPosition);
        pausedAt = nextSpaceIndex > 0 ? nextSpaceIndex + 1 : wordEndPosition;
      } else {
        // Kein Wort-Tracking - nutze Zeit-Schätzung
        pausedAt = estimatedCharsSpoken;
      }
      
      // Stoppe Sprachausgabe (triggert onend Event, aber Flag verhindert Überschreiben!)
      this.speechSynthesis.cancel();
      
      // Setze Position NACH cancel()
      this.spokenCharacterIndex = Math.min(pausedAt, Math.max(0, this.currentTextToSpeak.length - 1));
      
      // WICHTIG: Leere currentTextToSpeak NICHT - wir brauchen es für Resume!
      
      console.log('⏸️ Sprachausgabe pausiert bei Position:', this.spokenCharacterIndex, '/', this.currentTextToSpeak.length);
      console.log('⏸️ Pausierter Text:', this.currentTextToSpeak.substring(0, 50) + '...');
      console.log('⏸️ Verbleibender Text:', this.currentTextToSpeak.substring(this.spokenCharacterIndex, this.spokenCharacterIndex + 30) + '...');
    } else {
      // Noch nicht gestartete (verzögerte) Äußerung verwerfen
      this.currentUtterance = undefined;
      console.log('⏸️ Keine aktive Sprachausgabe zum Pausieren');
    }
  }

  // ▶️ Neue Methode: Sprachausgabe fortsetzen
  private resumeSpeech(): void {
    console.log('🔍 Resume Speech Check:', {
      hasText: !!this.currentTextToSpeak,
      textLength: this.currentTextToSpeak?.length,
      charIndex: this.spokenCharacterIndex,
      isMuted: this.isMuted()
    });

    if (!this.currentTextToSpeak) {
      console.warn('⚠️ Kein Text zum Fortsetzen vorhanden');
      return;
    }

    if (this.isMuted()) {
      console.log('🔇 Stumm geschaltet - kein Resume');
      return;
    }

    if (this.spokenCharacterIndex >= this.currentTextToSpeak.length) {
      console.log('✅ Text bereits komplett gesprochen');
      return;
    }

    console.log('▶️ Setze Sprachausgabe fort ab Position:', this.spokenCharacterIndex);
    this.speakText(this.currentTextToSpeak, this.spokenCharacterIndex);
  }

  // 🔇🔊 Mute/Unmute Toggle
  toggleMute(): void {
    this.isMuted.update(v => !v);
    
    if (this.isMuted()) {
      // Wenn stumm geschaltet, pausiere aktuelle Sprachausgabe
      this.pauseSpeech();
      console.log('🔇 Stumm geschaltet');
    } else {
      // Wenn Stummschaltung aufgehoben, setze Sprachausgabe fort
      if (this.isPlaying()) {
        this.resumeSpeech();
      }
      console.log('🔊 Laut geschaltet');
    }
  }

  private voiceCheckTimer?: ReturnType<typeof setTimeout>;

  // Stimmen werden asynchron geladen: erst nach kurzer Wartezeit als "keine vorhanden" werten
  private checkVoices(): void {
    if (!('speechSynthesis' in window)) {
      this.noVoices.set(true);
      return;
    }
    const update = () => this.noVoices.set(this.speechSynthesis.getVoices().length === 0);
    this.speechSynthesis.onvoiceschanged = update;
    this.voiceCheckTimer = setTimeout(update, 1500);
  }

  get currentScene(): Tas | null {
    return this.steps()[this.currentIndex()] || null;
  }
}