import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Musicista } from '../musicista.model';
import { MusicistaService } from '../musicista.service';

@Component({
  selector: 'app-musicisti-list',
  imports: [RouterLink],
  templateUrl: './musicisti-list.html',
  styleUrl: './musicisti-list.scss'
})
export class MusicistiList {
  private musicistaService = inject(MusicistaService);

  musicisti = signal<Musicista[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  annoCorrente = new Date().getFullYear();

  constructor() {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.musicistaService.getAll().subscribe({
      next: (data) => {
        this.musicisti.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare i musicisti. Controlla che il backend sia avviato.');
        this.loading.set(false);
      }
    });
  }

  remove(id: number): void {
    if (!confirm('Eliminare questo musicista?')) {
      return;
    }

    this.musicistaService.delete(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set("Errore durante l'eliminazione.")
    });
  }
}