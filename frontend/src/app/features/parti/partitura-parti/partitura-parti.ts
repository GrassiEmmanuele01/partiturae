import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Observable, map, of, switchMap } from 'rxjs';

import { Famiglia } from '../../famiglie/famiglia.model';
import { FamigliaService } from '../../famiglie/famiglia.service';
import { Partitura } from '../../partiture/partitura.model';
import { PartituraService } from '../../partiture/partitura.service';
import { Strumento } from '../../strumenti/strumento.model';
import { StrumentoService } from '../../strumenti/strumento.service';
import { StrumentoFiglio } from '../../strumenti-figli/strumento-figlio.model';
import { StrumentoFiglioService } from '../../strumenti-figli/strumento-figlio.service';
import { Parte } from '../parte.model';
import { ParteService } from '../parte.service';

@Component({
  selector: 'app-partitura-parti',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './partitura-parti.html',
  styleUrl: './partitura-parti.scss'
})
export class PartituraParti {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private partituraService = inject(PartituraService);
  private parteService = inject(ParteService);
  private famigliaService = inject(FamigliaService);
  private strumentoService = inject(StrumentoService);
  private strumentoFiglioService = inject(StrumentoFiglioService);

  partituraId = Number(this.route.snapshot.paramMap.get('id'));

  partitura = signal<Partitura | null>(null);
  parti = signal<Parte[]>([]);

  famiglie = signal<Famiglia[]>([]);
  strumenti = signal<Strumento[]>([]);
  strumentiFigli = signal<StrumentoFiglio[]>([]);

  loading = signal(true);
  saving = signal(false);
  error = signal<string | null>(null);
  uploadingId = signal<number | null>(null);

  pdfFile = signal<File | null>(null);

