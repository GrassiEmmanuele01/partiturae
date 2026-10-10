import { Component, signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';

import { AuthService } from './auth.service';
import { PuoDirective } from './puo.directive';

@Component({
  imports: [PuoDirective],
  template: `
    <button *puo="'partiture:eliminare'" id="elimina">Elimina</button>
    <button *puo="'partiture:scrivere'" id="modifica">Modifica</button>
    <button *puo="'partiture:leggere'" id="leggi">Leggi</button>
  `
})
class Prova {}

describe('PuoDirective', () => {
  const puoEliminare = signal(false);

  beforeEach(() => {
    puoEliminare.set(false);
    TestBed.configureTestingModule({
      imports: [Prova],
      providers: [
        {
          provide: AuthService,
          useValue: {
            puoLeggere: () => true,
            puoScrivere: () => true,
            puoEliminare: () => puoEliminare()
          }
        }
      ]
    });
  });

  async function disegna() {
    const fixture = TestBed.createComponent(Prova);
    fixture.detectChanges();
    await fixture.whenStable();
    return fixture;
  }

  it('mostra solo gli elementi per cui c\'è il permesso', async () => {
    const fixture = await disegna();
    const pagina = fixture.nativeElement as HTMLElement;

    expect(pagina.querySelector('#leggi')).not.toBeNull();
    expect(pagina.querySelector('#modifica')).not.toBeNull();
    expect(pagina.querySelector('#elimina')).toBeNull();
  });

  it('se i permessi cambiano (es. cambio banda) la pagina si aggiorna', async () => {
    const fixture = await disegna();
    const pagina = fixture.nativeElement as HTMLElement;
    expect(pagina.querySelector('#elimina')).toBeNull();

    puoEliminare.set(true);
    fixture.detectChanges();
    await fixture.whenStable();
    expect(pagina.querySelector('#elimina')).not.toBeNull();

    puoEliminare.set(false);
    fixture.detectChanges();
    await fixture.whenStable();
    expect(pagina.querySelector('#elimina')).toBeNull();
  });
});
