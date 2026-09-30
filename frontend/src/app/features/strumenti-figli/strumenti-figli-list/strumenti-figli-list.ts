import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { StrumentoFiglio } from '../strumento-figlio.model';
import { StrumentoFiglioService } from '../strumento-figlio.service';

@Component({
  selector: 'app-strumenti-figli-list',
  imports: [RouterLink],
  templateUrl: './strumenti-figli-list.html',
  styleUrl: './strumenti-figli-list.scss'
})
export class StrumentiFigliList {
  private strumentoFiglioService = inject(StrumentoFiglioService);

  strumentiFigli = signal<StrumentoFiglio[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  constructor() {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.strumentoFiglioService.getAll().subscribe({
      next: (data) => {
        this.strumentiFigli.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare gli strumenti. Controlla che il backend sia avviato.');
        this.loading.set(false);
      }
    });
  }

  remove(id: number): void {
    if (!confirm('Eliminare questo elemento?')) {
      return;
    }

    this.strumentoFiglioService.delete(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set("Errore durante l'eliminazione. Controlla che non sia usato in qualche parte.")
    });
  }
}