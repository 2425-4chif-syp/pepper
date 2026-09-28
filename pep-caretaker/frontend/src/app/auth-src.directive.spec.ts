import { Component } from '@angular/core';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { AuthSrcDirective } from './auth-src.directive';
import { pictureUrl } from './image-url';
import { environment } from '../environments/environment';

@Component({
  standalone: true,
  imports: [AuthSrcDirective],
  template: '<img [appAuthSrc]="src" alt="test">',
})
class HostComponent {
  src = '';
}

describe('AuthSrcDirective', () => {
  let fixture: ComponentFixture<HostComponent>;
  let http: HttpTestingController;
  let realObserver: typeof IntersectionObserver;

  beforeEach(() => {
    // Im Test ist nichts wirklich sichtbar, der echte Observer würde also nie auslösen.
    // Der Stub meldet jedes beobachtete Element sofort als sichtbar.
    realObserver = window.IntersectionObserver;
    window.IntersectionObserver = class {
      constructor(private callback: IntersectionObserverCallback) {}
      observe(element: Element) {
        this.callback(
          [{ target: element, isIntersecting: true } as unknown as IntersectionObserverEntry],
          this as unknown as IntersectionObserver,
        );
      }
      unobserve() {}
      disconnect() {}
      takeRecords() { return []; }
    } as unknown as typeof IntersectionObserver;

    TestBed.configureTestingModule({
      imports: [HostComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    fixture = TestBed.createComponent(HostComponent);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    window.IntersectionObserver = realObserver;
    http.verify();
  });

  function imageBlob(): Blob {
    return new Blob(['x'], { type: 'image/jpeg' });
  }

  it('fällt auf den direkten Backend-Pfad zurück, wenn imagor scheitert', () => {
    if (!environment.imagor) {
      pending('imagor ist in dieser Umgebung abgeschaltet');
      return;
    }
    const url = pictureUrl(4, 400);
    fixture.componentInstance.src = url;
    fixture.detectChanges();

    // Erster Versuch geht an imagor und scheitert
    http.expectOne(url).error(new ProgressEvent('error'), { status: 403, statusText: 'Forbidden' });

    // Danach muss direkt beim Backend nachgeladen werden
    http.expectOne('/api/image/picture/4').flush(imageBlob());
  });

  it('fällt auch zurück, wenn imagor mit Status 200 HTML liefert', () => {
    if (!environment.imagor) {
      pending('imagor ist in dieser Umgebung abgeschaltet');
      return;
    }
    const url = pictureUrl(4, 400);
    fixture.componentInstance.src = url;
    fixture.detectChanges();

    http.expectOne(url).flush(new Blob(['<!doctype html>'], { type: 'text/html' }));
    http.expectOne('/api/image/picture/4').flush(imageBlob());
  });

  it('lädt nicht-API-URLs unverändert ohne HTTP-Aufruf', () => {
    fixture.componentInstance.src = 'assets/images/imageNotFound.png';
    fixture.detectChanges();

    http.expectNone(() => true);
    const img: HTMLImageElement = fixture.nativeElement.querySelector('img');
    expect(img.getAttribute('src')).toContain('imageNotFound.png');
  });
});
