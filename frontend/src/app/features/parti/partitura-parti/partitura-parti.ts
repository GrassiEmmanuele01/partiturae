import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Partitura } from '../../partiture/partitura.model';
import { PartituraService } from '../../partiture/partitura.service';
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
  private strumentoFiglioService = inject(StrumentoFiglioService);

  partituraId = Number(this.route.snapshot.paramMap.get('id'));

  partitura = signal<Partitura | null>(null);
  parti = signal<Parte[]>([]);
  strumentiFigli = signal<StrumentoFiglio[]>([]);
  suggestions = signal<StrumentoFiglio[]>([]);
  selectedStrumentoFiglioId = signal<number | null>(null);

  loading = signal(true);
  saving = signal(false);
  error = signal<string | null>(null);
  uploadingId = signal<number | null>(null);

  form = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    strumentoFiglioNome: ['', Validators.required],
    libretto: [false]
  });

  constructor() {
    this.partituraService.getById(this.partituraId).subscribe({
      next: (data) => this.partitura.set(data),
      error: () => this.error.set('Impossibile caricare la partitura.')
    });

    this.strumentoFiglioService.getAll().subscribe({
      next: (data) => this.strumentiFigli.set(data)
    });

    this.loadParti();
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

  onStrumentoInput(): void {
    this.selectedStrumentoFiglioId.set(null);

    const term = this.form.controls.strumentoFiglioNome.value.trim().toLowerCase();
    if (!term) {
      this.suggestions.set([]);
      return;
    }

    this.suggestions.set(
      this.strumentiFigli()
        .filter((sf) => sf.nome.toLowerCase().includes(term))
        .slice(0, 8)
    );
  }

  selectStrumento(sf: StrumentoFiglio): void {
    this.form.patchValue({ strumentoFiglioNome: sf.nome });
    this.selectedStrumentoFiglioId.set(sf.id);
    this.suggestions.set([]);
  }

  hideSuggestionsDelayed(): void {
    setTimeout(() => this.suggestions.set([]), 150);
  }

  addParte(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    const selectedId = this.selectedStrumentoFiglioId();
    const exact = this.strumentiFigli().find(
      (sf) => sf.nome.toLowerCase() === value.strumentoFiglioNome.trim().toLowerCase()
    );
    const strumentoFiglioId = selectedId ?? exact?.id;

    if (!strumentoFiglioId) {
      this.error.set('Strumento non trovato: creane uno prima dalla pagina "Strumenti (dettaglio)".');
      return;
    }

    this.saving.set(true);
    this.error.set(null);

    this.parteService
      .create({
        nome: value.nome,
        partituraId: this.partituraId,
        strumentoFiglioId,
        libretto: value.libretto
      })
      .subscribe({
        next: () => {
          this.saving.set(false);
          this.form.reset({ nome: '', strumentoFiglioNome: '', libretto: false });
          this.selectedStrumentoFiglioId.set(null);
          this.loadParti();
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