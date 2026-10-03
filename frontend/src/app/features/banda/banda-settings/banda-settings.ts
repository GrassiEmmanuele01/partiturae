import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { BandaService } from '../banda.service';

@Component({
  selector: 'app-banda-settings',
  imports: [ReactiveFormsModule],
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

  form = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    descrizione: ['']
  });

  constructor() {
    this.bandaService.get().subscribe({
      next: (banda) => {
        this.form.patchValue({
          nome: banda.nome,
          descrizione: banda.descrizione ?? ''
        });
        this.hasLogo.set(true);
        this.loading.set(false);
      },
      error: (err) => {
        // 404 = nessuna banda ancora configurata: è normale alla primissima apertura,
        // lasciamo semplicemente il form vuoto pronto per il primo salvataggio.
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
    this.bandaService.save({ nome: value.nome, descrizione: value.descrizione || null }).subscribe({
      next: () => {
        this.saving.set(false);
        this.success.set('Dati salvati.');
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