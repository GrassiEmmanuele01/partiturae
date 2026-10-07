export interface UtilizzoElemento {
  tipo: string;
  nome: string;
}

export interface Utilizzo {
  count: number;
  elementi: UtilizzoElemento[];
}