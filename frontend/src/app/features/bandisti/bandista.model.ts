import { Socio } from '../soci/socio.model';
import { Strumento } from '../strumenti/strumento.model';

export interface Bandista {
  id: number;
  socio: Socio;
  strumenti: Strumento[];
  tesseratoAnnoCorrente: boolean;
}

export interface BandistaRequest {
  socioId: number;
}

export interface Tesseramento {
  anno: number;
  tesserato: boolean;
}

export interface TesseramentoSummary {
  tesseratoAnnoCorrente: boolean;
  anniTesserato: number;
  anni: number[];
}