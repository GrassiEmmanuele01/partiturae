import { ApplicationConfig, inject, provideAppInitializer, provideZonelessChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';

import { routes } from './app.routes';
import { authInterceptor } from './features/auth/auth.interceptor';
import { AuthService } from './features/auth/auth.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideHttpClient(withFetch(), withInterceptors([authInterceptor])),
    // Prima di mostrare qualsiasi pagina si prova a ripristinare la sessione (se c'è un cookie valido).
    provideAppInitializer(() => inject(AuthService).restoreSession())
  ]
};