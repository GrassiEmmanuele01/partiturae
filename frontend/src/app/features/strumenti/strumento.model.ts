export interface Strumento {
  id: number;
  nome: string;
  famigliaId: number;
  famigliaNome: string;
}

export interface StrumentoRequest {
  nome: string;
  famigliaId: number;
}