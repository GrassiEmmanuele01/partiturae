import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Bandista } from '../bandista.model';
import { BandistaService } from '../bandista.service';

@Component({
  selector: 'app-bandisti-list',
  imports: [RouterLink],
  templateUrl: './bandisti-list.html',
  styleUrl: './bandisti-list.scss'
})
export class BandistiList {
  private bandistaService = inject(BandistaService);

  bandisti = signal<Bandista[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  constructor() {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.bandistaService.getAll().subscribe({
      next: (data) => {
        this.bandisti.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare i bandisti. Controlla che il backend sia avviato.');
        this.loading.set(false);
      }
    });
  }

  remove(id: number): void {
    if (!confirm('Eliminare questo bandista?')) {
      return;
    }

    this.bandistaService.delete(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set("Errore durante l'eliminazione.")
    });
  }
}