export interface Banda {
  id: number;
  nome: string;
  descrizione: string | null;
}

export interface BandaRequest {
  nome: string;
  descrizione?: string | null;
}