export interface Socio {
  id: number;
  nome: string;
  cognome: string;
  mail: string;
  codiceFiscale: string | null;
  telefono: string | null;
  aggiunto: boolean;
  iscrittoAnnoCorrente: boolean;
}

export interface SocioRequest {
  nome: string;
  cognome: string;
  mail: string;
  codiceFiscale?: string | null;
  telefono?: string | null;
  aggiunto?: boolean;
}

export interface Iscrizione {
  anno: number;
  iscritto: boolean;
  tesserato: boolean;
}

export interface IscrizioneSummary {
  iscrittoAnnoCorrente: boolean;
  anniIscritto: number;
  anniIscrizione: number[];
  tesseratoAnnoCorrente: boolean;
  anniTesserato: number;
  anniTesseramento: number[];
}