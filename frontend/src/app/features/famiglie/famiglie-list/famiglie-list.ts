import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Utilizzo } from '../../../shared/utilizzo.model';
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

  pendingDeleteId = signal<number | null>(null);
  pendingDeleteUtilizzo = signal<Utilizzo | null>(null);
  checkingUtilizzo = signal(false);

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

  requestRemove(id: number): void {
    this.checkingUtilizzo.set(true);
    this.pendingDeleteId.set(id);

    this.famigliaService.getUtilizzo(id).subscribe({
      next: (utilizzo) => {
        this.checkingUtilizzo.set(false);
        if (utilizzo.count === 0) {
          if (confirm('Eliminare questa famiglia?')) {
            this.doDelete(id);
          }
          this.pendingDeleteId.set(null);
        } else {
          this.pendingDeleteUtilizzo.set(utilizzo);
        }
      },
      error: () => {
        this.checkingUtilizzo.set(false);
        this.pendingDeleteId.set(null);
        this.error.set("Errore durante il controllo dell'utilizzo.");
      }
    });
  }

  confirmDeleteAnyway(): void {
    const id = this.pendingDeleteId();
    if (id) {
      this.doDelete(id);
    }
    this.cancelPendingDelete();
  }

  cancelPendingDelete(): void {
    this.pendingDeleteId.set(null);
    this.pendingDeleteUtilizzo.set(null);
  }

  private doDelete(id: number): void {
    this.famigliaService.delete(id).subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(err.error?.message ?? "Errore durante l'eliminazione.")
    });
  }
}