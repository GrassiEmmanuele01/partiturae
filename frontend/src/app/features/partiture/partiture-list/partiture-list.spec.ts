import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { environment } from '../../../../environments/environment';
import { Account, AuthResponse, Ruolo } from '../../auth/auth.model';
import { AuthService } from '../../auth/auth.service';
import { PartitureList } from './partiture-list';

const API = environment.apiUrl;

function risposta(ruoli: Ruolo[]): AuthResponse {
  const account: Account = {
    id: 1,
    email: 'prova@esempio.it',
    nome: null,
    cognome: null,
    superadmin: false,
    deveCambiarePassword: false,
    bandaCorrente: { id: 1, nome: 'Banda di prova' },
    ruoli,
    bande: [{ id: 1, nome: 'Banda di prova' }],
    socioId: null
  };
  return { accessToken: 'token', tokenType: 'Bearer', expiresIn: 900, account };
}

describe('Elenco partiture: i pulsanti dipendono dal ruolo', () => {
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

  async function apriPaginaCome(ruoli: Ruolo[]): Promise<string[]> {
    auth.login('prova@esempio.it', 'password').subscribe();
    backend.expectOne(`${API}/auth/login`).flush(risposta(ruoli));

    const fixture = TestBed.createComponent(PartitureList);
    fixture.detectChanges();
    backend.expectOne(`${API}/partiture`).flush([
      { id: 1, nome: 'Inno di Mameli', descrizione: null, anno: 1847, tipo: 'INNO', autore: { id: 1, nominativo: 'Goffredo Mameli' } }
    ]);
    fixture.detectChanges();
    await fixture.whenStable();

    const pagina = fixture.nativeElement as HTMLElement;
    return Array.from(pagina.querySelectorAll('a, button')).map((e) => e.textContent?.trim() ?? '');
  }

  it("l'archivista vede tutto, compreso Elimina", async () => {
    const azioni = await apriPaginaCome(['ARCHIVISTA']);
    expect(azioni).toEqual(expect.arrayContaining(['+ Nuova partitura', 'Parti', 'Modifica', 'Elimina']));
  });

  it('il maestro aggiunge e modifica ma non vede Elimina', async () => {
    const azioni = await apriPaginaCome(['MAESTRO']);
    expect(azioni).toEqual(expect.arrayContaining(['+ Nuova partitura', 'Parti', 'Modifica']));
    expect(azioni).not.toContain('Elimina');
    expect(azioni).not.toContain('La mia parte');
  });

  it('il musicista consulta soltanto e arriva alla propria parte', async () => {
    const azioni = await apriPaginaCome(['MUSICISTA']);
    expect(azioni).toContain('La mia parte');
    expect(azioni).not.toContain('+ Nuova partitura');
    expect(azioni).not.toContain('Modifica');
    expect(azioni).not.toContain('Elimina');
    expect(azioni).not.toContain('Parti');
  });
});