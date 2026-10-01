export interface Parte {
  id: number;
  nome: string;
  libretto: boolean | null;
  pdfNome: string | null;
  partituraId: number;
  partituraNome: string;
  strumentoFiglioId: number;
  strumentoFiglioNome: string;
}

export interface ParteRequest {
  nome: string;
  partituraId: number;
  strumentoFiglioId: number;
  libretto?: boolean | null;
}