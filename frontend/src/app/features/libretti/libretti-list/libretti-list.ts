import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Libretto } from '../libretto.model';
import { LibrettoService } from '../libretto.service';

@Component({
  selector: 'app-libretti-list',
  imports: [RouterLink],
  templateUrl: './libretti-list.html',
  styleUrl: './libretti-list.scss'
})
export class LibrettiList {
  private librettoService = inject(LibrettoService);

  libretti = signal<Libretto[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  constructor() {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.librettoService.getAll().subscribe({
      next: (data) => {
        this.libretti.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare i libretti. Controlla che il backend sia avviato.');
        this.loading.set(false);
      }
    });
  }

  remove(id: number): void {
    if (!confirm('Eliminare questo libretto? Le partiture contenute non verranno eliminate.')) {
      return;
    }

    this.librettoService.delete(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set("Errore durante l'eliminazione.")
    });
  }
}