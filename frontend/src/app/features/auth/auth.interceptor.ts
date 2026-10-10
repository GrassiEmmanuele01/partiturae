import { HttpErrorResponse, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';

import { environment } from '../../../environments/environment';
import { NotificheService } from '../../shared/notifiche.service';
import { AuthService } from './auth.service';

// Login, rinnovo e logout si autenticano con il cookie, non con il token.
const ENDPOINT_CON_COOKIE = /\/auth\/(login|refresh|logout)$/;

/**
 * Aggiunge il token a ogni richiesta verso l'API. Se il server risponde 401 (token scaduto)
 * prova a rinnovare la sessione una volta e ripete la richiesta; se non ci riesce, torna al login.
 * Se risponde 403 (operazione non permessa al tuo ruolo) lo comunica con un messaggio.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const notifiche = inject(NotificheService);

  if (!req.url.startsWith(environment.apiUrl) || ENDPOINT_CON_COOKIE.test(req.url)) {
    return next(req);
  }

  const conToken = (richiesta: HttpRequest<unknown>) => {
    const token = auth.accessToken();
    return token ? richiesta.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : richiesta;
  };

  return next(conToken(req)).pipe(
    catchError((errore: HttpErrorResponse) => {
      if (errore.status === 403) {
        notifiche.errore('Non hai i permessi per questa operazione.');
        return throwError(() => errore);
      }

      if (errore.status !== 401) {
        return throwError(() => errore);
      }

      return auth.refresh().pipe(
        // Solo un rinnovo fallito significa "sessione finita": gli errori della richiesta ripetuta passano così come sono.
        catchError(() => {
          auth.sessioneScaduta();
          return throwError(() => errore);
        }),
        switchMap(() => next(conToken(req)))
      );
    })
  );
};