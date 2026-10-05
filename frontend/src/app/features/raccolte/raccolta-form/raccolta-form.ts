import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { RaccoltaService } from '../raccolta.service';

@Component({
  selector: 'app-raccolta-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './raccolta-form.html',
  styleUrl: './raccolta-form.scss'
})
export class RaccoltaForm {
  private fb = inject(FormBuilder);
  private raccoltaService = inject(RaccoltaService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  raccoltaId = signal<number | null>(null);
  loading = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    anno: this.fb.control<number | null>(new Date().getFullYear()),
    descrizione: ['']
  });

  constructor() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.raccoltaId.set(id);
      this.loading.set(true);

      this.raccoltaService.getById(id).subscribe({
        next: (raccolta) => {
          this.form.patchValue({
            nome: raccolta.nome,
            anno: raccolta.anno,
            descrizione: raccolta.descrizione ?? ''
          });
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Impossibile caricare la raccolta.');
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
    const request = { nome: value.nome, anno: value.anno, descrizione: value.descrizione || null };

    const id = this.raccoltaId();
    const request$ = id
      ? this.raccoltaService.update(id, request)
      : this.raccoltaService.create(request);

    request$.subscribe({
      next: (raccolta) => this.router.navigate(['/raccolte', raccolta.id]),
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