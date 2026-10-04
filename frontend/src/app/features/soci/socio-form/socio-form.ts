import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

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
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  socioId = signal<number | null>(null);
  loading = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);

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