export interface Bandista {
  id: number;
  nome: string;
  cognome: string;
  mail: string;
  codiceFiscale: string | null;
  telefono: string | null;
}

export interface BandistaRequest {
  nome: string;
  cognome: string;
  mail: string;
  codiceFiscale?: string | null;
  telefono?: string | null;
}