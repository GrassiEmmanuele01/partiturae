import { StrumentoFiglio } from '../strumenti-figli/strumento-figlio.model';

export interface Parte {
  id: number;
  partituraId: number;
  partituraNome: string;
  strumenti: StrumentoFiglio[];
  pdfPresente: boolean;
  pdfNome: string | null;
}

export interface ParteRequest {
  partituraId: number;
  strumentoFiglioIds: number[];
}
