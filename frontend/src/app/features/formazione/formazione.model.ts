import { MembroDirettivo } from '../direttivo/membro-direttivo.model';

export interface Formazione {
  id: number;
  nome: string;
  descrizione: string | null;
  annoFondazione: number | null;
  indirizzo: string | null;
  codiceFiscale: string | null;
  email: string | null;
  telefono: string | null;
  sitoWeb: string | null;
  numeroAssociatiAnnoCorrente: number;
  direttivoInCarica: MembroDirettivo[];
}

export interface FormazioneRequest {
  nome: string;
  descrizione?: string | null;
  annoFondazione?: number | null;
  indirizzo?: string | null;
  codiceFiscale?: string | null;
  email?: string | null;
  telefono?: string | null;
  sitoWeb?: string | null;
}