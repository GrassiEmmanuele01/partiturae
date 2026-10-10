import { Component, DestroyRef, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { FormazioneService } from '../formazione.service';
import { MembroDirettivo } from '../../direttivo/membro-direttivo.model';

@Component({
  selector: 'app-formazione-settings',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './formazione-settings.html',
  styleUrl: './formazione-settings.scss'
})
export class FormazioneSettings {
  private fb = inject(FormBuilder);
  private formazioneService = inject(FormazioneService);
  private destroyRef = inject(DestroyRef);

  loading = signal(true);
  saving = signal(false);
  uploadingLogo = signal(false);
  logoSrc = signal<string | null>(null);
  error = signal<string | null>(null);
  success = signal<string | null>(null);

  numeroAssociati = signal(0);
  direttivoInCarica = signal<MembroDirettivo[]>([]);

  form = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    descrizione: [''],
    annoFondazione: this.fb.control<number | null>(null),
    indirizzo: [''],
    codiceFiscale: [''],
    email: ['', [Validators.email]],
    telefono: [''],
    sitoWeb: ['']
  });

  constructor() {
    this.destroyRef.onDestroy(() => this.rilasciaLogo());
    this.load();
  }

  load(): void {
    this.loading.set(true);

    this.formazioneService.get().subscribe({
      next: (formazione) => {
        this.form.patchValue({
          nome: formazione.nome,
          descrizione: formazione.descrizione ?? '',
          annoFondazione: formazione.annoFondazione,
          indirizzo: formazione.indirizzo ?? '',
          codiceFiscale: formazione.codiceFiscale ?? '',
          email: formazione.email ?? '',
          telefono: formazione.telefono ?? '',
          sitoWeb: formazione.sitoWeb ?? ''
        });
        this.numeroAssociati.set(formazione.numeroAssociatiAnnoCorrente);
        this.direttivoInCarica.set(formazione.direttivoInCarica);
        this.loading.set(false);
        this.caricaLogo();
      },
      error: (err) => {
        // 404 = nessuna formazione ancora configurata: è normale alla primissima apertura.
        if (err.status !== 404) {
          this.error.set('Impossibile caricare i dati della formazione.');
        }
        this.loading.set(false);
      }
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.error.set(null);
    this.success.set(null);

    const value = this.form.getRawValue();
    this.formazioneService
      .save({
        nome: value.nome,
        descrizione: value.descrizione || null,
        annoFondazione: value.annoFondazione,
        indirizzo: value.indirizzo || null,
        codiceFiscale: value.codiceFiscale || null,
        email: value.email || null,
        telefono: value.telefono || null,
        sitoWeb: value.sitoWeb || null
      })
      .subscribe({
        next: () => {
          this.saving.set(false);
          this.success.set('Dati salvati.');
          this.load();
        },
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

  onLogoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      return;
    }

    this.uploadingLogo.set(true);
    this.error.set(null);

    this.formazioneService.uploadLogo(file).subscribe({
      next: () => {
        this.uploadingLogo.set(false);
        input.value = '';
        this.caricaLogo();
      },
      error: () => {
        this.uploadingLogo.set(false);
        this.error.set('Errore durante il caricamento del logo.');
      }
    });
  }

  private caricaLogo(): void {
    this.formazioneService.caricaLogo().subscribe({
      next: (blob) => {
        this.rilasciaLogo();
        this.logoSrc.set(URL.createObjectURL(blob));
      },
      // 404 = nessun logo caricato
      error: () => {
        this.rilasciaLogo();
        this.logoSrc.set(null);
      }
    });
  }

  private rilasciaLogo(): void {
    const attuale = this.logoSrc();
    if (attuale) {
      URL.revokeObjectURL(attuale);
    }
  }
}