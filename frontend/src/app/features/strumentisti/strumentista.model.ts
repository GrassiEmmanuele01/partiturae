import { Socio } from '../soci/socio.model';
import { Strumento } from '../strumenti/strumento.model';

export interface Strumentista {
  id: number;
  socio: Socio;
  strumenti: Strumento[];
}

export interface StrumentistaRequest {
  socioId: number;
}