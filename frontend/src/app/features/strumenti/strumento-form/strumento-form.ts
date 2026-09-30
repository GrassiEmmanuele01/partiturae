import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Observable, map, of } from 'rxjs';

import { Famiglia } from '../../famiglie/famiglia.model';
import { FamigliaService } from '../../famiglie/famiglia.service';
import { StrumentoService } from '../strumento.service';

@Component({
  selector: 'app-strumento-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './strumento-form.html',
  styleUrl: './strumento-form.scss'
})
export class StrumentoForm {
  private fb = inject(FormBuilder);
  private strumentoService = inject(StrumentoService);
  private famigliaService = inject(FamigliaService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  strumentoId = signal<number | null>(null);
  famiglie = signal<Famiglia[]>([]);
  suggestions = signal<Famiglia[]>([]);
  selectedFamigliaId = signal<number | null>(null);
  loading = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    famigliaNome: ['', Validators.required]
  });

  willCreateNewFamiglia = computed(() => {
    const term = this.form.controls.famigliaNome.value?.trim();
    if (!term || this.selectedFamigliaId()) {
      return false;
    }
    return !this.famiglie().some((f) => f.nome.toLowerCase() === term.toLowerCase());
  });

  constructor() {
    this.famigliaService.getAll().subscribe({
      next: (data) => this.famiglie.set(data),
      error: () => this.error.set('Impossibile caricare le famiglie.')
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.strumentoId.set(id);
      this.loading.set(true);

      this.strumentoService.getById(id).subscribe({
        next: (strumento) => {
          this.form.patchValue({
            nome: strumento.nome,
            famigliaNome: strumento.famigliaNome
          });
          this.selectedFamigliaId.set(strumento.famigliaId);
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Impossibile caricare lo strumento.');
          this.loading.set(false);
        }
      });
    }
  }

  onFamigliaInput(): void {
    this.selectedFamigliaId.set(null);

    const term = this.form.controls.famigliaNome.value.trim().toLowerCase();
    if (!term) {
      this.suggestions.set([]);
      return;
    }

    this.suggestions.set(
      this.famiglie()
        .filter((f) => f.nome.toLowerCase().includes(term))
        .slice(0, 8)
    );
  }

  selectFamiglia(famiglia: Famiglia): void {
    this.form.patchValue({ famigliaNome: famiglia.nome });
    this.selectedFamigliaId.set(famiglia.id);
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

    this.resolveFamigliaId(value.famigliaNome.trim()).subscribe({
      next: (famigliaId) => {
        const request = { nome: value.nome, famigliaId };
        const id = this.strumentoId();
        const request$ = id
          ? this.strumentoService.update(id, request)
          : this.strumentoService.create(request);

        request$.subscribe({
          next: () => this.router.navigate(['/strumenti']),
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
        this.error.set("Errore durante la gestione della famiglia.");
      }
    });
  }

  private resolveFamigliaId(nome: string): Observable<number> {
    const selectedId = this.selectedFamigliaId();
    if (selectedId) {
      return of(selectedId);
    }

    const exact = this.famiglie().find((f) => f.nome.toLowerCase() === nome.toLowerCase());
    if (exact) {
      return of(exact.id);
    }

    return this.famigliaService.create({ nome }).pipe(map((f) => f.id));
  }
}