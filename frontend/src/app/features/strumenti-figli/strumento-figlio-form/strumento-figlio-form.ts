import { Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Observable, map, of } from 'rxjs';

import { Strumento } from '../../strumenti/strumento.model';
import { StrumentoService } from '../../strumenti/strumento.service';
import { StrumentoFiglioService } from '../strumento-figlio.service';

@Component({
  selector: 'app-strumento-figlio-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './strumento-figlio-form.html',
  styleUrl: './strumento-figlio-form.scss'
})
export class StrumentoFiglioForm {
  private fb = inject(FormBuilder);
  private strumentoFiglioService = inject(StrumentoFiglioService);
  private strumentoService = inject(StrumentoService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  strumentoFiglioId = signal<number | null>(null);
  strumenti = signal<Strumento[]>([]);
  suggestions = signal<Strumento[]>([]);
  selectedStrumentoId = signal<number | null>(null);
  loading = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    strumentoNome: ['', Validators.required]
  });

  willFailNewStrumento = computed(() => {
    const term = this.form.controls.strumentoNome.value?.trim();
    if (!term || this.selectedStrumentoId()) {
      return false;
    }
    return !this.strumenti().some((s) => s.nome.toLowerCase() === term.toLowerCase());
  });

  constructor() {
    this.strumentoService.getAll().subscribe({
      next: (data) => this.strumenti.set(data),
      error: () => this.error.set('Impossibile caricare gli strumenti.')
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.strumentoFiglioId.set(id);
      this.loading.set(true);

      this.strumentoFiglioService.getById(id).subscribe({
        next: (sf) => {
          this.form.patchValue({
            nome: sf.nome,
            strumentoNome: sf.strumentoNome
          });
          this.selectedStrumentoId.set(sf.strumentoId);
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Impossibile caricare l\'elemento.');
          this.loading.set(false);
        }
      });
    }
  }

  onStrumentoInput(): void {
    this.selectedStrumentoId.set(null);

    const term = this.form.controls.strumentoNome.value.trim().toLowerCase();
    if (!term) {
      this.suggestions.set([]);
      return;
    }

    this.suggestions.set(
      this.strumenti()
        .filter((s) => s.nome.toLowerCase().includes(term))
        .slice(0, 8)
    );
  }

  selectStrumento(strumento: Strumento): void {
    this.form.patchValue({ strumentoNome: strumento.nome });
    this.selectedStrumentoId.set(strumento.id);
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

    // Qui NON creiamo lo strumento al volo se non esiste: uno strumento richiede
    // a sua volta una famiglia, quindi se scrivi un nome mai visto blocchiamo il
    // salvataggio e ti mandiamo a crearlo dalla sua pagina dedicata.
    const strumentoId = this.selectedStrumentoId();
    const exact = this.strumenti().find(
      (s) => s.nome.toLowerCase() === this.form.controls.strumentoNome.value.trim().toLowerCase()
    );

    if (!strumentoId && !exact) {
      this.error.set('Strumento non trovato: creane uno prima dalla pagina "Strumenti".');
      return;
    }

    this.saving.set(true);
    this.error.set(null);

    const value = this.form.getRawValue();
    const request = { nome: value.nome, strumentoId: strumentoId ?? exact!.id };

    const id = this.strumentoFiglioId();
    const request$ = id
      ? this.strumentoFiglioService.update(id, request)
      : this.strumentoFiglioService.create(request);

    request$.subscribe({
      next: () => this.router.navigate(['/strumenti-figli']),
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