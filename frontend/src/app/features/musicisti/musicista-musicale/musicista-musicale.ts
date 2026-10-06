import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Strumento } from '../../strumenti/strumento.model';
import { StrumentoService } from '../../strumenti/strumento.service';
import { Musicista } from '../musicista.model';
import { MusicistaService } from '../musicista.service';

@Component({
  selector: 'app-musicista-musicale',
  imports: [RouterLink],
  templateUrl: './musicista-musicale.html',
  styleUrl: './musicista-musicale.scss'
})
export class MusicistaMusicale {
  private route = inject(ActivatedRoute);
  private musicistaService = inject(MusicistaService);
  private strumentoService = inject(StrumentoService);

  musicistaId = Number(this.route.snapshot.paramMap.get('id'));

  musicista = signal<Musicista | null>(null);
  strumentiDisponibili = signal<Strumento[]>([]);
  suggestions = signal<Strumento[]>([]);
  searchTerm = signal('');

  loading = signal(true);
  error = signal<string | null>(null);

  constructor() {
    this.loadMusicista();
    this.strumentoService.getAll().subscribe({
      next: (data) => this.strumentiDisponibili.set(data)
    });
  }

  loadMusicista(): void {
    this.musicistaService.getById(this.musicistaId).subscribe({
      next: (data) => {
        this.musicista.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare il musicista.');
        this.loading.set(false);
      }
    });
  }

  onSearchInput(term: string): void {
    this.searchTerm.set(term);
    const lower = term.trim().toLowerCase();
    const giaSelezionati = new Set(this.musicista()?.strumenti.map((s) => s.id) ?? []);

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
    const attuali = this.musicista()?.strumenti.map((s) => s.id) ?? [];
    this.saveStrumenti([...attuali, strumento.id]);
    this.searchTerm.set('');
    this.suggestions.set([]);
  }

  removeStrumento(strumentoId: number): void {
    const attuali = this.musicista()?.strumenti.map((s) => s.id) ?? [];
    this.saveStrumenti(attuali.filter((id) => id !== strumentoId));
  }

  private saveStrumenti(ids: number[]): void {
    this.musicistaService.updateStrumenti(this.musicistaId, ids).subscribe({
      next: (data) => this.musicista.set(data),
      error: () => this.error.set('Errore durante il salvataggio degli strumenti.')
    });
  }
}