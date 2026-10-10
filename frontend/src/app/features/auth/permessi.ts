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

/** leggere = vedere, scrivere = aggiungere e modificare, eliminare = cancellare. */
export type Azione = 'leggere' | 'scrivere' | 'eliminare';

// Gruppi di ruoli (gli stessi nomi di PermessiApi.java nel backend)
const TUTTI: Ruolo[] = ['ADMIN', 'ARCHIVISTA', 'MAESTRO', 'MAESTROALLIEVI', 'DIRETTIVO', 'MUSICISTA', 'ALLIEVO', 'SOCIO'];
const SEGRETERIA: Ruolo[] = ['ADMIN', 'ARCHIVISTA', 'DIRETTIVO']; // dati personali dei soci
const DIREZIONE_BANDA: Ruolo[] = ['ADMIN', 'DIRETTIVO']; // informazioni e direttivo della banda
const CHI_COMPONE: Ruolo[] = ['ADMIN', 'ARCHIVISTA', 'MAESTRO', 'MAESTROALLIEVI']; // aggiungono e modificano
const GESTORI_ARCHIVIO: Ruolo[] = ['ADMIN', 'ARCHIVISTA']; // eliminano dall'archivio
const CHI_LEGGE_REPERTORIO: Ruolo[] = ['ADMIN', 'ARCHIVISTA', 'MAESTRO', 'MAESTROALLIEVI', 'MUSICISTA', 'ALLIEVO'];
const TUTTI_TRANNE_SOCIO: Ruolo[] = ['ADMIN', 'ARCHIVISTA', 'MAESTRO', 'MAESTROALLIEVI', 'DIRETTIVO', 'MUSICISTA', 'ALLIEVO'];
const CALENDARI: Ruolo[] = ['ADMIN', 'ARCHIVISTA', 'DIRETTIVO', 'MAESTRO', 'MAESTROALLIEVI'];

/**
 * Chi può fare cosa. Le stesse regole sono applicate dal backend (PermessiApi.java): questa tabella serve
 * solo a nascondere ciò che non si può usare, la protezione vera sta sul server.
 * Se cambi una regola, cambiala in entrambi i posti.
 */
export const PERMESSI: Record<Area, Record<Azione, readonly Ruolo[]>> = {
  // libro soci: dati personali
  soci: { leggere: SEGRETERIA, scrivere: SEGRETERIA, eliminare: SEGRETERIA },
  musicisti: { leggere: SEGRETERIA, scrivere: SEGRETERIA, eliminare: SEGRETERIA },

  // informazioni e direttivo della banda
  direttivo: { leggere: SEGRETERIA, scrivere: DIREZIONE_BANDA, eliminare: DIREZIONE_BANDA },
  formazione: { leggere: SEGRETERIA, scrivere: DIREZIONE_BANDA, eliminare: DIREZIONE_BANDA },

  // archivio
  catalogo: { leggere: TUTTI_TRANNE_SOCIO, scrivere: CHI_COMPONE, eliminare: GESTORI_ARCHIVIO },
  partiture: { leggere: CHI_LEGGE_REPERTORIO, scrivere: CHI_COMPONE, eliminare: GESTORI_ARCHIVIO },
  parti: { leggere: CHI_COMPONE, scrivere: CHI_COMPONE, eliminare: GESTORI_ARCHIVIO },
  raccolte: { leggere: CHI_LEGGE_REPERTORIO, scrivere: CHI_COMPONE, eliminare: GESTORI_ARCHIVIO },

  // calendario
  calendario: { leggere: TUTTI, scrivere: CALENDARI, eliminare: CALENDARI },
  presenze: { leggere: CALENDARI, scrivere: CALENDARI, eliminare: CALENDARI }
};

/** True se almeno uno dei ruoli posseduti è tra quelli consentiti. */
export function haPermesso(ruoliPosseduti: readonly Ruolo[], area: Area, azione: Azione): boolean {
  return PERMESSI[area][azione].some((ruolo) => ruoliPosseduti.includes(ruolo));
}
