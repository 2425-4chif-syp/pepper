import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { EnvironmentProviders, Provider } from '@angular/core';
import { of } from 'rxjs';
import { STORY_URL } from '../app/app.config';

/**
 * Abhängigkeiten, die fast jede Komponente dieser App zum Erzeugen braucht.
 *
 * Die Specs sind Gerüst-Specs ("should create") und hatten diese Provider nie - ohne sie
 * scheitert schon `TestBed.createComponent` an einem `NullInjectorError`. Hier stehen sie
 * einmal, damit jeder Spec nur `providers: [...testProviders]` ergänzen muss.
 */
export const testProviders: (Provider | EnvironmentProviders)[] = [
  provideHttpClient(),
  provideHttpClientTesting(),
  provideRouter([]),
  { provide: STORY_URL, useValue: '/api/' },
  {
    // Komponenten mit :id-Route lesen params - ausserhalb eines echten Routings gibt es die nicht
    provide: ActivatedRoute,
    useValue: {
      params: of({}),
      queryParams: of({}),
      paramMap: of(convertToParamMap({})),
      queryParamMap: of(convertToParamMap({})),
      snapshot: {
        params: {},
        queryParams: {},
        paramMap: convertToParamMap({}),
        queryParamMap: convertToParamMap({}),
      },
    },
  },
];
