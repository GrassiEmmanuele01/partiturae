import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { BandistaService } from '../bandista.service';

@Component({
  selector: 'app-bandista-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './bandista-form.html',
  styleUrl: './bandista-form.scss'
})
export class BandistaForm {
  private fb = inject(FormBuilder);
  private bandistaService = inject(BandistaService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  bandistaId = signal<number | null>(null);
  loading = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    cognome: ['', Validators.required],
    mail: ['', [Validators.required, Validators.email]],
    codiceFiscale: [''],
    telefono: ['']
  });

  constructor() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.bandistaId.set(id);
      this.loading.set(true);

      this.bandistaService.getById(id).subscribe({
        next: (bandista) => {
          this.form.patchValue({
            nome: bandista.nome,
            cognome: bandista.cognome,
            mail: bandista.mail,
            codiceFiscale: bandista.codiceFiscale ?? '',
            telefono: bandista.telefono ?? ''
          });
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Impossibile caricare il bandista.');
          this.loading.set(false);
        }
      });
    }
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
      telefono: value.telefono || null
    };

    const id = this.bandistaId();
    const request$ = id
      ? this.bandistaService.update(id, request)
      : this.bandistaService.create(request);

    request$.subscribe({
      next: () => this.router.navigate(['/bandisti']),
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