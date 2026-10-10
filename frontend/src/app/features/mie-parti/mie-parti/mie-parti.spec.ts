import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';

import { environment } from '../../../../environments/environment';
import { Parte } from '../../parti/parte.model';
import { MiePartiRisposta } from '../mie-parti.model';
import { MieParti } from './mie-parti';

const API = environment.apiUrl;

function parte(id: number, partituraId: number, partituraNome: string, voci: string[], pdf = true): Parte {
  return {
    id,
    partituraId,
    partituraNome,
    strumenti: voci.map((nome, i) => ({ id: id * 10 + i, nome, strumentoId: 7, strumentoNome: 'Tromba' })),
    pdfPresente: pdf,
    pdfNome: pdf ? `${voci[0].replace(' ', '')}_${partituraNome.replace(/ /g, '')}.pdf` : null
  };
}

describe('Le mie parti', () => {
  let backend: HttpTestingController;

  function configura(idPartitura: string | null) {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap(idPartitura ? { id: idPartitura } : {}) } }
        }
      ]
    });
    backend = TestBed.inject(HttpTestingController);
  }

  async function apri(risposta: MiePartiRisposta) {
    const fixture = TestBed.createComponent(MieParti);
    fixture.detectChanges();
    backend.expectOne((r) => r.url === `${API}/mie-parti`).flush(risposta);
    fixture.detectChanges();
    await fixture.whenStable();
    return fixture.nativeElement as HTMLElement;
  }

  afterEach(() => backend.verify());

  it('se non trova il profilo musicale spiega cosa fare', async () => {
    configura(null);
    const pagina = await apri({ profiloTrovato: false, strumenti: [], parti: [] });

    expect(pagina.textContent).toContain('Non trovo il tuo profilo musicale');
    expect(pagina.querySelector('table')).toBeNull();
  });

  it('se non ha strumenti lo dice', async () => {
    configura(null);
    const pagina = await apri({ profiloTrovato: true, strumenti: [], parti: [] });

    expect(pagina.textContent).toContain('non ha ancora strumenti');
  });

  it('mostra gli strumenti e le parti, con il link per scaricare il PDF', async () => {
    configura(null);
    const pagina = await apri({
      profiloTrovato: true,
      strumenti: ['Tromba'],
      parti: [parte(5, 10, 'Inno di Mameli', ['Tromba 1', 'Tromba 2']), parte(6, 20, 'Marcia lenta', ['Tromba 3'], false)]
    });

    expect(pagina.querySelector('.chip')?.textContent?.trim()).toBe('Tromba');

    const righe = Array.from(pagina.querySelectorAll('tbody tr'));
    expect(righe).toHaveLength(2);
    expect(righe[0].textContent).toContain('Inno di Mameli');
    expect(righe[0].textContent).toContain('Tromba 1, Tromba 2');
    expect(righe[0].querySelector('a')?.getAttribute('href')).toBe(`${API}/mie-parti/5/pdf`);
    expect(righe[1].textContent).toContain('PDF non ancora caricato');
    expect(righe[1].querySelector('a')).toBeNull();
  });

  it('la ricerca filtra per nome della partitura', async () => {
    configura(null);
    const fixture = TestBed.createComponent(MieParti);
    fixture.detectChanges();
    backend.expectOne((r) => r.url === `${API}/mie-parti`).flush({
      profiloTrovato: true,
      strumenti: ['Tromba'],
      parti: [parte(5, 10, 'Inno di Mameli', ['Tromba 1']), parte(6, 20, 'Marcia lenta', ['Tromba 3'])]
    });
    fixture.detectChanges();
    await fixture.whenStable();
    const pagina = fixture.nativeElement as HTMLElement;

    const campo = pagina.querySelector('input[type="search"]') as HTMLInputElement;
    campo.value = 'marcia';
    campo.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    await fixture.whenStable();

    const righe = Array.from(pagina.querySelectorAll('tbody tr'));
    expect(righe).toHaveLength(1);
    expect(righe[0].textContent).toContain('Marcia lenta');
  });

  it('se la rotta indica una partitura chiede al server solo quella', async () => {
    configura('10');
    const fixture = TestBed.createComponent(MieParti);
    fixture.detectChanges();

    const richiesta = backend.expectOne((r) => r.url === `${API}/mie-parti`);
    expect(richiesta.request.params.get('partituraId')).toBe('10');
    richiesta.flush({ profiloTrovato: true, strumenti: ['Tromba'], parti: [parte(5, 10, 'Inno di Mameli', ['Tromba 1'])] });
    fixture.detectChanges();
    await fixture.whenStable();

    const pagina = fixture.nativeElement as HTMLElement;
    expect(pagina.querySelector('h2')?.textContent).toContain('La mia parte');
    expect(pagina.querySelector('.subtitle')?.textContent).toContain('Inno di Mameli');
    expect(pagina.querySelector('input[type="search"]')).toBeNull();
  });
});