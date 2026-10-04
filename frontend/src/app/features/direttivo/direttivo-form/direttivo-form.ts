import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { Socio } from '../../soci/socio.model';
import { SocioService } from '../../soci/socio.service';
import { MembroDirettivoService } from '../membro-direttivo.service';

@Component({
  selector: 'app-direttivo-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './direttivo-form.html',
  styleUrl: './direttivo-form.scss'
})
export class DirettivoForm {
  private fb = inject(FormBuilder);
  private membroDirettivoService = inject(MembroDirettivoService);
  private socioService = inject(SocioService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  membroId = signal<number | null>(null);
  loading = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);

  soci = signal<Socio[]>([]);
  suggestions = signal<Socio[]>([]);
  selectedSocioId = signal<number | null>(null);

  form = this.fb.nonNullable.group({
    socioNome: ['', Validators.required],
    carica: ['', Validators.required],
    annoInizio: this.fb.control<number | null>(new Date().getFullYear(), Validators.required),
    mandatoInCorso: [true],
    annoFine: this.fb.control<number | null>(null)
  });

  constructor() {
    this.socioService.getAll().subscribe({ next: (data) => this.soci.set(data) });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      // Non c'è un endpoint di dettaglio singolo: recuperiamo dalla lista completa.
      const id = Number(idParam);
      this.membroId.set(id);
      this.loading.set(true);

      this.membroDirettivoService.getAll().subscribe({
        next: (membri) => {
          const membro = membri.find((m) => m.id === id);
          if (!membro) {
            this.error.set('Membro del direttivo non trovato.');
            this.loading.set(false);
            return;
          }

          this.form.patchValue({
            socioNome: `${membro.socio.nome} ${membro.socio.cognome}`,
            carica: membro.carica,
            annoInizio: membro.annoInizio,
            mandatoInCorso: membro.annoFine === null,
            annoFine: membro.annoFine
          });
          this.selectedSocioId.set(membro.socio.id);
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Impossibile caricare i dati.');
          this.loading.set(false);
        }
      });
    }
  }

  onSocioInput(): void {
    this.selectedSocioId.set(null);

    const term = this.form.controls.socioNome.value.trim().toLowerCase();
    if (!term) {
      this.suggestions.set([]);
      return;
    }

    this.suggestions.set(
      this.soci()
        .filter((s) => `${s.nome} ${s.cognome}`.toLowerCase().includes(term))
        .slice(0, 8)
    );
  }

  selectSocio(socio: Socio): void {
    this.form.patchValue({ socioNome: `${socio.nome} ${socio.cognome}` });
    this.selectedSocioId.set(socio.id);
    this.suggestions.set([]);
  }

  hideSuggestionsDelayed(): void {
    setTimeout(() => this.suggestions.set([]), 150);
  }

  submit(): void {
    const socioId = this.selectedSocioId();

    if (this.form.invalid || !socioId) {
      this.form.markAllAsTouched();
      if (!socioId) {
        this.error.set("Seleziona un socio esistente dall'elenco.");
      }
      return;
    }

    this.saving.set(true);
    this.error.set(null);

    const value = this.form.getRawValue();
    const request = {
      socioId,
      carica: value.carica,
      annoInizio: value.annoInizio as number,
      annoFine: value.mandatoInCorso ? null : value.annoFine
    };

    const id = this.membroId();
    const request$ = id
      ? this.membroDirettivoService.update(id, request)
      : this.membroDirettivoService.create(request);

    request$.subscribe({
      next: () => this.router.navigate(['/direttivo']),
      error: (err) => {
        this.saving.set(false);
        if (err.status === 400 && err.error?.errors) {
          this.error.set(Object.values(err.error.errors).join(', '));
        } else {
          this.error.set('Errore durante il salvataggio.');
        }
      }
    });
  }
}