import { Strumento } from '../strumenti/strumento.model';

export interface Bandista {
  id: number;
  nome: string;
  cognome: string;
  mail: string;
  codiceFiscale: string | null;
  telefono: string | null;
  strumenti: Strumento[];
  tesseratoAnnoCorrente: boolean;
}

export interface BandistaRequest {
  nome: string;
  cognome: string;
  mail: string;
  codiceFiscale?: string | null;
  telefono?: string | null;
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