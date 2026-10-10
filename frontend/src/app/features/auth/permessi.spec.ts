import { Ruolo } from './auth.model';
import { Area, Azione, haPermesso } from './permessi';

function puo(ruoli: Ruolo[], area: Area, azione: Azione): boolean {
  return haPermesso(ruoli, area, azione);
}

describe('permessi per ruolo', () => {
  it("l'amministratore può fare tutto", () => {
    const aree: Area[] = ['soci', 'musicisti', 'direttivo', 'formazione', 'catalogo', 'partiture', 'parti', 'raccolte', 'calendario', 'presenze'];
    for (const area of aree) {
      expect(puo(['ADMIN_BANDA'], area, 'leggere')).toBe(true);
      expect(puo(['ADMIN_BANDA'], area, 'scrivere')).toBe(true);
    }
  });

  it("il musicista non vede i dati personali dei soci né le parti", () => {
    for (const area of ['soci', 'musicisti', 'direttivo', 'formazione', 'parti', 'presenze'] as Area[]) {
      expect(puo(['MUSICISTA'], area, 'leggere')).toBe(false);
    }
  });

  it('il musicista legge repertorio, raccolte e calendario ma non modifica niente', () => {
    for (const area of ['catalogo', 'partiture', 'raccolte', 'calendario'] as Area[]) {
      expect(puo(['MUSICISTA'], area, 'leggere')).toBe(true);
      expect(puo(['MUSICISTA'], area, 'scrivere')).toBe(false);
    }
  });

  it("l'archivista cura l'archivio ma non il calendario né i soci", () => {
    expect(puo(['ARCHIVISTA'], 'partiture', 'scrivere')).toBe(true);
    expect(puo(['ARCHIVISTA'], 'parti', 'scrivere')).toBe(true);
    expect(puo(['ARCHIVISTA'], 'catalogo', 'scrivere')).toBe(true);
    expect(puo(['ARCHIVISTA'], 'raccolte', 'scrivere')).toBe(true);
    expect(puo(['ARCHIVISTA'], 'calendario', 'scrivere')).toBe(false);
    expect(puo(['ARCHIVISTA'], 'soci', 'leggere')).toBe(false);
  });

  it('il maestro gestisce calendario, presenze e raccolte, e legge le parti ma non le modifica', () => {
    expect(puo(['MAESTRO'], 'calendario', 'scrivere')).toBe(true);
    expect(puo(['MAESTRO'], 'presenze', 'scrivere')).toBe(true);
    expect(puo(['MAESTRO'], 'raccolte', 'scrivere')).toBe(true);
    expect(puo(['MAESTRO'], 'parti', 'leggere')).toBe(true);
    expect(puo(['MAESTRO'], 'parti', 'scrivere')).toBe(false);
    expect(puo(['MAESTRO'], 'partiture', 'scrivere')).toBe(false);
  });

  it('con più ruoli valgono i permessi di tutti', () => {
    expect(puo(['MUSICISTA', 'ARCHIVISTA'], 'parti', 'scrivere')).toBe(true);
    expect(puo(['MUSICISTA', 'MAESTRO'], 'calendario', 'scrivere')).toBe(true);
  });

  it('senza ruoli si può leggere solo ciò che è aperto a tutti', () => {
    expect(puo([], 'partiture', 'leggere')).toBe(true);
    expect(puo([], 'partiture', 'scrivere')).toBe(false);
    expect(puo([], 'soci', 'leggere')).toBe(false);
  });
});