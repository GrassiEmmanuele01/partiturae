import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { Socio } from '../../soci/socio.model';
import { SocioService } from '../../soci/socio.service';
import { Strumentista } from '../strumentista.model';
import { StrumentistaService } from '../strumentista.service';

@Component({
  selector: 'app-strumentista-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './strumentista-form.html',
  styleUrl: './strumentista-form.scss'
})
export class StrumentistaForm {
  private fb = inject(FormBuilder);
  private strumentistaService = inject(StrumentistaService);
  private socioService = inject(SocioService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  strumentistaId = signal<number | null>(null);
  loading = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);

  soci = signal<Socio[]>([]);
  strumentistiEsistenti = signal<Strumentista[]>([]);
  suggestions = signal<Socio[]>([]);
  selectedSocioId = signal<number | null>(null);

  modalitaNuovo = signal(false);

  searchForm = this.fb.nonNullable.group({
    termine: ['']
  });

  nuovoSocioForm = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    cognome: ['', Validators.required],
    mail: ['', [Validators.required, Validators.email]],
    codiceFiscale: [''],
    telefono: ['']
  });

  constructor() {
    this.socioService.getAll().subscribe({ next: (data) => this.soci.set(data) });
    this.strumentistaService.getAll().subscribe({ next: (data) => this.strumentistiEsistenti.set(data) });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.strumentistaId.set(id);
      this.loading.set(true);

      this.strumentistaService.getById(id).subscribe({
        next: (strumentista) => {
          this.selectedSocioId.set(strumentista.socio.id);
          this.searchForm.patchValue({ termine: `${strumentista.socio.nome} ${strumentista.socio.cognome}` });
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Impossibile caricare il strumentista.');
          this.loading.set(false);
        }
      });
    }
  }

  private socioGiaStrumentista(socioId: number): boolean {
    return this.strumentistiEsistenti().some((b) => b.socio.id === socioId && b.id !== this.strumentistaId());
  }

  onSearchInput(): void {
    this.selectedSocioId.set(null);

    const term = this.searchForm.controls.termine.value.trim().toLowerCase();
    if (!term) {
      this.suggestions.set([]);
      return;
    }

    this.suggestions.set(
      this.soci()
        .filter((s) => !this.socioGiaStrumentista(s.id))
        .filter((s) => `${s.nome} ${s.cognome}`.toLowerCase().includes(term))
        .slice(0, 8)
    );
  }

  selectSocio(socio: Socio): void {
    this.searchForm.patchValue({ termine: `${socio.nome} ${socio.cognome}` });
    this.selectedSocioId.set(socio.id);
    this.suggestions.set([]);
  }

  hideSuggestionsDelayed(): void {
    setTimeout(() => this.suggestions.set([]), 150);
  }

  toggleModalitaNuovo(): void {
    this.modalitaNuovo.set(!this.modalitaNuovo());
    this.error.set(null);
  }

  submit(): void {
    this.error.set(null);

    if (this.modalitaNuovo()) {
      if (this.nuovoSocioForm.invalid) {
        this.nuovoSocioForm.markAllAsTouched();
        return;
      }

      this.saving.set(true);
      const value = this.nuovoSocioForm.getRawValue();

      this.socioService
        .create({
          nome: value.nome,
          cognome: value.cognome,
          mail: value.mail,
          codiceFiscale: value.codiceFiscale || null,
          telefono: value.telefono || null,
          aggiunto: true
        })
        .subscribe({
          next: (socio) => this.salvaStrumentista(socio.id),
          error: (err) => {
            this.saving.set(false);
            if (err.status === 400 && err.error?.errors) {
              this.error.set(Object.values(err.error.errors).join(', '));
            } else {
              this.error.set('Errore durante la creazione del socio.');
            }
          }
        });
      return;
    }

    const socioId = this.selectedSocioId();
    if (!socioId) {
      this.error.set('Seleziona un socio esistente dall\'elenco, oppure creane uno nuovo.');
      return;
    }

    this.saving.set(true);
    this.salvaStrumentista(socioId);
  }

  private salvaStrumentista(socioId: number): void {
    const id = this.strumentistaId();
    const request$ = id
      ? this.strumentistaService.update(id, { socioId })
      : this.strumentistaService.create({ socioId });

    request$.subscribe({
      next: () => this.router.navigate(['/strumentisti']),
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