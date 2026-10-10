import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree, provideRouter } from '@angular/router';

import { NotificheService } from '../../shared/notifiche.service';
import { permessoGuard } from './auth.guard';
import { AuthService } from './auth.service';

describe('permessoGuard', () => {
  function esegui(auth: Partial<AuthService>, guard = permessoGuard('soci')) {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: AuthService, useValue: auth }]
    });
    return TestBed.runInInjectionContext(() => guard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot));
  }

  it('lascia passare chi ha il permesso', () => {
    const esito = esegui({ puoLeggere: () => true });
    expect(esito).toBe(true);
  });

  it('senza permesso torna alla home e avvisa', () => {
    const esito = esegui({ puoLeggere: () => false });
    const router = TestBed.inject(Router);

    expect(esito).toBeInstanceOf(UrlTree);
    expect(router.serializeUrl(esito as UrlTree)).toBe('/');
    expect(TestBed.inject(NotificheService).notifiche()[0].testo).toContain('permessi');
  });

  it('per le pagine di modifica controlla il permesso di scrittura', () => {
    const solaLettura = esegui({ puoLeggere: () => true, puoScrivere: () => false }, permessoGuard('soci', 'scrivere'));
    expect(solaLettura).toBeInstanceOf(UrlTree);
  });
});
