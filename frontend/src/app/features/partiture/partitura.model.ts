import { Autore } from '../autori/autore.model';

export interface Partitura {
  id: number;
  nome: string;
  descrizione: string | null;
  anno: number | null;
  autore: Autore;
}

export interface PartituraRequest {
  nome: string;
  descrizione?: string | null;
  anno?: number | null;
  autoreId: number;
}