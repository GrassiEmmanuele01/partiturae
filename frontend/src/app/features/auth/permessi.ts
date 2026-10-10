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
  soci: { leggere: ['ADMIN_BANDA'], scrivere: ['ADMIN_BANDA'] },
  musicisti: { leggere: ['ADMIN_BANDA'], scrivere: ['ADMIN_BANDA'] },
  direttivo: { leggere: ['ADMIN_BANDA'], scrivere: ['ADMIN_BANDA'] },
  formazione: { leggere: ['ADMIN_BANDA'], scrivere: ['ADMIN_BANDA'] },

  // archivio
  catalogo: { leggere: 'tutti', scrivere: ['ADMIN_BANDA', 'ARCHIVISTA'] },
  partiture: { leggere: 'tutti', scrivere: ['ADMIN_BANDA', 'ARCHIVISTA'] },
  parti: { leggere: ['ADMIN_BANDA', 'MAESTRO', 'ARCHIVISTA'], scrivere: ['ADMIN_BANDA', 'ARCHIVISTA'] },
  raccolte: { leggere: 'tutti', scrivere: ['ADMIN_BANDA', 'MAESTRO', 'ARCHIVISTA'] },

  // calendario
  calendario: { leggere: 'tutti', scrivere: ['ADMIN_BANDA', 'MAESTRO'] },
  presenze: { leggere: ['ADMIN_BANDA', 'MAESTRO'], scrivere: ['ADMIN_BANDA', 'MAESTRO'] }
};

/** True se almeno uno dei ruoli posseduti è tra quelli consentiti. */
export function haPermesso(ruoliPosseduti: readonly Ruolo[], area: Area, azione: Azione): boolean {
  const consentiti = PERMESSI[area][azione];
  return consentiti === 'tutti' || consentiti.some((ruolo) => ruoliPosseduti.includes(ruolo));
}