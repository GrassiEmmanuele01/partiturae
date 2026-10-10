import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Raccolta } from '../raccolta.model';
import { RaccoltaService } from '../raccolta.service';
import { PuoDirective } from '../../auth/puo.directive';

@Component({
  selector: 'app-raccolte-list',
  imports: [RouterLink, PuoDirective],
  templateUrl: './raccolte-list.html',
  styleUrl: './raccolte-list.scss'
})
export class RaccolteList {
  private raccoltaService = inject(RaccoltaService);

  raccolte = signal<Raccolta[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  constructor() {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.raccoltaService.getAll().subscribe({
      next: (data) => {
        this.raccolte.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare i raccolte. Controlla che il backend sia avviato.');
        this.loading.set(false);
      }
    });
  }

  remove(id: number): void {
    if (!confirm('Eliminare questo raccolta? Le partiture contenute non verranno eliminate.')) {
      return;
    }

    this.raccoltaService.delete(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set("Errore durante l'eliminazione.")
    });
  }
}