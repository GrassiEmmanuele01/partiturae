/** Ruoli dentro una banda. Il superadmin non è un ruolo di banda: è un attributo dell'account. */
export type Ruolo =
  | 'ADMIN'
  | 'ARCHIVISTA'
  | 'MAESTRO'
  | 'MAESTROALLIEVI'
  | 'DIRETTIVO'
  | 'MUSICISTA'
  | 'ALLIEVO'
  | 'SOCIO';

export const RUOLO_LABELS: Record<Ruolo, string> = {
  ADMIN: 'Admin',
  ARCHIVISTA: 'Archivista',
  MAESTRO: 'Maestro',
  MAESTROALLIEVI: 'Maestro allievi',
  DIRETTIVO: 'Direttivo',
  MUSICISTA: 'Musicista',
  ALLIEVO: 'Allievo',
  SOCIO: 'Socio'
};

export interface BandaSintesi {
  id: number;
  nome: string;
}

export interface Account {
  id: number;
  email: string;
  nome: string | null;
  cognome: string | null;
  superadmin: boolean;
  deveCambiarePassword: boolean;
  /** La banda in cui si sta lavorando (null per un superadmin che non è in nessuna banda). */
  bandaCorrente: BandaSintesi | null;
  /** I ruoli dell'utente nella banda corrente. */
  ruoli: Ruolo[];
  /** Tutte le bande in cui l'utente può entrare. */
  bande: BandaSintesi[];
  socioId: number | null;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  /** Durata dell'access token in secondi. */
  expiresIn: number;
  account: Account;
}
