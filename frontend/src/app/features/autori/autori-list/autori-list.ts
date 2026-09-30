import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Autore } from '../autore.model';
import { AutoreService } from '../autore.service';

@Component({
  selector: 'app-autori-list',
  imports: [RouterLink],
  templateUrl: './autori-list.html',
  styleUrl: './autori-list.scss'
})
export class AutoriList {
  private autoreService = inject(AutoreService);

  autori = signal<Autore[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  constructor() {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.autoreService.getAll().subscribe({
      next: (data) => {
        this.autori.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare gli autori. Controlla che il backend sia avviato.');
        this.loading.set(false);
      }
    });
  }

  remove(id: number): void {
    if (!confirm('Eliminare questo autore?')) {
      return;
    }

    this.autoreService.delete(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set("Errore durante l'eliminazione. Controlla che non sia usato in qualche partitura.")
    });
  }
}