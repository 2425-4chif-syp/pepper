import { ApplicationConfig, provideZoneChangeDetection, InjectionToken } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';
import { provideHttpClient, withFetch, withInterceptors } from "@angular/common/http";
import { environment } from '../environments/environment';
import { authInterceptor } from './auth.interceptor';
export const STORY_URL= new InjectionToken<string>('STORY_URL');
export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }),
  {
    provide: STORY_URL,
    useValue: environment.apiUrl
  },
  provideRouter(routes), provideClientHydration(), provideHttpClient(withFetch(), withInterceptors([authInterceptor]))]
};
