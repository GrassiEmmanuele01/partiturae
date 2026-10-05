import { MembroDirettivo } from '../direttivo/membro-direttivo.model';

export interface Banda {
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

export interface BandaRequest {
  nome: string;
  descrizione?: string | null;
  annoFondazione?: number | null;
  indirizzo?: string | null;
  codiceFiscale?: string | null;
  email?: string | null;
  telefono?: string | null;
  sitoWeb?: string | null;
}