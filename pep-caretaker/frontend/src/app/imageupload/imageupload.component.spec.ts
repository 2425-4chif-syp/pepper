import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImageuploadComponent } from './imageupload.component';
import { testProviders } from '../../testing/test-providers';
import { ImageServiceService } from '../service/image-service.service';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

describe('ImageuploadComponent', () => {
  let component: ImageuploadComponent;
  let fixture: ComponentFixture<ImageuploadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ImageuploadComponent],
      providers: [...testProviders]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ImageuploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Pflichtfeld Bezeichnung', () => {
    /**
     * Ein zugeschnittenes Bild vortäuschen, sonst ist Speichern ohnehin nicht möglich.
     * Der Platzhalter braucht destroy(), weil die Komponente den Cropper beim Abbau aufräumt.
     */
    function withCroppedImage(): void {
      // Erst mit gesetzter imageUrl rendert das Formular überhaupt
      component.imageUrl.set('blob:test');
      component.cropSize.set({ width: 1280, height: 800 });
      (component as any).cropper = { destroy: () => {} };
      fixture.detectChanges();
    }

    it('meldet eine fehlende Bezeichnung', () => {
      withCroppedImage();
      expect(component.descriptionMissing()).toBeTrue();
      expect(component.canSave()).toBeFalse();
    });

    it('lässt den Speichern-Button klickbar, damit der Fehler gezeigt werden kann', () => {
      withCroppedImage();
      // Ein deaktivierter Button könnte die Markierung nie auslösen
      expect(component.canAttemptSave()).toBeTrue();

      const button: HTMLButtonElement = fixture.nativeElement.querySelector('button[type="submit"]');
      expect(button.disabled).toBeFalse();
    });

    it('markiert das Feld beim Speichern ohne Bezeichnung und lädt nichts hoch', () => {
      withCroppedImage();
      const upload = spyOn(TestBed.inject(ImageServiceService), 'uploadImage');

      component.save();
      fixture.detectChanges();

      expect(component.descriptionInvalid()).toBeTrue();
      expect(upload).not.toHaveBeenCalled();

      const input: HTMLInputElement = fixture.nativeElement.querySelector('#description');
      expect(input.classList).toContain('field-error');
      expect(input.getAttribute('aria-invalid')).toBe('true');
      expect(input.getAttribute('aria-describedby')).toBe('description-error');
    });

    it('zeigt eine Fehlermeldung, die Screenreader ansagen', () => {
      withCroppedImage();
      component.save();
      fixture.detectChanges();

      const error: HTMLElement = fixture.nativeElement.querySelector('#description-error');
      expect(error).toBeTruthy();
      expect(error.getAttribute('role')).toBe('alert');
      expect(error.textContent).toContain('Bezeichnung');
    });

    it('setzt den Fokus in das leere Feld', () => {
      withCroppedImage();
      component.save();
      fixture.detectChanges();

      const input: HTMLInputElement = fixture.nativeElement.querySelector('#description');
      expect(document.activeElement).toBe(input);
    });

    it('nimmt die Markierung zurück, sobald etwas eingegeben wird', () => {
      withCroppedImage();
      component.save();
      fixture.detectChanges();
      expect(component.descriptionInvalid()).toBeTrue();

      component.description.set('Ausflug in den Tierpark');
      component.onDescriptionInput();
      fixture.detectChanges();

      expect(component.descriptionInvalid()).toBeFalse();
      expect(component.canSave()).toBeTrue();

      const input: HTMLInputElement = fixture.nativeElement.querySelector('#description');
      expect(input.classList).not.toContain('field-error');
      expect(fixture.nativeElement.querySelector('#description-error')).toBeNull();
    });

    it('markiert nichts, wenn Leerzeichen als Bezeichnung eingegeben werden', () => {
      withCroppedImage();
      component.description.set('   ');
      fixture.detectChanges();

      expect(component.descriptionMissing()).toBeTrue();
      expect(component.canSave()).toBeFalse();
    });
  });
});

/**
 * Die Seite dient zwei Zwecken: normaler Bild-Upload und Bildauswahl für den
 * Geschichten-Editor. Welcher gilt, steht in der Route - nicht im sessionStorage.
 */
describe('ImageuploadComponent Betriebsart', () => {
  /** Baut die Komponente so, als wäre sie mit diesen Query-Parametern aufgerufen worden. */
  async function createWith(queryParams: Record<string, string>): Promise<ImageuploadComponent> {
    TestBed.resetTestingModule();
    await TestBed.configureTestingModule({
      imports: [ImageuploadComponent],
      providers: [
        ...testProviders,
        // nach testProviders, damit diese ActivatedRoute gewinnt
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParamMap: convertToParamMap(queryParams) } },
        },
      ],
    }).compileComponents();

    return TestBed.createComponent(ImageuploadComponent).componentInstance;
  }

  /** Der Editor legt diesen Eintrag an, bevor er zur Upload-Seite wechselt. */
  function withPendingStory(imageType: 'title' | 'scene'): void {
    sessionStorage.setItem(
      'pendingStoryState',
      JSON.stringify({ titleName: 'Test', scenes: [], returnTo: 'createstory', imageType }),
    );
  }

  afterEach(() => sessionStorage.clear());

  it('ist normaler Upload, wenn die Route nichts anderes sagt', async () => {
    const component = await createWith({});
    expect(component.storyImageType).toBeNull();
    expect(component.pageTitle).toBe('Bild hochladen');
  });

  it('bleibt normaler Upload, obwohl noch ein Story-Entwurf herumliegt', async () => {
    // Genau der gemeldete Fall: Geschichte angefangen, Bild anfordern, dann über das Logo
    // zur Startseite - der Entwurf bleibt liegen. Der nächste Aufruf über "Bilder" darf
    // davon nichts mitbekommen.
    withPendingStory('title');

    const component = await createWith({});

    expect(component.storyImageType).toBeNull();
    expect(component.pageTitle).toBe('Bild hochladen');
  });

  it('wählt das Titelbild, wenn die Route es verlangt', async () => {
    withPendingStory('title');
    const component = await createWith({ for: 'title' });
    expect(component.storyImageType).toBe('title');
    expect(component.pageTitle).toBe('Titelbild wählen');
  });

  it('wählt das Szenenbild, wenn die Route es verlangt', async () => {
    withPendingStory('scene');
    const component = await createWith({ for: 'scene' });
    expect(component.storyImageType).toBe('scene');
    expect(component.pageTitle).toBe('Szenenbild wählen');
  });

  it('ignoriert die Betriebsart ohne zugehörigen Story-Entwurf', async () => {
    // Ohne Entwurf gäbe es beim Speichern nichts, wohin das Bild zurückfliessen könnte
    const component = await createWith({ for: 'title' });
    expect(component.storyImageType).toBeNull();
  });

  it('ignoriert einen unbekannten Wert', async () => {
    withPendingStory('title');
    const component = await createWith({ for: 'irgendwas' });
    expect(component.storyImageType).toBeNull();
  });

  it('verlangt im normalen Upload eine Bezeichnung, im Story-Modus nicht', async () => {
    const normal = await createWith({});
    expect(normal.descriptionMissing()).toBeTrue();

    withPendingStory('title');
    const story = await createWith({ for: 'title' });
    expect(story.descriptionMissing()).toBeFalse();
  });
});
