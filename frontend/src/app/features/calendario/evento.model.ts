export type TipoEvento = 'PROVA' | 'CONCERTO' | 'ASSEMBLEA' | 'ALTRO';

export const TIPO_EVENTO_LABELS: Record<TipoEvento, string> = {
  PROVA: 'Prova',
  CONCERTO: 'Concerto',
  ASSEMBLEA: 'Assemblea',
  ALTRO: 'Altro'
};

export interface Evento {
  id: number;
  titolo: string;
  tipo: TipoEvento;
  data: string;
  ora: string | null;
  luogo: string | null;
  note: string | null;
  numeroPresenti: number;
}

export interface EventoRequest {
  titolo: string;
  tipo: TipoEvento;
  data: string;
  ora?: string | null;
  luogo?: string | null;
  note?: string | null;
}

export interface Presenza {
  socioId: number;
  nome: string;
  cognome: string;
  presente: boolean;
}