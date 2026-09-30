import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Famiglia } from '../famiglia.model';
import { FamigliaService } from '../famiglia.service';

@Component({
  selector: 'app-famiglie-list',
  imports: [RouterLink],
  templateUrl: './famiglie-list.html',
  styleUrl: './famiglie-list.scss'
})
export class FamiglieList {
  private famigliaService = inject(FamigliaService);

  famiglie = signal<Famiglia[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  constructor() {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.famigliaService.getAll().subscribe({
      next: (data) => {
        this.famiglie.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare le famiglie. Controlla che il backend sia avviato.');
        this.loading.set(false);
      }
    });
  }

  remove(id: number): void {
    if (!confirm('Eliminare questa famiglia?')) {
      return;
    }

    this.famigliaService.delete(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set("Errore durante l'eliminazione. Controlla che non sia usata da qualche strumento.")
    });
  }
}