import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Observable, map, of } from 'rxjs';

import { Autore } from '../../autori/autore.model';
import { AutoreService } from '../../autori/autore.service';
import { PartituraService } from '../partitura.service';
import { TIPO_PARTITURA_LABELS, TipoPartitura } from '../partitura.model';

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
  suggestions = signal<Autore[]>([]);
  selectedAutoreId = signal<number | null>(null);
  loading = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);

  tipi = Object.entries(TIPO_PARTITURA_LABELS) as [TipoPartitura, string][];

  form = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    descrizione: [''],
    anno: this.fb.control<number | null>(null),
    tipo: this.fb.control<TipoPartitura | null>(null, Validators.required),
    autoreNome: ['', Validators.required]
  });

  willCreateNewAutore = computed(() => {
    const term = this.form.controls.autoreNome.value?.trim();
    if (!term || this.selectedAutoreId()) {
      return false;
    }
    return !this.autori().some((a) => a.nominativo.toLowerCase() === term.toLowerCase());
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
            tipo: partitura.tipo,
            autoreNome: partitura.autore.nominativo
          });
          this.selectedAutoreId.set(partitura.autore.id);
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Impossibile caricare la partitura.');
          this.loading.set(false);
        }
      });
    }
  }

  onAutoreInput(): void {
    this.selectedAutoreId.set(null);

    const term = this.form.controls.autoreNome.value.trim().toLowerCase();
    if (!term) {
      this.suggestions.set([]);
      return;
    }

    this.suggestions.set(
      this.autori()
        .filter((a) => a.nominativo.toLowerCase().includes(term))
        .slice(0, 8)
    );
  }

  selectAutore(autore: Autore): void {
    this.form.patchValue({ autoreNome: autore.nominativo });
    this.selectedAutoreId.set(autore.id);
    this.suggestions.set([]);
  }

  hideSuggestionsDelayed(): void {
    setTimeout(() => this.suggestions.set([]), 150);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.error.set(null);

    const value = this.form.getRawValue();

    this.resolveAutoreId(value.autoreNome.trim()).subscribe({
      next: (autoreId) => {
        const request = {
          nome: value.nome,
          descrizione: value.descrizione || null,
          anno: value.anno,
          tipo: value.tipo as TipoPartitura,
          autoreId
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
      },
      error: () => {
        this.saving.set(false);
        this.error.set("Errore durante la gestione dell'autore.");
      }
    });
  }

  private resolveAutoreId(nome: string): Observable<number> {
    const selectedId = this.selectedAutoreId();
    if (selectedId) {
      return of(selectedId);
    }

    const exact = this.autori().find((a) => a.nominativo.toLowerCase() === nome.toLowerCase());
    if (exact) {
      return of(exact.id);
    }

    return this.autoreService.create({ nominativo: nome }).pipe(map((a) => a.id));
  }
}