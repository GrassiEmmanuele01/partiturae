export interface StrumentoFiglio {
  id: number;
  nome: string;
  strumentoId: number;
  strumentoNome: string;
}

export interface StrumentoFiglioRequest {
  nome: string;
  strumentoId: number;
}