import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { environment } from '../environments/environment';
import { AuthResponse, BandaSintesi, Ruolo } from './features/auth/auth.model';
import { AuthService } from './features/auth/auth.service';
import { FileService } from './shared/file.service';
import { App } from './app';

const BANDA: BandaSintesi = { id: 1, nome: 'Banda di prova' };

function risposta(ruoli: Ruolo[], opzioni: { superadmin?: boolean; bande?: BandaSintesi[] } = {}): AuthResponse {
  return {
    accessToken: 'token',
    tokenType: 'Bearer',
    expiresIn: 900,
    account: {
      id: 1,
      email: 'prova@esempio.it',
      nome: null,
      cognome: null,
      superadmin: opzioni.superadmin ?? false,
      deveCambiarePassword: false,
      bandaCorrente: BANDA,
      ruoli,
      bande: opzioni.bande ?? [BANDA],
      socioId: null
    }
  };
}

describe('App', () => {
  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('non mostra il menu a chi non ha fatto il login', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.sidebar')).toBeNull();
  });

  it('i link verso l\'API vengono scaricati con il token invece di aprirsi da soli', () => {
    const scarica = vi.spyOn(TestBed.inject(FileService), 'scarica').mockImplementation(() => undefined);
    TestBed.createComponent(App);

    const link = document.createElement('a');
    link.href = `${environment.apiUrl}/parti/5/pdf`;
    document.body.appendChild(link);
    const evento = new MouseEvent('click', { bubbles: true, cancelable: true });
    link.dispatchEvent(evento);
    link.remove();

    expect(evento.defaultPrevented).toBe(true);
    expect(scarica).toHaveBeenCalledWith(`${environment.apiUrl}/parti/5/pdf`);
  });

  it('i link normali dell\'app non vengono toccati', () => {
    const scarica = vi.spyOn(TestBed.inject(FileService), 'scarica').mockImplementation(() => undefined);
    TestBed.createComponent(App);

    const link = document.createElement('a');
    link.href = 'http://localhost/partiture';
    link.addEventListener('click', (e) => e.preventDefault());
    document.body.appendChild(link);
    link.click();
    link.remove();

    expect(scarica).not.toHaveBeenCalled();
  });

  describe('menu laterale', () => {
    async function apriCome(ruoli: Ruolo[], opzioni: { superadmin?: boolean; bande?: BandaSintesi[] } = {}): Promise<HTMLElement> {
      TestBed.inject(AuthService).login('prova@esempio.it', 'password').subscribe();
      TestBed.inject(HttpTestingController).expectOne(`${environment.apiUrl}/auth/login`).flush(risposta(ruoli, opzioni));

      const fixture = TestBed.createComponent(App);
      fixture.detectChanges();
      await fixture.whenStable();
      return fixture.nativeElement as HTMLElement;
    }

    const voci = (pagina: HTMLElement) =>
      Array.from(pagina.querySelectorAll('nav a')).map((a) => a.textContent?.trim());

    it('il musicista ha "Le mie parti" e le partiture, ma non il libro soci né le impostazioni', async () => {
      const pagina = await apriCome(['MUSICISTA']);

      expect(voci(pagina)).toEqual(expect.arrayContaining(['Home', 'Partiture', 'Le mie parti', 'Raccolte', 'Calendario']));
      expect(voci(pagina)).not.toContain('Libro soci');
      expect(voci(pagina)).not.toContain('Impostazioni');
    });

    it("l'archivista ha le impostazioni ma non \"Le mie parti\" (non è un'area per lui)", async () => {
      const pagina = await apriCome(['ARCHIVISTA']);

      expect(voci(pagina)).toContain('Impostazioni');
      expect(voci(pagina)).not.toContain('Le mie parti');
    });

    it('il socio vede solo Home e Calendario', async () => {
      const pagina = await apriCome(['SOCIO']);

      expect(voci(pagina)).toEqual(['Home', 'Calendario']);
    });

    it('con una banda sola il suo nome è scritto sopra il menu, senza selettore', async () => {
      const pagina = await apriCome(['ADMIN']);

      expect(pagina.querySelector('.banda-nome')?.textContent?.trim()).toBe('Banda di prova');
      expect(pagina.querySelector('select')).toBeNull();
    });

    it('con più bande compare il selettore, con la banda corrente già scelta', async () => {
      const pagina = await apriCome(['ADMIN'], { bande: [BANDA, { id: 2, nome: 'Altra banda' }] });

      const opzioni = Array.from(pagina.querySelectorAll('select option')) as HTMLOptionElement[];
      expect(opzioni.map((o) => o.textContent?.trim())).toEqual(['Banda di prova', 'Altra banda']);
      expect(opzioni[0].selected).toBe(true);
      expect(pagina.querySelector('.banda-nome')).toBeNull();
    });

    it('sotto il nome ci sono i ruoli, compreso il superadmin', async () => {
      const pagina = await apriCome(['ADMIN', 'MUSICISTA'], { superadmin: true });

      const badge = Array.from(pagina.querySelectorAll('.role-badge')).map((b) => b.textContent?.trim());
      expect(badge).toEqual(['Superadmin', 'Admin', 'Musicista']);
    });
  });
});