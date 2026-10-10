import { Ruolo } from './auth.model';

/** Le aree dell'applicazione su cui si decidono i permessi. */
export type Area =
  | 'soci'
  | 'musicisti'
  | 'direttivo'
  | 'formazione'
  | 'catalogo' // famiglie, strumenti, voci e autori
  | 'partiture'
  | 'parti' // le parti di una partitura e i loro PDF
  | 'raccolte'
  | 'calendario'
  | 'presenze';

export type Azione = 'leggere' | 'scrivere';

/** 'tutti' = qualunque utente che ha fatto il login. */
type Consentiti = Ruolo[] | 'tutti';

/**
 * Chi può fare cosa. Le stesse regole sono applicate dal backend (SecurityConfig): questa tabella serve
 * solo a nascondere ciò che non si può usare, la protezione vera sta sul server.
 * Se cambi una regola, cambiala in entrambi i posti.
 */
export const PERMESSI: Record<Area, Record<Azione, Consentiti>> = {
  // dati personali dei soci: solo l'amministratore
  soci: { leggere: ['ADMIN'], scrivere: ['ADMIN'] },
  musicisti: { leggere: ['ADMIN'], scrivere: ['ADMIN'] },
  direttivo: { leggere: ['ADMIN'], scrivere: ['ADMIN'] },
  formazione: { leggere: ['ADMIN'], scrivere: ['ADMIN'] },

  // archivio
  catalogo: { leggere: 'tutti', scrivere: ['ADMIN', 'ARCHIVISTA'] },
  partiture: { leggere: 'tutti', scrivere: ['ADMIN', 'ARCHIVISTA'] },
  parti: { leggere: ['ADMIN', 'MAESTRO', 'ARCHIVISTA'], scrivere: ['ADMIN', 'ARCHIVISTA'] },
  raccolte: { leggere: 'tutti', scrivere: ['ADMIN', 'MAESTRO', 'ARCHIVISTA'] },

  // calendario
  calendario: { leggere: 'tutti', scrivere: ['ADMIN', 'MAESTRO'] },
  presenze: { leggere: ['ADMIN', 'MAESTRO'], scrivere: ['ADMIN', 'MAESTRO'] }
};

/** True se almeno uno dei ruoli posseduti è tra quelli consentiti. */
export function haPermesso(ruoliPosseduti: readonly Ruolo[], area: Area, azione: Azione): boolean {
  const consentiti = PERMESSI[area][azione];
  return consentiti === 'tutti' || consentiti.some((ruolo) => ruoliPosseduti.includes(ruolo));
}
