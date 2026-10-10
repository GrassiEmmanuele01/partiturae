import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { NotificheService } from './notifiche.service';

/**
 * Scarica file dall'API (es. i PDF delle parti). Un normale link non può mandare il token di accesso,
 * quindi il file si chiede con una richiesta autenticata e poi si salva con il nome scelto dal server
 * (es. Ottavino1_InnoDiMameli.pdf).
 */
@Injectable({ providedIn: 'root' })
export class FileService {
  private http = inject(HttpClient);
  private notifiche = inject(NotificheService);

  scarica(url: string): void {
    this.http.get(url, { responseType: 'blob', observe: 'response' }).subscribe({
      next: (risposta) => {
        const blob = risposta.body;
        if (!blob) {
          return;
        }

        const nome = this.nomeFile(risposta.headers.get('Content-Disposition')) ?? 'documento.pdf';
        const indirizzo = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = indirizzo;
        link.download = nome;
        document.body.appendChild(link);
        link.click();
        link.remove();
        setTimeout(() => URL.revokeObjectURL(indirizzo), 10_000);
      },
      error: () => this.notifiche.errore('Impossibile scaricare il file.')
    });
  }

  private nomeFile(intestazione: string | null): string | null {
    const trovato = intestazione?.match(/filename\*?=(?:UTF-8'')?"?([^";]+)"?/i);
    return trovato ? decodeURIComponent(trovato[1]) : null;
  }
}
