import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { AutoreService } from '../autore.service';

@Component({
  selector: 'app-autore-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './autore-form.html',
  styleUrl: './autore-form.scss'
})
export class AutoreForm {
  private fb = inject(FormBuilder);
  private autoreService = inject(AutoreService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  autoreId = signal<number | null>(null);
  loading = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    nominativo: ['', Validators.required]
  });

  constructor() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.autoreId.set(id);
      this.loading.set(true);

      this.autoreService.getById(id).subscribe({
        next: (autore) => {
          this.form.patchValue({ nominativo: autore.nominativo });
          this.loading.set(false);
        },
        error: () => {
          this.error.set("Impossibile caricare l'autore.");
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
    const id = this.autoreId();
    const request$ = id
      ? this.autoreService.update(id, request)
      : this.autoreService.create(request);

    request$.subscribe({
      next: () => this.router.navigate(['/autori']),
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