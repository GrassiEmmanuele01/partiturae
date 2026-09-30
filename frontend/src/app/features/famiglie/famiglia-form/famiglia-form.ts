import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { FamigliaService } from '../famiglia.service';

@Component({
  selector: 'app-famiglia-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './famiglia-form.html',
  styleUrl: './famiglia-form.scss'
})
export class FamigliaForm {
  private fb = inject(FormBuilder);
  private famigliaService = inject(FamigliaService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  famigliaId = signal<number | null>(null);
  loading = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    nome: ['', Validators.required]
  });

  constructor() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.famigliaId.set(id);
      this.loading.set(true);

      this.famigliaService.getById(id).subscribe({
        next: (famiglia) => {
          this.form.patchValue({ nome: famiglia.nome });
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Impossibile caricare la famiglia.');
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

    const request = this.form.getRawValue();
    const id = this.famigliaId();
    const request$ = id
      ? this.famigliaService.update(id, request)
      : this.famigliaService.create(request);

    request$.subscribe({
      next: () => this.router.navigate(['/famiglie']),
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