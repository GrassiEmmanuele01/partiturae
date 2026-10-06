import { Component, computed, inject, signal } from '@angular/core';

import { Famiglia, FamigliaRequest } from '../../famiglie/famiglia.model';
import { FamigliaService } from '../../famiglie/famiglia.service';
import { StrumentoFiglio } from '../../strumenti-figli/strumento-figlio.model';
import { StrumentoFiglioService } from '../../strumenti-figli/strumento-figlio.service';
import { Strumento } from '../strumento.model';
import { StrumentoService } from '../strumento.service';

interface FamigliaGroup {
  id: number;
  nome: string;
  strumenti: Strumento[];
}

@Component({
  selector: 'app-strumenti-list',
  templateUrl: './strumenti-list.html',
  styleUrl: './strumenti-list.scss'
})
export class StrumentiList {
  private famigliaService = inject(FamigliaService);
  private strumentoService = inject(StrumentoService);
  private strumentoFiglioService = inject(StrumentoFiglioService);

  famiglie = signal<Famiglia[]>([]);
  strumenti = signal<Strumento[]>([]);
  strumentiFigli = signal<StrumentoFiglio[]>([]);

  loading = signal(true);
  error = signal<string | null>(null);

  expandedIds = signal<Set<number>>(new Set());
  draftSottostrumento = signal<Record<number, string>>({});

  showAddForm = signal(false);
  nuovoNome = signal('');
  nuovaFamigliaNome = signal('');
  famigliaSuggestions = signal<Famiglia[]>([]);
  selectedFamigliaId = signal<number | null>(null);
  pendingNuovaFamiglia = signal<string | null>(null);
  saving = signal(false);

  famiglieConStrumenti = computed<FamigliaGroup[]>(() =>
    this.famiglie().map((f) => ({
      id: f.id,
      nome: f.nome,
      strumenti: this.strumenti().filter((s) => s.famigliaId === f.id)
    }))
  );

  constructor() {
    this.loadAll();
  }

  loadAll(): void {
    this.loading.set(true);
    this.error.set(null);

    this.famigliaService.getAll().subscribe({
      next: (data) => {
        this.famiglie.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare gli strumenti. Controlla che il backend sia avviato.');
        this.loading.set(false);
      }
    });

    this.strumentoService.getAll().subscribe({ next: (data) => this.strumenti.set(data) });
    this.strumentoFiglioService.getAll().subscribe({ next: (data) => this.strumentiFigli.set(data) });
  }

  sottostrumentiDi(strumentoId: number): StrumentoFiglio[] {
    return this.strumentiFigli().filter((sf) => sf.strumentoId === strumentoId);
  }

  isExpanded(strumentoId: number): boolean {
    return this.expandedIds().has(strumentoId);
  }

  toggleExpand(strumentoId: number): void {
    const current = new Set(this.expandedIds());
    if (current.has(strumentoId)) {
      current.delete(strumentoId);
    } else {
      current.add(strumentoId);
    }
    this.expandedIds.set(current);
  }

  onDraftInput(strumentoId: number, value: string): void {
    this.draftSottostrumento.set({ ...this.draftSottostrumento(), [strumentoId]: value });
  }

  addSottostrumento(strumentoId: number): void {
    const nome = (this.draftSottostrumento()[strumentoId] ?? '').trim();
    if (!nome) {
      return;
    }

    this.strumentoFiglioService.create({ nome, strumentoId }).subscribe({
      next: (nuovo) => {
        this.strumentiFigli.set([...this.strumentiFigli(), nuovo]);
        this.draftSottostrumento.set({ ...this.draftSottostrumento(), [strumentoId]: '' });
      },
      error: () => this.error.set('Errore durante la creazione della parte.')
    });
  }

  toggleAddForm(): void {
    this.showAddForm.set(!this.showAddForm());
    this.nuovoNome.set('');
    this.nuovaFamigliaNome.set('');
    this.selectedFamigliaId.set(null);
    this.pendingNuovaFamiglia.set(null);
    this.famigliaSuggestions.set([]);
  }

  onFamigliaInput(value: string): void {
    this.nuovaFamigliaNome.set(value);
    this.selectedFamigliaId.set(null);
    this.pendingNuovaFamiglia.set(null);

    const term = value.trim().toLowerCase();
    if (!term) {
      this.famigliaSuggestions.set([]);
      return;
    }

    this.famigliaSuggestions.set(
      this.famiglie().filter((f) => f.nome.toLowerCase().includes(term)).slice(0, 8)
    );
  }

  selectFamiglia(f: Famiglia): void {
    this.nuovaFamigliaNome.set(f.nome);
    this.selectedFamigliaId.set(f.id);
    this.famigliaSuggestions.set([]);
  }

  hideFamigliaSuggestionsDelayed(): void {
    setTimeout(() => this.famigliaSuggestions.set([]), 150);
  }

  submitAddStrumento(): void {
    const nome = this.nuovoNome().trim();
    const famigliaNome = this.nuovaFamigliaNome().trim();

    if (!nome || !famigliaNome) {
      this.error.set('Nome strumento e famiglia sono obbligatori.');
      return;
    }

    const selectedId = this.selectedFamigliaId();
    if (selectedId) {
      this.createStrumento(nome, selectedId);
      return;
    }

    const esatta = this.famiglie().find((f) => f.nome.toLowerCase() === famigliaNome.toLowerCase());
    if (esatta) {
      this.createStrumento(nome, esatta.id);
      return;
    }

    // Nessuna corrispondenza: chiediamo conferma prima di creare la famiglia.
    this.pendingNuovaFamiglia.set(famigliaNome);
  }

  confermaCreaFamiglia(): void {
    const nome = this.pendingNuovaFamiglia();
    if (!nome) return;

    this.saving.set(true);
    const request: FamigliaRequest = { nome };

    this.famigliaService.create(request).subscribe({
      next: (famiglia) => {
        this.famiglie.set([...this.famiglie(), famiglia]);
        this.pendingNuovaFamiglia.set(null);
        this.createStrumento(this.nuovoNome().trim(), famiglia.id);
      },
      error: () => {
        this.saving.set(false);
        this.error.set('Errore durante la creazione della famiglia.');
      }
    });
  }

  annullaCreaFamiglia(): void {
    this.pendingNuovaFamiglia.set(null);
  }

  private createStrumento(nome: string, famigliaId: number): void {
    this.saving.set(true);
    this.error.set(null);

    this.strumentoService.create({ nome, famigliaId }).subscribe({
      next: (strumento) => {
        this.strumenti.set([...this.strumenti(), strumento]);
        this.saving.set(false);
        this.toggleAddForm();
      },
      error: (err) => {
        this.saving.set(false);
        if (err.status === 400 && err.error?.errors) {
          this.error.set(Object.values(err.error.errors).join(', '));
        } else {
          this.error.set('Errore durante la creazione dello strumento.');
        }
      }
    });
  }
}