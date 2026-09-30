import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Strumento } from '../strumento.model';
import { StrumentoService } from '../strumento.service';

@Component({
  selector: 'app-strumenti-list',
  imports: [RouterLink],
  templateUrl: './strumenti-list.html',
  styleUrl: './strumenti-list.scss'
})
export class StrumentiList {
  private strumentoService = inject(StrumentoService);

  strumenti = signal<Strumento[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  constructor() {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.strumentoService.getAll().subscribe({
      next: (data) => {
        this.strumenti.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare gli strumenti. Controlla che il backend sia avviato.');
        this.loading.set(false);
      }
    });
  }

  remove(id: number): void {
    if (!confirm('Eliminare questo strumento?')) {
      return;
    }

    this.strumentoService.delete(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set("Errore durante l'eliminazione. Controlla che non sia usato altrove.")
    });
  }
}