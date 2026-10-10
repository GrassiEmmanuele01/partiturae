import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { environment } from '../../../environments/environment';
import { Account, AuthResponse } from './auth.model';
import { AuthService } from './auth.service';

const API = environment.apiUrl;

function account(extra: Partial<Account> = {}): Account {
  return {
    id: 1,
    email: 'mario@esempio.it',
    nome: 'Mario',
    cognome: 'Rossi',
    superadmin: false,
    deveCambiarePassword: false,
    bandaCorrente: { id: 1, nome: 'Banda A' },
    ruoli: ['ARCHIVISTA', 'MUSICISTA'],
    bande: [
      { id: 1, nome: 'Banda A' },
      { id: 2, nome: 'Banda B' }
    ],
    socioId: null,
    ...extra
  };
}

function risposta(token: string, conto: Account): AuthResponse {
  return { accessToken: token, tokenType: 'Bearer', expiresIn: 900, account: conto };
}

describe('AuthService con più bande', () => {
  let auth: AuthService;
  let backend: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()]
    });
    auth = TestBed.inject(AuthService);
    backend = TestBed.inject(HttpTestingController);
  });

  afterEach(() => backend.verify());

  function accedi(conto: Account): void {
    auth.login('mario@esempio.it', 'password').subscribe();
    backend.expectOne(`${API}/auth/login`).flush(risposta('token-A', conto));
  }

  it('i permessi seguono i ruoli della banda corrente', () => {
    accedi(account());

    expect(auth.bandaCorrente()?.nome).toBe('Banda A');
    expect(auth.puoScrivere('parti')).toBe(true); // ARCHIVISTA
    expect(auth.puoScrivere('calendario')).toBe(false);
  });

  it('cambiando banda si prendono i ruoli della nuova banda', () => {
    accedi(account());

    auth.cambiaBanda(2).subscribe();
    const richiesta = backend.expectOne(`${API}/auth/banda`);
    expect(richiesta.request.body).toEqual({ bandaId: 2 });
    expect(richiesta.request.withCredentials).toBe(true);
    richiesta.flush(risposta('token-B', account({ bandaCorrente: { id: 2, nome: 'Banda B' }, ruoli: ['MUSICISTA'] })));

    expect(auth.bandaCorrente()?.nome).toBe('Banda B');
    expect(auth.accessToken()).toBe('token-B');
    expect(auth.puoScrivere('parti')).toBe(false);
    expect(auth.puoLeggere('partiture')).toBe(true);
  });

  it('un superadmin senza banda non può usare i dati di nessuna banda', () => {
    accedi(account({ superadmin: true, bandaCorrente: null, ruoli: [], bande: [] }));

    expect(auth.isSuperadmin()).toBe(true);
    expect(auth.puoLeggere('partiture')).toBe(false);
    expect(auth.puoScrivere('catalogo')).toBe(false);
  });
});
