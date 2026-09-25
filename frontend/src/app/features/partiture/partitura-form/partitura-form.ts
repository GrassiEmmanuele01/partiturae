import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { Autore } from '../../autori/autore.model';
import { AutoreService } from '../../autori/autore.service';
import { PartituraService } from '../partitura.service';

@Component({
  selector: 'app-partitura-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './partitura-form.html',
  styleUrl: './partitura-form.scss'
})
export class PartituraForm {
  private fb = inject(FormBuilder);
  private partituraService = inject(PartituraService);
  private autoreService = inject(AutoreService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  partituraId = signal<number | null>(null);
  autori = signal<Autore[]>([]);
  loading = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    descrizione: [''],
    anno: this.fb.control<number | null>(null),
    autoreId: this.fb.control<number | null>(null, Validators.required)
  });

  constructor() {
    this.autoreService.getAll().subscribe({
      next: (data) => this.autori.set(data),
      error: () => this.error.set('Impossibile caricare gli autori.')
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.partituraId.set(id);
      this.loading.set(true);

      this.partituraService.getById(id).subscribe({
        next: (partitura) => {
          this.form.patchValue({
            nome: partitura.nome,
            descrizione: partitura.descrizione ?? '',
            anno: partitura.anno,
            autoreId: partitura.autore.id
          });
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Impossibile caricare la partitura.');
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
      descrizione: value.descrizione || null,
      anno: value.anno,
      autoreId: value.autoreId as number
    };

    const id = this.partituraId();
    const request$ = id
      ? this.partituraService.update(id, request)
      : this.partituraService.create(request);

    request$.subscribe({
      next: () => this.router.navigate(['/partiture']),
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