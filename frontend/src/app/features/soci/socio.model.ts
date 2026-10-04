export interface Socio {
  id: number;
  nome: string;
  cognome: string;
  mail: string;
  codiceFiscale: string | null;
  telefono: string | null;
  aggiunto: boolean;
}

export interface SocioRequest {
  nome: string;
  cognome: string;
  mail: string;
  codiceFiscale?: string | null;
  telefono?: string | null;
  aggiunto?: boolean;
}