import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Strumento } from '../../strumenti/strumento.model';
import { StrumentoService } from '../../strumenti/strumento.service';
import { Strumentista } from '../strumentista.model';
import { StrumentistaService } from '../strumentista.service';

@Component({
  selector: 'app-strumentista-musicale',
  imports: [RouterLink],
  templateUrl: './strumentista-musicale.html',
  styleUrl: './strumentista-musicale.scss'
})
export class StrumentistaMusicale {
  private route = inject(ActivatedRoute);
  private strumentistaService = inject(StrumentistaService);
  private strumentoService = inject(StrumentoService);

  strumentistaId = Number(this.route.snapshot.paramMap.get('id'));

  strumentista = signal<Strumentista | null>(null);
  strumentiDisponibili = signal<Strumento[]>([]);
  suggestions = signal<Strumento[]>([]);
  searchTerm = signal('');

  loading = signal(true);
  error = signal<string | null>(null);

  constructor() {
    this.loadStrumentista();
    this.strumentoService.getAll().subscribe({
      next: (data) => this.strumentiDisponibili.set(data)
    });
  }

  loadStrumentista(): void {
    this.strumentistaService.getById(this.strumentistaId).subscribe({
      next: (data) => {
        this.strumentista.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare il strumentista.');
        this.loading.set(false);
      }
    });
  }

  onSearchInput(term: string): void {
    this.searchTerm.set(term);
    const lower = term.trim().toLowerCase();
    const giaSelezionati = new Set(this.strumentista()?.strumenti.map((s) => s.id) ?? []);

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
    const attuali = this.strumentista()?.strumenti.map((s) => s.id) ?? [];
    this.saveStrumenti([...attuali, strumento.id]);
    this.searchTerm.set('');
    this.suggestions.set([]);
  }

  removeStrumento(strumentoId: number): void {
    const attuali = this.strumentista()?.strumenti.map((s) => s.id) ?? [];
    this.saveStrumenti(attuali.filter((id) => id !== strumentoId));
  }

  private saveStrumenti(ids: number[]): void {
    this.strumentistaService.updateStrumenti(this.strumentistaId, ids).subscribe({
      next: (data) => this.strumentista.set(data),
      error: () => this.error.set('Errore durante il salvataggio degli strumenti.')
    });
  }
}