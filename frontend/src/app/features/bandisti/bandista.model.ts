import { Socio } from '../soci/socio.model';
import { Strumento } from '../strumenti/strumento.model';

export interface Bandista {
  id: number;
  socio: Socio;
  strumenti: Strumento[];
}

export interface BandistaRequest {
  socioId: number;
}