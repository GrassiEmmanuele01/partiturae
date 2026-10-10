import { Ruolo } from './auth.model';
import { Area, Azione, haPermesso } from './permessi';

function puo(ruoli: Ruolo[], area: Area, azione: Azione): boolean {
  return haPermesso(ruoli, area, azione);
}

const AREE: Area[] = ['soci', 'musicisti', 'direttivo', 'formazione', 'catalogo', 'partiture', 'parti', 'mieParti', 'raccolte', 'calendario', 'presenze'];
const AZIONI: Azione[] = ['leggere', 'scrivere', 'eliminare'];

describe('permessi per ruolo', () => {
  it("l'admin può fare tutto (tranne le parti personali di chi suona)", () => {
    for (const area of AREE.filter((a) => a !== 'mieParti')) {
      for (const azione of AZIONI) {
        expect(puo(['ADMIN'], area, azione)).toBe(true);
      }
    }
  });

  it("l'archivista gestisce archivio, soci e calendari, ma non le informazioni della banda", () => {
    for (const area of ['catalogo', 'partiture', 'parti', 'raccolte', 'soci', 'musicisti', 'calendario', 'presenze'] as Area[]) {
      for (const azione of AZIONI) {
        expect(puo(['ARCHIVISTA'], area, azione)).toBe(true);
      }
    }
    expect(puo(['ARCHIVISTA'], 'formazione', 'leggere')).toBe(true);
    expect(puo(['ARCHIVISTA'], 'formazione', 'scrivere')).toBe(false);
  });

  it('il maestro aggiunge e modifica ma non elimina', () => {
    for (const ruolo of ['MAESTRO', 'MAESTROALLIEVI'] as Ruolo[]) {
      for (const area of ['partiture', 'parti', 'raccolte', 'catalogo'] as Area[]) {
        expect(puo([ruolo], area, 'leggere')).toBe(true);
        expect(puo([ruolo], area, 'scrivere')).toBe(true);
        expect(puo([ruolo], area, 'eliminare')).toBe(false);
      }
      expect(puo([ruolo], 'calendario', 'scrivere')).toBe(true);
      expect(puo([ruolo], 'soci', 'leggere')).toBe(false);
    }
  });

  it('il direttivo gestisce soci, musicisti, informazioni della banda e calendari, non le partiture', () => {
    for (const azione of AZIONI) {
      expect(puo(['DIRETTIVO'], 'soci', azione)).toBe(true);
      expect(puo(['DIRETTIVO'], 'musicisti', azione)).toBe(true);
      expect(puo(['DIRETTIVO'], 'formazione', azione)).toBe(true);
      expect(puo(['DIRETTIVO'], 'calendario', azione)).toBe(true);
    }
    expect(puo(['DIRETTIVO'], 'partiture', 'leggere')).toBe(false);
    expect(puo(['DIRETTIVO'], 'raccolte', 'leggere')).toBe(false);
  });

  it('musicista e allievo consultano repertorio e raccolte, vedono le proprie parti ma non quelle di tutti e non modificano', () => {
    for (const ruolo of ['MUSICISTA', 'ALLIEVO'] as Ruolo[]) {
      for (const area of ['partiture', 'raccolte', 'catalogo', 'calendario'] as Area[]) {
        expect(puo([ruolo], area, 'leggere')).toBe(true);
        expect(puo([ruolo], area, 'scrivere')).toBe(false);
      }
      for (const area of ['soci', 'musicisti', 'direttivo', 'formazione', 'parti', 'presenze'] as Area[]) {
        expect(puo([ruolo], area, 'leggere')).toBe(false);
      }
      expect(puo([ruolo], 'mieParti', 'leggere')).toBe(true);
    }
  });

  it('le proprie parti sono solo per chi suona', () => {
    for (const ruolo of ['ADMIN', 'ARCHIVISTA', 'MAESTRO', 'MAESTROALLIEVI', 'DIRETTIVO', 'SOCIO'] as Ruolo[]) {
      expect(puo([ruolo], 'mieParti', 'leggere')).toBe(false);
    }
    // chi suona e ha anche altri compiti ha entrambi i ruoli
    expect(puo(['ARCHIVISTA', 'MUSICISTA'], 'mieParti', 'leggere')).toBe(true);
  });

  it('il socio vede solo il calendario', () => {
    for (const area of AREE) {
      expect(puo(['SOCIO'], area, 'leggere')).toBe(area === 'calendario');
      expect(puo(['SOCIO'], area, 'scrivere')).toBe(false);
      expect(puo(['SOCIO'], area, 'eliminare')).toBe(false);
    }
  });

  it('con più ruoli valgono i permessi di tutti', () => {
    expect(puo(['MUSICISTA', 'ARCHIVISTA'], 'parti', 'eliminare')).toBe(true);
    expect(puo(['MUSICISTA', 'DIRETTIVO'], 'soci', 'eliminare')).toBe(true);
    expect(puo(['SOCIO', 'MAESTRO'], 'calendario', 'scrivere')).toBe(true);
  });

  it('senza ruoli non si può fare niente', () => {
    for (const area of AREE) {
      for (const azione of AZIONI) {
        expect(puo([], area, azione)).toBe(false);
      }
    }
  });
});