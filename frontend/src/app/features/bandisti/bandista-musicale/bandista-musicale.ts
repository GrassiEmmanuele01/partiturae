import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Strumento } from '../../strumenti/strumento.model';
import { StrumentoService } from '../../strumenti/strumento.service';
import { Bandista } from '../bandista.model';
import { BandistaService } from '../bandista.service';

@Component({
  selector: 'app-bandista-musicale',
  imports: [RouterLink],
  templateUrl: './bandista-musicale.html',
  styleUrl: './bandista-musicale.scss'
})
export class BandistaMusicale {
  private route = inject(ActivatedRoute);
  private bandistaService = inject(BandistaService);
  private strumentoService = inject(StrumentoService);

  bandistaId = Number(this.route.snapshot.paramMap.get('id'));

  bandista = signal<Bandista | null>(null);
  strumentiDisponibili = signal<Strumento[]>([]);
  suggestions = signal<Strumento[]>([]);
  searchTerm = signal('');

  loading = signal(true);
  error = signal<string | null>(null);

  constructor() {
    this.loadBandista();
    this.strumentoService.getAll().subscribe({
      next: (data) => this.strumentiDisponibili.set(data)
    });
  }

  loadBandista(): void {
    this.bandistaService.getById(this.bandistaId).subscribe({
      next: (data) => {
        this.bandista.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare il bandista.');
        this.loading.set(false);
      }
    });
  }

  onSearchInput(term: string): void {
    this.searchTerm.set(term);
    const lower = term.trim().toLowerCase();
    const giaSelezionati = new Set(this.bandista()?.strumenti.map((s) => s.id) ?? []);

    if (!lower) {
      this.suggestions.set([]);
      return;
    }

    this.suggestions.set(
      this.strumentiDisponibili()
        .filter((s) => !giaSelezionati.has(s.id) && s.nome.toLowerCase().includes(lower))
        .slice(0, 8)
    );
  }

  addStrumento(strumento: Strumento): void {
    const attuali = this.bandista()?.strumenti.map((s) => s.id) ?? [];
    this.saveStrumenti([...attuali, strumento.id]);
    this.searchTerm.set('');
    this.suggestions.set([]);
  }

  removeStrumento(strumentoId: number): void {
    const attuali = this.bandista()?.strumenti.map((s) => s.id) ?? [];
    this.saveStrumenti(attuali.filter((id) => id !== strumentoId));
  }

  private saveStrumenti(ids: number[]): void {
    this.bandistaService.updateStrumenti(this.bandistaId, ids).subscribe({
      next: (data) => this.bandista.set(data),
      error: () => this.error.set('Errore durante il salvataggio degli strumenti.')
    });
  }
}