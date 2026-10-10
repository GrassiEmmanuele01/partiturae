export type Ruolo = 'ADMIN_BANDA' | 'MAESTRO' | 'ARCHIVISTA' | 'MUSICISTA';

export const RUOLO_LABELS: Record<Ruolo, string> = {
  ADMIN_BANDA: 'Amministratore',
  MAESTRO: 'Maestro',
  ARCHIVISTA: 'Archivista',
  MUSICISTA: 'Musicista'
};

export interface Account {
  id: number;
  email: string;
  ruoli: Ruolo[];
  socioId: number | null;
  nome: string | null;
  cognome: string | null;
  deveCambiarePassword: boolean;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  /** Durata dell'access token in secondi. */
  expiresIn: number;
  account: Account;
}