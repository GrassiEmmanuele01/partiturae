import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { CARICA_DIRETTIVO_LABELS, MembroDirettivo } from '../membro-direttivo.model';
import { MembroDirettivoService } from '../membro-direttivo.service';
import { PuoDirective } from '../../auth/puo.directive';

@Component({
  selector: 'app-direttivo-list',
  imports: [RouterLink, PuoDirective],
  templateUrl: './direttivo-list.html',
  styleUrl: './direttivo-list.scss'
})
export class DirettivoList {
  private membroDirettivoService = inject(MembroDirettivoService);

  membri = signal<MembroDirettivo[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  caricaLabels = CARICA_DIRETTIVO_LABELS;

  constructor() {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.membroDirettivoService.getAll().subscribe({
      next: (data) => {
        this.membri.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare il direttivo. Controlla che il backend sia avviato.');
        this.loading.set(false);
      }
    });
  }

  remove(id: number): void {
    if (!confirm('Eliminare questa carica?')) {
      return;
    }

    this.membroDirettivoService.delete(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set("Errore durante l'eliminazione.")
    });
  }
}