import { Partitura } from '../partiture/partitura.model';

export interface Raccolta {
  id: number;
  nome: string;
  anno: number | null;
  descrizione: string | null;
  numeroPartiture: number;
  partiture: RaccoltaPartitura[] | null;
}

export interface RaccoltaPartitura {
  ordine: number;
  partitura: Partitura;
}

export interface RaccoltaRequest {
  nome: string;
  anno?: number | null;
  descrizione?: string | null;
}