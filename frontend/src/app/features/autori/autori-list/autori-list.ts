import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Utilizzo } from '../../../shared/utilizzo.model';
import { Autore } from '../autore.model';
import { AutoreService } from '../autore.service';
import { PuoDirective } from '../../auth/puo.directive';

@Component({
  selector: 'app-autori-list',
  imports: [RouterLink, PuoDirective],
  templateUrl: './autori-list.html',
  styleUrl: './autori-list.scss'
})
export class AutoriList {
  private autoreService = inject(AutoreService);

  autori = signal<Autore[]>([]);
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

  requestRemove(id: number): void {
    this.checkingUtilizzo.set(true);
    this.pendingDeleteId.set(id);

    this.autoreService.getUtilizzo(id).subscribe({
      next: (utilizzo) => {
        this.checkingUtilizzo.set(false);
        if (utilizzo.count === 0) {
          if (confirm('Eliminare questo autore?')) {
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
    this.autoreService.delete(id).subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(err.error?.message ?? "Errore durante l'eliminazione.")
    });
  }
}