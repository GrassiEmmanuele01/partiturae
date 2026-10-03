import { Autore } from '../autori/autore.model';

export type TipoPartitura =
  | 'MARCIA_LIBRETTO'
  | 'MARCIA_CONCERTO'
  | 'INNO'
  | 'VALZER'
  | 'POLKA'
  | 'MAZURKA'
  | 'ALTRO';

export const TIPO_PARTITURA_LABELS: Record<TipoPartitura, string> = {
  MARCIA_LIBRETTO: 'Marcia da libretto',
  MARCIA_CONCERTO: 'Marcia da concerto',
  INNO: 'Inno',
  VALZER: 'Valzer',
  POLKA: 'Polka',
  MAZURKA: 'Mazurka',
  ALTRO: 'Altro'
};

export interface Partitura {
  id: number;
  nome: string;
  descrizione: string | null;
  anno: number | null;
  tipo: TipoPartitura;
  autore: Autore;
}

export interface PartituraRequest {
  nome: string;
  descrizione?: string | null;
  anno?: number | null;
  tipo: TipoPartitura;
  autoreId: number;
}