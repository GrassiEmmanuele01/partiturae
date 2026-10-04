import { Socio } from '../soci/socio.model';

export interface MembroDirettivo {
  id: number;
  socio: Socio;
  carica: string;
  annoInizio: number;
  annoFine: number | null;
  inCarica: boolean;
}

export interface MembroDirettivoRequest {
  socioId: number;
  carica: string;
  annoInizio: number;
  annoFine?: number | null;
}