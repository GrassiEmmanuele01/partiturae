import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { Musicista } from '../../musicisti/musicista.model';
import { MusicistaService } from '../../musicisti/musicista.service';
import { Strumento } from '../../strumenti/strumento.model';
import { StrumentoService } from '../../strumenti/strumento.service';
import { IscrizioneService } from '../iscrizione.service';
import { Iscrizione, IscrizioneSummary } from '../socio.model';
import { SocioService } from '../socio.service';

@Component({
  selector: 'app-socio-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './socio-form.html',
  styleUrl: './socio-form.scss'
})
export class SocioForm {
  private fb = inject(FormBuilder);
  private socioService = inject(SocioService);
  private iscrizioneService = inject(IscrizioneService);
  private musicistaService = inject(MusicistaService);
  private strumentoService = inject(StrumentoService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  socioId = signal<number | null>(null);
  loading = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);

  iscrizioni = signal<Iscrizione[]>([]);
  summary = signal<IscrizioneSummary | null>(null);
  nuovoAnno = signal(new Date().getFullYear());
  nuovoIscritto = signal(true);

  // --- Sezione musicale (opzionale) ---
  eMusicista = signal(false);
  musicistaEsistenteId = signal<number | null>(null);
  strumentiSelezionati = signal<Strumento[]>([]);
  strumentiDisponibili = signal<Strumento[]>([]);
  strumentoSuggestions = signal<Strumento[]>([]);
  strumentoSearchTerm = signal('');

  form = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    cognome: ['', Validators.required],
    mail: ['', [Validators.required, Validators.email]],
    codiceFiscale: [''],
    telefono: [''],
    aggiunto: [false]
  });

  constructor() {
    this.strumentoService.getAll().subscribe({ next: (data) => this.strumentiDisponibili.set(data) });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.socioId.set(id);
      this.loading.set(true);

      this.socioService.getById(id).subscribe({
        next: (socio) => {
          this.form.patchValue({
            nome: socio.nome,
            cognome: socio.cognome,
            mail: socio.mail,
            codiceFiscale: socio.codiceFiscale ?? '',
            telefono: socio.telefono ?? '',
            aggiunto: socio.aggiunto
          });
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Impossibile caricare il socio.');
          this.loading.set(false);
        }
      });

      this.musicistaService.getAll().subscribe({
        next: (musicisti) => {
          const match = musicisti.find((m) => m.socio.id === id);
          if (match) {
            this.eMusicista.set(true);
            this.musicistaEsistenteId.set(match.id);
            this.strumentiSelezionati.set(match.strumenti);
          }
        }
      });

      this.loadIscrizioni();
    }
  }

  loadIscrizioni(): void {
    const id = this.socioId();
    if (!id) return;

    this.iscrizioneService.getAll(id).subscribe({ next: (data) => this.iscrizioni.set(data) });
    this.iscrizioneService.getSummary(id).subscribe({ next: (data) => this.summary.set(data) });
  }

  addIscrizione(): void {
    const id = this.socioId();
    if (!id) return;

    this.iscrizioneService.upsert(id, this.nuovoAnno(), this.nuovoIscritto(), false).subscribe({
      next: () => this.loadIscrizioni(),
      error: () => this.error.set("Errore durante il salvataggio dell'iscrizione.")
    });
  }

  removeIscrizione(anno: number): void {
    const id = this.socioId();
    if (!id) return;

    if (!confirm(`Eliminare l'iscrizione ${anno}?`)) {
      return;
    }

    this.iscrizioneService.delete(id, anno).subscribe({
      next: () => this.loadIscrizioni(),
      error: () => this.error.set("Errore durante l'eliminazione.")
    });
  }

  toggleEMusicista(): void {
    this.eMusicista.set(!this.eMusicista());
  }

  onStrumentoInput(term: string): void {
    this.strumentoSearchTerm.set(term);
    const lower = term.trim().toLowerCase();
    const giaSelezionati = new Set(this.strumentiSelezionati().map((s) => s.id));

    if (!lower) {
      this.strumentoSuggestions.set([]);
      return;
    }

    this.strumentoSuggestions.set(
      this.strumentiDisponibili()
        .filter((s) => !giaSelezionati.has(s.id) && s.nome.toLowerCase().includes(lower))
        .slice(0, 8)
    );
  }

  addStrumento(strumento: Strumento): void {
    this.strumentiSelezionati.set([...this.strumentiSelezionati(), strumento]);
    this.strumentoSearchTerm.set('');
    this.strumentoSuggestions.set([]);
  }

  removeStrumento(strumentoId: number): void {
    this.strumentiSelezionati.set(this.strumentiSelezionati().filter((s) => s.id !== strumentoId));
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
      cognome: value.cognome,
      mail: value.mail,
      codiceFiscale: value.codiceFiscale || null,
      telefono: value.telefono || null,
      aggiunto: value.aggiunto
    };

    const id = this.socioId();
    const request$ = id
      ? this.socioService.update(id, request)
      : this.socioService.create(request);

    request$.subscribe({
      next: (socio) => this.handleMusicista(socio.id),
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

  private handleMusicista(socioId: number): void {
    const esistenteId = this.musicistaEsistenteId();
    const strumentoIds = this.strumentiSelezionati().map((s) => s.id);

    if (this.eMusicista()) {
      if (esistenteId) {
        this.musicistaService.updateStrumenti(esistenteId, strumentoIds).subscribe({
          next: () => this.finish(),
          error: () => this.finishWithWarning('Socio salvato, ma errore nell\'aggiornare gli strumenti.')
        });
      } else {
        this.musicistaService.create({ socioId }).subscribe({
          next: (musicista) => {
            this.musicistaService.updateStrumenti(musicista.id, strumentoIds).subscribe({
              next: () => this.finish(),
              error: () => this.finishWithWarning('Socio salvato, ma errore nell\'aggiungere gli strumenti.')
            });
          },
          error: () => this.finishWithWarning('Socio salvato, ma errore nel creare il profilo musicale.')
        });
      }
    } else if (esistenteId) {
      this.musicistaService.delete(esistenteId).subscribe({
        next: () => this.finish(),
        error: () => this.finishWithWarning('Socio salvato, ma errore nel rimuovere il profilo musicale.')
      });
    } else {
      this.finish();
    }
  }

  private finish(): void {
    this.saving.set(false);
    this.router.navigate(['/soci']);
  }

  private finishWithWarning(message: string): void {
    this.saving.set(false);
    this.error.set(message);
  }
}