  form = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    strumentoFiglioNome: ['', Validators.required],
    libretto: [false]
  });

  // --- Livello 1: StrumentoFiglio (es. "Flicorno tenore 2") ---
  sfSuggestions = signal<StrumentoFiglio[]>([]);
  selectedStrumentoFiglioId = signal<number | null>(null);

  creatingStrumentoFiglio = computed(() => {
    const term = this.form.controls.strumentoFiglioNome.value?.trim();
    if (!term || this.selectedStrumentoFiglioId()) return false;
    return !this.strumentiFigli().some((sf) => sf.nome.toLowerCase() === term.toLowerCase());
  });

  // --- Livello 2 (cascata, solo se livello 1 va creato): Strumento (es. "Flicorno tenore") ---
  strumentoNome = signal('');
  strumentoSuggestions = signal<Strumento[]>([]);
  selectedStrumentoId = signal<number | null>(null);

  creatingStrumento = computed(() => {
    const term = this.strumentoNome().trim();
    if (!term || this.selectedStrumentoId()) return false;
    return !this.strumenti().some((s) => s.nome.toLowerCase() === term.toLowerCase());
  });

  // --- Livello 3 (cascata, solo se livello 2 va creato): Famiglia (es. "Ottoni") ---
  famigliaNome = signal('');
  famigliaSuggestions = signal<Famiglia[]>([]);
  selectedFamigliaId = signal<number | null>(null);

  constructor() {
    this.partituraService.getById(this.partituraId).subscribe({
      next: (data) => this.partitura.set(data),
      error: () => this.error.set('Impossibile caricare la partitura.')
    });

    this.refreshAnagrafiche();
    this.loadParti();
  }

  private refreshAnagrafiche(): void {
    this.famigliaService.getAll().subscribe({ next: (data) => this.famiglie.set(data) });
    this.strumentoService.getAll().subscribe({ next: (data) => this.strumenti.set(data) });
    this.strumentoFiglioService.getAll().subscribe({ next: (data) => this.strumentiFigli.set(data) });
  }

  loadParti(): void {
    this.loading.set(true);
    this.parteService.getByPartitura(this.partituraId).subscribe({
      next: (data) => {
        this.parti.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare le parti.');
        this.loading.set(false);
      }
    });
  }

  // --- Autocomplete livello 1 ---
  onStrumentoFiglioInput(): void {
    this.selectedStrumentoFiglioId.set(null);
    const term = this.form.controls.strumentoFiglioNome.value.trim().toLowerCase();
    this.sfSuggestions.set(
      term ? this.strumentiFigli().filter((sf) => sf.nome.toLowerCase().includes(term)).slice(0, 8) : []
    );
  }

  selectStrumentoFiglio(sf: StrumentoFiglio): void {
    this.form.patchValue({ strumentoFiglioNome: sf.nome });
    this.selectedStrumentoFiglioId.set(sf.id);
    this.sfSuggestions.set([]);
    this.resetCascade();
  }

  hideSfSuggestionsDelayed(): void {
    setTimeout(() => this.sfSuggestions.set([]), 150);
  }

  // --- Autocomplete livello 2 ---
  onStrumentoInput(value: string): void {
    this.strumentoNome.set(value);
    this.selectedStrumentoId.set(null);
    const term = value.trim().toLowerCase();
    this.strumentoSuggestions.set(
      term ? this.strumenti().filter((s) => s.nome.toLowerCase().includes(term)).slice(0, 8) : []
    );
  }

  selectStrumento(s: Strumento): void {
    this.strumentoNome.set(s.nome);
    this.selectedStrumentoId.set(s.id);
    this.strumentoSuggestions.set([]);
  }

  hideStrumentoSuggestionsDelayed(): void {
    setTimeout(() => this.strumentoSuggestions.set([]), 150);
  }

  // --- Autocomplete livello 3 ---
  onFamigliaInput(value: string): void {
    this.famigliaNome.set(value);
    this.selectedFamigliaId.set(null);
    const term = value.trim().toLowerCase();
    this.famigliaSuggestions.set(
      term ? this.famiglie().filter((f) => f.nome.toLowerCase().includes(term)).slice(0, 8) : []
    );
  }

  selectFamiglia(f: Famiglia): void {
    this.famigliaNome.set(f.nome);
    this.selectedFamigliaId.set(f.id);
    this.famigliaSuggestions.set([]);
  }

  hideFamigliaSuggestionsDelayed(): void {
    setTimeout(() => this.famigliaSuggestions.set([]), 150);
  }

  private resetCascade(): void {
    this.strumentoNome.set('');
    this.selectedStrumentoId.set(null);
    this.famigliaNome.set('');
    this.selectedFamigliaId.set(null);
  }

  onFileSelectedForNewParte(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.pdfFile.set(input.files?.[0] ?? null);
  }

  addParte(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    if (this.creatingStrumentoFiglio() && !this.selectedStrumentoId()) {
      const sNome = this.strumentoNome().trim();
      if (!sNome) {
        this.error.set('Indica a quale strumento appartiene.');
        return;
      }
      const sEsiste = this.strumenti().some((s) => s.nome.toLowerCase() === sNome.toLowerCase());
      if (!sEsiste && !this.famigliaNome().trim()) {
        this.error.set('Indica la famiglia del nuovo strumento.');
        return;
      }
    }

    this.saving.set(true);
    this.error.set(null);

    this.resolveStrumentoFiglioId().subscribe({
      next: (strumentoFiglioId) => {
        const value = this.form.getRawValue();

        this.parteService
          .create({
            nome: value.nome,
            partituraId: this.partituraId,
            strumentoFiglioId,
            libretto: value.libretto
          })
          .subscribe({
            next: (parte) => {
              const file = this.pdfFile();
              if (file) {
                this.parteService.uploadPdf(parte.id, file).subscribe({
                  next: () => this.finishAddParte(),
                  error: () => {
                    this.finishAddParte();
                    this.error.set('Parte creata, ma il caricamento del PDF è fallito: riprova dalla tabella sotto.');
                  }
                });
              } else {
                this.finishAddParte();
              }
            },
            error: (err) => {
              this.saving.set(false);
              if (err.status === 400 && err.error?.errors) {
                this.error.set(Object.values(err.error.errors).join(', '));
              } else {
                this.error.set('Errore durante il salvataggio della parte.');
              }
            }
          });
      },
      error: () => {
        this.saving.set(false);
        this.error.set('Errore durante la creazione dello strumento.');
      }
    });
  }

  private finishAddParte(): void {
    this.saving.set(false);
    this.form.reset({ nome: '', strumentoFiglioNome: '', libretto: false });
    this.selectedStrumentoFiglioId.set(null);
    this.pdfFile.set(null);
    this.resetCascade();
    this.refreshAnagrafiche();
    this.loadParti();
  }

  // Risolve l'id dello StrumentoFiglio, creando a cascata Strumento/Famiglia se mancanti
  private resolveStrumentoFiglioId(): Observable<number> {
    const sfId = this.selectedStrumentoFiglioId();
    if (sfId) {
      return of(sfId);
    }

    const sfNome = this.form.controls.strumentoFiglioNome.value.trim();
    const sfExact = this.strumentiFigli().find((sf) => sf.nome.toLowerCase() === sfNome.toLowerCase());
    if (sfExact) {
      return of(sfExact.id);
    }

    return this.resolveStrumentoId().pipe(
      switchMap((strumentoId) =>
        this.strumentoFiglioService.create({ nome: sfNome, strumentoId }).pipe(map((sf) => sf.id))
      )
    );
  }

  private resolveStrumentoId(): Observable<number> {
    const sId = this.selectedStrumentoId();
    if (sId) {
      return of(sId);
    }

    const sNome = this.strumentoNome().trim();
    const sExact = this.strumenti().find((s) => s.nome.toLowerCase() === sNome.toLowerCase());
    if (sExact) {
      return of(sExact.id);
    }

    return this.resolveFamigliaId().pipe(
      switchMap((famigliaId) => this.strumentoService.create({ nome: sNome, famigliaId }).pipe(map((s) => s.id)))
    );
  }

  private resolveFamigliaId(): Observable<number> {
    const fId = this.selectedFamigliaId();
    if (fId) {
      return of(fId);
    }

    const fNome = this.famigliaNome().trim();
    const fExact = this.famiglie().find((f) => f.nome.toLowerCase() === fNome.toLowerCase());
    if (fExact) {
      return of(fExact.id);
    }

    return this.famigliaService.create({ nome: fNome }).pipe(map((f) => f.id));
  }

  removeParte(id: number): void {
    if (!confirm('Eliminare questa parte?')) {
      return;
    }

    this.parteService.delete(id).subscribe({
      next: () => this.loadParti(),
      error: () => this.error.set("Errore durante l'eliminazione.")
    });
  }

  onFileSelected(event: Event, parteId: number): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      return;
    }

    this.uploadingId.set(parteId);
    this.parteService.uploadPdf(parteId, file).subscribe({
      next: () => {
        this.uploadingId.set(null);
        input.value = '';
        this.loadParti();
      },
      error: () => {
        this.uploadingId.set(null);
        this.error.set('Errore durante il caricamento del PDF.');
      }
    });
  }

  pdfUrl(id: number): string {
    return this.parteService.pdfUrl(id);
  }
}