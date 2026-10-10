import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { NotificheService } from '../../shared/notifiche.service';
import { AuthService } from './auth.service';
import { Area, Azione } from './permessi';

/** Lascia passare solo chi è entrato; gli altri vanno al login e poi tornano alla pagina richiesta. */
export const authGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAuthenticated()) {
    return true;
  }

  const queryParams = state.url !== '/' ? { redirect: state.url } : {};
  return router.createUrlTree(['/login'], { queryParams });
};

/** Per la pagina di login: chi è già entrato viene mandato alla home. */
export const guestGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  return auth.isAuthenticated() ? router.createUrlTree(['/']) : true;
};

/** Lascia aprire la pagina solo a chi ha il permesso sull'area (altrimenti messaggio e ritorno alla home). */
export const permessoGuard =
  (area: Area, azione: Azione = 'leggere'): CanActivateFn =>
  () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const notifiche = inject(NotificheService);

    const consentito = azione === 'scrivere' ? auth.puoScrivere(area) : auth.puoLeggere(area);
    if (consentito) {
      return true;
    }

    notifiche.errore('Non hai i permessi per aprire questa pagina.');
    return router.createUrlTree(['/']);
  };
