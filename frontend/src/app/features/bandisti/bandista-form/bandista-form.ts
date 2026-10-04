import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { Socio } from '../../soci/socio.model';
import { SocioService } from '../../soci/socio.service';
import { Bandista } from '../bandista.model';
import { BandistaService } from '../bandista.service';

@Component({
  selector: 'app-bandista-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './bandista-form.html',
  styleUrl: './bandista-form.scss'
})
export class BandistaForm {
  private fb = inject(FormBuilder);
  private bandistaService = inject(BandistaService);
  private socioService = inject(SocioService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  bandistaId = signal<number | null>(null);
  loading = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);

  soci = signal<Socio[]>([]);
  bandistiEsistenti = signal<Bandista[]>([]);
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
    this.bandistaService.getAll().subscribe({ next: (data) => this.bandistiEsistenti.set(data) });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.bandistaId.set(id);
      this.loading.set(true);

      this.bandistaService.getById(id).subscribe({
        next: (bandista) => {
          this.selectedSocioId.set(bandista.socio.id);
          this.searchForm.patchValue({ termine: `${bandista.socio.nome} ${bandista.socio.cognome}` });
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Impossibile caricare il bandista.');
          this.loading.set(false);
        }
      });
    }
  }

  private socioGiaBandista(socioId: number): boolean {
    return this.bandistiEsistenti().some((b) => b.socio.id === socioId && b.id !== this.bandistaId());
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
        .filter((s) => !this.socioGiaBandista(s.id))
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
          next: (socio) => this.salvaBandista(socio.id),
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
    this.salvaBandista(socioId);
  }

  private salvaBandista(socioId: number): void {
    const id = this.bandistaId();
    const request$ = id
      ? this.bandistaService.update(id, { socioId })
      : this.bandistaService.create({ socioId });

    request$.subscribe({
      next: () => this.router.navigate(['/bandisti']),
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