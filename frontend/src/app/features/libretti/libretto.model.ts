import { Partitura } from '../partiture/partitura.model';

export interface Libretto {
  id: number;
  nome: string;
  anno: number | null;
  descrizione: string | null;
  numeroPartiture: number;
  partiture: LibrettoPartitura[] | null;
}

export interface LibrettoPartitura {
  ordine: number;
  partitura: Partitura;
}

export interface LibrettoRequest {
  nome: string;
  anno?: number | null;
  descrizione?: string | null;
}