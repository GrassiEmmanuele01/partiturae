import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Partitura, TIPO_PARTITURA_LABELS } from '../../partiture/partitura.model';
import { PartituraService } from '../../partiture/partitura.service';
import { Libretto } from '../libretto.model';
import { LibrettoService } from '../libretto.service';

@Component({
  selector: 'app-libretto-dettaglio',
  imports: [RouterLink],
  templateUrl: './libretto-dettaglio.html',
  styleUrl: './libretto-dettaglio.scss'
})
export class LibrettoDettaglio {
  private route = inject(ActivatedRoute);
  private librettoService = inject(LibrettoService);
  private partituraService = inject(PartituraService);

  librettoId = Number(this.route.snapshot.paramMap.get('id'));

  libretto = signal<Libretto | null>(null);
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
    this.librettoService.getById(this.librettoId).subscribe({
      next: (data) => {
        this.libretto.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare il libretto.');
        this.loading.set(false);
      }
    });
  }

  onSearchInput(term: string): void {
    this.searchTerm.set(term);
    const lower = term.trim().toLowerCase();
    const giaPresenti = new Set((this.libretto()?.partiture ?? []).map((lp) => lp.partitura.id));

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
    this.librettoService.addPartitura(this.librettoId, partitura.id).subscribe({
      next: (data) => {
        this.libretto.set(data);
        this.searchTerm.set('');
        this.suggestions.set([]);
      },
      error: (err) => this.error.set(err.error?.message ?? 'Errore durante l\'aggiunta.')
    });
  }

  removePartitura(partituraId: number): void {
    if (!confirm('Rimuovere questa partitura dal libretto?')) {
      return;
    }

    this.librettoService.removePartitura(this.librettoId, partituraId).subscribe({
      next: (data) => this.libretto.set(data),
      error: () => this.error.set("Errore durante la rimozione.")
    });
  }

  moveUp(index: number): void {
    if (index === 0) return;
    this.swapAndReorder(index, index - 1);
  }

  moveDown(index: number): void {
    const partiture = this.libretto()?.partiture ?? [];
    if (index === partiture.length - 1) return;
    this.swapAndReorder(index, index + 1);
  }

  private swapAndReorder(i: number, j: number): void {
    const partiture = [...(this.libretto()?.partiture ?? [])];
    [partiture[i], partiture[j]] = [partiture[j], partiture[i]];

    const ids = partiture.map((lp) => lp.partitura.id);

    this.librettoService.reorder(this.librettoId, ids).subscribe({
      next: (data) => this.libretto.set(data),
      error: () => this.error.set("Errore durante il riordino.")
    });
  }
}