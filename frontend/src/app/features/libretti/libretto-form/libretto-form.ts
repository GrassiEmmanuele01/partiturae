import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { LibrettoService } from '../libretto.service';

@Component({
  selector: 'app-libretto-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './libretto-form.html',
  styleUrl: './libretto-form.scss'
})
export class LibrettoForm {
  private fb = inject(FormBuilder);
  private librettoService = inject(LibrettoService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  librettoId = signal<number | null>(null);
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
      this.librettoId.set(id);
      this.loading.set(true);

      this.librettoService.getById(id).subscribe({
        next: (libretto) => {
          this.form.patchValue({
            nome: libretto.nome,
            anno: libretto.anno,
            descrizione: libretto.descrizione ?? ''
          });
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Impossibile caricare il libretto.');
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

    const id = this.librettoId();
    const request$ = id
      ? this.librettoService.update(id, request)
      : this.librettoService.create(request);

    request$.subscribe({
      next: (libretto) => this.router.navigate(['/libretti', libretto.id]),
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