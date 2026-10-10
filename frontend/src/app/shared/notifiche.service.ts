import { Injectable, signal } from '@angular/core';

export type TipoNotifica = 'errore' | 'info' | 'successo';

export interface Notifica {
  id: number;
  tipo: TipoNotifica;
  testo: string;
}

/** Messaggi brevi in basso a destra (es. "Non hai i permessi per questa operazione"). */
@Injectable({ providedIn: 'root' })
export class NotificheService {
  private contatore = 0;

  readonly notifiche = signal<Notifica[]>([]);

  errore(testo: string): void {
    this.aggiungi('errore', testo);
  }

  info(testo: string): void {
    this.aggiungi('info', testo);
  }

  successo(testo: string): void {
    this.aggiungi('successo', testo);
  }

  chiudi(id: number): void {
    this.notifiche.update((elenco) => elenco.filter((n) => n.id !== id));
  }

  private aggiungi(tipo: TipoNotifica, testo: string): void {
    // Lo stesso messaggio non si ripete se è già visibile (es. più richieste rifiutate insieme).
    if (this.notifiche().some((n) => n.testo === testo)) {
      return;
    }

    const id = ++this.contatore;
    this.notifiche.update((elenco) => [...elenco, { id, tipo, testo }]);
    setTimeout(() => this.chiudi(id), 5000);
  }
}
