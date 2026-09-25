import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Partitura } from '../partitura.model';
import { PartituraService } from '../partitura.service';

@Component({
  selector: 'app-partiture-list',
  imports: [RouterLink],
  templateUrl: './partiture-list.html',
  styleUrl: './partiture-list.scss'
})
export class PartitureList {
  private partituraService = inject(PartituraService);

  partiture = signal<Partitura[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  constructor() {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.partituraService.getAll().subscribe({
      next: (data) => {
        this.partiture.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare le partiture. Controlla che il backend sia avviato.');
        this.loading.set(false);
      }
    });
  }

  remove(id: number): void {
    if (!confirm('Eliminare questa partitura?')) {
      return;
    }

    this.partituraService.delete(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set("Errore durante l'eliminazione.")
    });
  }
}