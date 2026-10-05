import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Strumentista } from '../strumentista.model';
import { StrumentistaService } from '../strumentista.service';

@Component({
  selector: 'app-strumentisti-list',
  imports: [RouterLink],
  templateUrl: './strumentisti-list.html',
  styleUrl: './strumentisti-list.scss'
})
export class StrumentistiList {
  private strumentistaService = inject(StrumentistaService);

  strumentisti = signal<Strumentista[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  annoCorrente = new Date().getFullYear();

  constructor() {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.strumentistaService.getAll().subscribe({
      next: (data) => {
        this.strumentisti.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare i strumentisti. Controlla che il backend sia avviato.');
        this.loading.set(false);
      }
    });
  }

  remove(id: number): void {
    if (!confirm('Eliminare questo strumentista?')) {
      return;
    }

    this.strumentistaService.delete(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set("Errore durante l'eliminazione.")
    });
  }
}