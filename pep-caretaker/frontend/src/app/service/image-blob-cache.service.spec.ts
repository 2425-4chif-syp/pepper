import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ImageBlobCacheService } from './image-blob-cache.service';

describe('ImageBlobCacheService', () => {
  let service: ImageBlobCacheService;
  let http: HttpTestingController;

  const URL_A = '/api/image/picture/1';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ImageBlobCacheService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  function flush(url: string): void {
    http.expectOne(url).flush(new Blob(['x'], { type: 'image/jpeg' }));
  }

  it('lädt eine URL nur einmal, auch bei parallelen Anfragen', () => {
    const seen: string[] = [];
    service.acquire(URL_A).subscribe(objectUrl => seen.push(objectUrl));
    service.acquire(URL_A).subscribe(objectUrl => seen.push(objectUrl));

    // expectOne schlägt fehl, sobald ein zweiter Request rausgegangen wäre
    flush(URL_A);

    expect(seen.length).toBe(2);
    expect(seen[0]).toBe(seen[1]);
  });

  it('liefert nach dem Freigeben aus dem Cache statt neu zu laden', () => {
    let first = '';
    service.acquire(URL_A).subscribe(objectUrl => (first = objectUrl));
    flush(URL_A);
    service.release(URL_A);

    // Das ist der Fall "Filter angeklickt": die Kacheln werden neu gebunden.
    let second = '';
    service.acquire(URL_A).subscribe(objectUrl => (second = objectUrl));

    http.expectNone(URL_A);
    expect(second).toBe(first);
  });

  it('erlaubt nach einem Fehler einen neuen Versuch', () => {
    let failed = false;
    service.acquire(URL_A).subscribe({ error: () => (failed = true) });
    http.expectOne(URL_A).error(new ProgressEvent('error'), { status: 500, statusText: 'Server Error' });
    expect(failed).toBeTrue();

    let objectUrl = '';
    service.acquire(URL_A).subscribe(value => (objectUrl = value));
    flush(URL_A);
    expect(objectUrl).toBeTruthy();
  });

  it('lädt ein verworfenes Bild beim nächsten Mal erneut', () => {
    service.acquire(URL_A).subscribe();
    flush(URL_A);
    service.release(URL_A);

    service.invalidate(URL_A);

    service.acquire(URL_A).subscribe();
    flush(URL_A);
  });

  it('lehnt eine HTML-Antwort ab, obwohl der Status 200 ist', () => {
    // Genau das passiert, wenn /imagor/ nicht proxyt wird: der dev-server liefert index.html
    // mit Status 200. Ohne Prüfung landet das HTML als Blob im <img> und bleibt leer.
    let failed = false;
    service.acquire(URL_A).subscribe({ error: () => (failed = true) });
    http.expectOne(URL_A).flush(new Blob(['<!doctype html>'], { type: 'text/html' }));
    expect(failed).toBeTrue();
  });

  it('lehnt eine JSON-Fehlerantwort mit Status 200 ab', () => {
    let failed = false;
    service.acquire(URL_A).subscribe({ error: () => (failed = true) });
    http.expectOne(URL_A).flush(new Blob(['{"message":"nope"}'], { type: 'application/json' }));
    expect(failed).toBeTrue();
  });

  it('akzeptiert eine Antwort ohne Content-Type', () => {
    let objectUrl = '';
    service.acquire(URL_A).subscribe(value => (objectUrl = value));
    http.expectOne(URL_A).flush(new Blob(['binary']));
    expect(objectUrl).toBeTruthy();
  });

  it('startet höchstens sechs Requests gleichzeitig', () => {
    const urls = Array.from({ length: 10 }, (_, i) => `/api/image/picture/${i + 10}`);
    urls.forEach(url => service.acquire(url).subscribe());

    expect(http.match(() => true).length).toBe(6);
  });
});
