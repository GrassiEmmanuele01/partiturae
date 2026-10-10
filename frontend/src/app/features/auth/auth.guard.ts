import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from './auth.service';

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