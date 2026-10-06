import { Socio } from '../soci/socio.model';
import { Strumento } from '../strumenti/strumento.model';

export interface Musicista {
  id: number;
  socio: Socio;
  strumenti: Strumento[];
}

export interface MusicistaRequest {
  socioId: number;
}