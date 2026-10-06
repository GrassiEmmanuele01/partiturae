import { Socio } from '../soci/socio.model';

export type CaricaDirettivo =
  | 'PRESIDENTE'
  | 'VICEPRESIDENTE'
  | 'SEGRETARIO'
  | 'TESORIERE'
  | 'CONSIGLIERE'
  | 'REVISORE_DEI_CONTI'
  | 'MAESTRO_CONCERTATORE'
  | 'ALTRO';

export const CARICA_DIRETTIVO_LABELS: Record<CaricaDirettivo, string> = {
  PRESIDENTE: 'Presidente',
  VICEPRESIDENTE: 'Vicepresidente',
  SEGRETARIO: 'Segretario',
  TESORIERE: 'Tesoriere',
  CONSIGLIERE: 'Consigliere',
  REVISORE_DEI_CONTI: 'Revisore dei conti',
  MAESTRO_CONCERTATORE: 'Maestro concertatore',
  ALTRO: 'Altro'
};

export const CARICHE_UNICHE: CaricaDirettivo[] = [
  'PRESIDENTE',
  'VICEPRESIDENTE',
  'SEGRETARIO',
  'TESORIERE',
  'MAESTRO_CONCERTATORE'
];

export interface MembroDirettivo {
  id: number;
  socio: Socio;
  carica: CaricaDirettivo;
  annoInizio: number;
  annoFine: number | null;
  inCarica: boolean;
}

export interface MembroDirettivoRequest {
  socioId: number;
  carica: CaricaDirettivo;
  annoInizio: number;
  annoFine?: number | null;
}