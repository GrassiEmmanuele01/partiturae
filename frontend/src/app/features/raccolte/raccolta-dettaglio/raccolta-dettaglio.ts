import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Partitura, TIPO_PARTITURA_LABELS } from '../../partiture/partitura.model';
import { PartituraService } from '../../partiture/partitura.service';
import { Raccolta } from '../raccolta.model';
import { RaccoltaService } from '../raccolta.service';
import { PuoDirective } from '../../auth/puo.directive';

@Component({
  selector: 'app-raccolta-dettaglio',
  imports: [RouterLink, PuoDirective],
  templateUrl: './raccolta-dettaglio.html',
  styleUrl: './raccolta-dettaglio.scss'
})
export class RaccoltaDettaglio {
  private route = inject(ActivatedRoute);
  private raccoltaService = inject(RaccoltaService);
  private partituraService = inject(PartituraService);

  raccoltaId = Number(this.route.snapshot.paramMap.get('id'));

  raccolta = signal<Raccolta | null>(null);
  tutteLePartiture = signal<Partitura[]>([]);
  suggestions = signal<Partitura[]>([]);
  searchTerm = signal('');

  loading = signal(true);
  error = signal<string | null>(null);
  tipoLabels = TIPO_PARTITURA_LABELS;

  constructor() {
    this.load();
    this.partituraService.getAll().subscribe({
      next: (data) => this.tutteLePartiture.set(data)
    });
  }

  load(): void {
    this.loading.set(true);
    this.raccoltaService.getById(this.raccoltaId).subscribe({
      next: (data) => {
        this.raccolta.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare il raccolta.');
        this.loading.set(false);
      }
    });
  }

  onSearchInput(term: string): void {
    this.searchTerm.set(term);
    const lower = term.trim().toLowerCase();
    const giaPresenti = new Set((this.raccolta()?.partiture ?? []).map((lp) => lp.partitura.id));

    if (!lower) {
      this.suggestions.set([]);
      return;
    }

    this.suggestions.set(
      this.tutteLePartiture()
        .filter((p) => !giaPresenti.has(p.id) && p.nome.toLowerCase().includes(lower))
        .slice(0, 8)
    );
  }

  addPartitura(partitura: Partitura): void {
    this.raccoltaService.addPartitura(this.raccoltaId, partitura.id).subscribe({
      next: (data) => {
        this.raccolta.set(data);
        this.searchTerm.set('');
        this.suggestions.set([]);
      },
      error: (err) => this.error.set(err.error?.message ?? 'Errore durante l\'aggiunta.')
    });
  }

  removePartitura(partituraId: number): void {
    if (!confirm('Rimuovere questa partitura dal raccolta?')) {
      return;
    }

    this.raccoltaService.removePartitura(this.raccoltaId, partituraId).subscribe({
      next: (data) => this.raccolta.set(data),
      error: () => this.error.set("Errore durante la rimozione.")
    });
  }

  moveUp(index: number): void {
    if (index === 0) return;
    this.swapAndReorder(index, index - 1);
  }

  moveDown(index: number): void {
    const partiture = this.raccolta()?.partiture ?? [];
    if (index === partiture.length - 1) return;
    this.swapAndReorder(index, index + 1);
  }

  private swapAndReorder(i: number, j: number): void {
    const partiture = [...(this.raccolta()?.partiture ?? [])];
    [partiture[i], partiture[j]] = [partiture[j], partiture[i]];

    const ids = partiture.map((lp) => lp.partitura.id);

    this.raccoltaService.reorder(this.raccoltaId, ids).subscribe({
      next: (data) => this.raccolta.set(data),
      error: () => this.error.set("Errore durante il riordino.")
    });
  }
}