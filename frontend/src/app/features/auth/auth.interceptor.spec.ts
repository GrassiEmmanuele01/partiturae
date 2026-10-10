import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';

import { environment } from '../../../environments/environment';
import { AuthResponse } from './auth.model';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from './auth.service';

const API = environment.apiUrl;

function risposta(token: string): AuthResponse {
  return {
    accessToken: token,
    tokenType: 'Bearer',
    expiresIn: 900,
    account: { id: 1, email: 'a@b.it', ruoli: ['ADMIN_BANDA'], socioId: null, nome: null, cognome: null, deveCambiarePassword: false }
  };
}

describe('authInterceptor', () => {
  let http: HttpClient;
  let backend: HttpTestingController;
  let auth: AuthService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting()
      ]
    });
    http = TestBed.inject(HttpClient);
    backend = TestBed.inject(HttpTestingController);
    auth = TestBed.inject(AuthService);
  });

  afterEach(() => backend.verify());

  function accedi(): void {
    auth.login('a@b.it', 'password').subscribe();
    backend.expectOne(`${API}/auth/login`).flush(risposta('token-1'));
  }

  it('aggiunge il token alle richieste verso l\'API', () => {
    accedi();

    http.get(`${API}/soci`).subscribe();
    const richiesta = backend.expectOne(`${API}/soci`);

    expect(richiesta.request.headers.get('Authorization')).toBe('Bearer token-1');
    richiesta.flush([]);
  });

  it('non aggiunge il token alle richieste verso altri indirizzi', () => {
    accedi();

    http.get('https://esempio.it/dati').subscribe();
    const richiesta = backend.expectOne('https://esempio.it/dati');

    expect(richiesta.request.headers.has('Authorization')).toBe(false);
    richiesta.flush({});
  });

  it('con un 401 rinnova il token e ripete la richiesta una volta', () => {
    accedi();
    let esito: unknown = null;

    http.get(`${API}/soci`).subscribe((dati) => (esito = dati));

    backend.expectOne(`${API}/soci`).flush({}, { status: 401, statusText: 'Unauthorized' });
    backend.expectOne(`${API}/auth/refresh`).flush(risposta('token-2'));

    const ripetuta = backend.expectOne(`${API}/soci`);
    expect(ripetuta.request.headers.get('Authorization')).toBe('Bearer token-2');
    ripetuta.flush(['ok']);

    expect(esito).toEqual(['ok']);
  });

  it('con più 401 contemporanei fa un solo rinnovo', () => {
    accedi();

    http.get(`${API}/soci`).subscribe();
    http.get(`${API}/strumenti`).subscribe();

    backend.expectOne(`${API}/soci`).flush({}, { status: 401, statusText: 'Unauthorized' });
    backend.expectOne(`${API}/strumenti`).flush({}, { status: 401, statusText: 'Unauthorized' });

    const rinnovi = backend.match(`${API}/auth/refresh`);
    expect(rinnovi.length).toBe(1);
    rinnovi[0].flush(risposta('token-2'));

    backend.expectOne(`${API}/soci`).flush([]);
    backend.expectOne(`${API}/strumenti`).flush([]);
  });

  it('se il rinnovo fallisce esce dalla sessione e torna al login', () => {
    accedi();
    const router = TestBed.inject(Router);
    const navigate = vi.spyOn(router, 'navigate').mockResolvedValue(true);
    let errore: unknown = null;

    http.get(`${API}/soci`).subscribe({ error: (e) => (errore = e) });

    backend.expectOne(`${API}/soci`).flush({}, { status: 401, statusText: 'Unauthorized' });
    backend.expectOne(`${API}/auth/refresh`).flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(errore).not.toBeNull();
    expect(auth.isAuthenticated()).toBe(false);
    expect(navigate).toHaveBeenCalled();
  });

  it('un errore diverso da 401 non fa partire nessun rinnovo', () => {
    accedi();
    let errore: { status: number } | null = null;

    http.get(`${API}/soci`).subscribe({ error: (e) => (errore = e) });
    backend.expectOne(`${API}/soci`).flush({}, { status: 500, statusText: 'Server Error' });

    expect(errore!.status).toBe(500);
    backend.expectNone(`${API}/auth/refresh`);
  });
});