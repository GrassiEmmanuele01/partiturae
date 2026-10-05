import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { BandaService } from '../banda.service';
import { MembroDirettivo } from '../../direttivo/membro-direttivo.model';

@Component({
  selector: 'app-banda-settings',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './banda-settings.html',
  styleUrl: './banda-settings.scss'
})
export class BandaSettings {
  private fb = inject(FormBuilder);
  private bandaService = inject(BandaService);

  loading = signal(true);
  saving = signal(false);
  uploadingLogo = signal(false);
  hasLogo = signal(false);
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
    this.load();
  }

  load(): void {
    this.loading.set(true);

    this.bandaService.get().subscribe({
      next: (banda) => {
        this.form.patchValue({
          nome: banda.nome,
          descrizione: banda.descrizione ?? '',
          annoFondazione: banda.annoFondazione,
          indirizzo: banda.indirizzo ?? '',
          codiceFiscale: banda.codiceFiscale ?? '',
          email: banda.email ?? '',
          telefono: banda.telefono ?? '',
          sitoWeb: banda.sitoWeb ?? ''
        });
        this.numeroAssociati.set(banda.numeroAssociatiAnnoCorrente);
        this.direttivoInCarica.set(banda.direttivoInCarica);
        this.hasLogo.set(true);
        this.loading.set(false);
      },
      error: (err) => {
        // 404 = nessuna banda ancora configurata: è normale alla primissima apertura.
        if (err.status !== 404) {
          this.error.set('Impossibile caricare i dati della banda.');
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
    this.bandaService
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

    this.bandaService.uploadLogo(file).subscribe({
      next: () => {
        this.uploadingLogo.set(false);
        this.hasLogo.set(true);
        input.value = '';
      },
      error: () => {
        this.uploadingLogo.set(false);
        this.error.set('Errore durante il caricamento del logo.');
      }
    });
  }

  logoUrl(): string {
    return this.bandaService.logoUrl();
  }
}