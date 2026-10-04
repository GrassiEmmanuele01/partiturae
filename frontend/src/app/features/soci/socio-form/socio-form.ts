import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { IscrizioneService } from '../iscrizione.service';
import { Iscrizione, IscrizioneSummary } from '../socio.model';
import { SocioService } from '../socio.service';

@Component({
  selector: 'app-socio-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './socio-form.html',
  styleUrl: './socio-form.scss'
})
export class SocioForm {
  private fb = inject(FormBuilder);
  private socioService = inject(SocioService);
  private iscrizioneService = inject(IscrizioneService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  socioId = signal<number | null>(null);
  loading = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);

  iscrizioni = signal<Iscrizione[]>([]);
  summary = signal<IscrizioneSummary | null>(null);
  nuovoAnno = signal(new Date().getFullYear());
  nuovoIscritto = signal(true);
  nuovoTesserato = signal(false);

  form = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    cognome: ['', Validators.required],
    mail: ['', [Validators.required, Validators.email]],
    codiceFiscale: [''],
    telefono: [''],
    aggiunto: [false]
  });

  constructor() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.socioId.set(id);
      this.loading.set(true);

      this.socioService.getById(id).subscribe({
        next: (socio) => {
          this.form.patchValue({
            nome: socio.nome,
            cognome: socio.cognome,
            mail: socio.mail,
            codiceFiscale: socio.codiceFiscale ?? '',
            telefono: socio.telefono ?? '',
            aggiunto: socio.aggiunto
          });
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Impossibile caricare il socio.');
          this.loading.set(false);
        }
      });

      this.loadIscrizioni();
    }
  }

  loadIscrizioni(): void {
    const id = this.socioId();
    if (!id) return;

    this.iscrizioneService.getAll(id).subscribe({ next: (data) => this.iscrizioni.set(data) });
    this.iscrizioneService.getSummary(id).subscribe({ next: (data) => this.summary.set(data) });
  }

  addIscrizione(): void {
    const id = this.socioId();
    if (!id) return;

    this.iscrizioneService.upsert(id, this.nuovoAnno(), this.nuovoIscritto(), this.nuovoTesserato()).subscribe({
      next: () => this.loadIscrizioni(),
      error: () => this.error.set("Errore durante il salvataggio dell'iscrizione.")
    });
  }

  removeIscrizione(anno: number): void {
    const id = this.socioId();
    if (!id) return;

    if (!confirm(`Eliminare l'iscrizione ${anno}?`)) {
      return;
    }

    this.iscrizioneService.delete(id, anno).subscribe({
      next: () => this.loadIscrizioni(),
      error: () => this.error.set("Errore durante l'eliminazione.")
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.error.set(null);

    const value = this.form.getRawValue();
    const request = {
      nome: value.nome,
      cognome: value.cognome,
      mail: value.mail,
      codiceFiscale: value.codiceFiscale || null,
      telefono: value.telefono || null,
      aggiunto: value.aggiunto
    };

    const id = this.socioId();
    const request$ = id
      ? this.socioService.update(id, request)
      : this.socioService.create(request);

    request$.subscribe({
      next: () => this.router.navigate(['/soci']),
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