import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { Component } from '@angular/core';
import { provideLocationMocks } from '@angular/common/testing';
import { routes } from './app.routes';
import { AuthGuard } from './auth.guard';

@Component({ standalone: true, template: '' })
class BlankComponent {}

/**
 * Prüft die Rückfallroute. Die echten Guards und Keycloak bleiben aussen vor - hier geht es
 * nur darum, dass eine unbekannte URL auf der Startseite landet und nicht im Nichts.
 */
describe('app routes', () => {
  let router: Router;

  beforeEach(async () => {
    // Ziel-Komponenten durch eine leere ersetzen, damit kein Lazy-Chunk geladen werden muss
    const testRoutes = routes.map(route =>
      route.redirectTo ? route : { ...route, loadComponent: undefined, component: BlankComponent },
    );

    TestBed.configureTestingModule({
      providers: [
        provideRouter(testRoutes),
        provideLocationMocks(),
        { provide: AuthGuard, useValue: { canActivate: () => true } },
      ],
    });

    router = TestBed.inject(Router);
  });

  it('leitet unbekannte URLs auf die Startseite um', async () => {
    await router.navigate(['/gibt-es-nicht']);
    expect(router.url).toBe('/');
  });

  it('leitet auch tief verschachtelte unbekannte URLs um', async () => {
    await router.navigate(['/foo/bar/baz']);
    expect(router.url).toBe('/');
  });

  it('lässt bekannte Routen unangetastet', async () => {
    await router.navigate(['/pictures']);
    expect(router.url).toBe('/pictures');
  });

  it('hat die Rückfallroute an letzter Stelle', () => {
    expect(routes[routes.length - 1].path).toBe('**');
  });
});
