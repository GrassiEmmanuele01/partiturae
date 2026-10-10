import { Component, ElementRef, computed, inject, signal, viewChild } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Observable, map, of, switchMap } from 'rxjs';

import { Famiglia } from '../../famiglie/famiglia.model';
import { FamigliaService } from '../../famiglie/famiglia.service';
import { Partitura } from '../../partiture/partitura.model';
import { PartituraService } from '../../partiture/partitura.service';
import { Strumento } from '../../strumenti/strumento.model';
import { StrumentoService } from '../../strumenti/strumento.service';
import { StrumentoFiglio } from '../../strumenti-figli/strumento-figlio.model';
import { StrumentoFiglioService } from '../../strumenti-figli/strumento-figlio.service';
import { Parte } from '../parte.model';
import { ParteService } from '../parte.service';
import { PuoDirective } from '../../auth/puo.directive';

interface RigaParte {
  parte: Parte;
  strumento: StrumentoFiglio;
}

@Component({
  selector: 'app-partitura-parti',
  imports: [RouterLink, PuoDirective],
  templateUrl: './partitura-parti.html',
  styleUrl: './partitura-parti.scss'
})
export class PartituraParti {
  private route = inject(ActivatedRoute);
  private partituraService = inject(PartituraService);
  private parteService = inject(ParteService);
  private famigliaService = inject(FamigliaService);
  private strumentoService = inject(StrumentoService);
  private strumentoFiglioService = inject(StrumentoFiglioService);

  partituraId = Number(this.route.snapshot.paramMap.get('id'));
  fileInput = viewChild<ElementRef<HTMLInputElement>>('fileInput');

  partitura = signal<Partitura | null>(null);
  parti = signal<Parte[]>([]);
  famiglie = signal<Famiglia[]>([]);
  strumenti = signal<Strumento[]>([]);
  strumentiFigli = signal<StrumentoFiglio[]>([]);

  loading = signal(true);
  saving = signal(false);
  error = signal<string | null>(null);
  uploadingId = signal<number | null>(null);

  // --- nuova parte: strumenti selezionati + PDF ---
  selezionati = signal<StrumentoFiglio[]>([]);
  ricerca = signal('');
  ricercaAperta = signal(false);
  pdfFile = signal<File | null>(null);

  // --- creazione al volo di uno strumento che non esiste ---
  creazioneAperta = signal(false);
  creating = signal(false);
  nuovoNome = signal('');
  genitoreNome = signal('');
  famigliaNome = signal('');

  righe = computed<RigaParte[]>(() =>
    this.parti()
      .flatMap((parte) => parte.strumenti.map((strumento) => ({ parte, strumento })))
      .sort(
        (a, b) =>
          a.strumento.nome.localeCompare(b.strumento.nome, 'it', { numeric: true }) ||
          a.parte.id - b.parte.id
      )
  );

  suggerimenti = computed(() => {
    const term = this.ricerca().trim().toLowerCase();
    if (!term) {
      return [];
    }
    const giaScelti = new Set(this.selezionati().map((s) => s.id));
    return this.strumentiFigli()
      .filter((s) => !giaScelti.has(s.id) && s.nome.toLowerCase().includes(term))
      .slice(0, 8);
  });

  puoCreare = computed(() => {
    const term = this.ricerca().trim().toLowerCase();
    return term.length > 0 && !this.strumentiFigli().some((s) => s.nome.toLowerCase() === term);
  });

  genitoreEsistente = computed(() => {
    const term = this.genitoreNome().trim().toLowerCase();
    return this.strumenti().find((s) => s.nome.toLowerCase() === term) ?? null;
  });

  famigliaEsistente = computed(() => {
    const term = this.famigliaNome().trim().toLowerCase();
    return this.famiglie().find((f) => f.nome.toLowerCase() === term) ?? null;
  });

  genitoreSuggerimenti = computed(() => {
    const term = this.genitoreNome().trim().toLowerCase();
    if (!term || this.genitoreEsistente()) {
      return [];
    }
    return this.strumenti().filter((s) => s.nome.toLowerCase().includes(term)).slice(0, 6);
  });

  famigliaSuggerimenti = computed(() => {
    const term = this.famigliaNome().trim().toLowerCase();
    if (!term || this.famigliaEsistente()) {
      return [];
    }
    return this.famiglie().filter((f) => f.nome.toLowerCase().includes(term)).slice(0, 6);
  });

  constructor() {
    this.partituraService.getById(this.partituraId).subscribe({
      next: (data) => this.partitura.set(data),
      error: () => this.error.set('Impossibile caricare la partitura.')
    });

    this.famigliaService.getAll().subscribe({ next: (data) => this.famiglie.set(data) });
    this.strumentoService.getAll().subscribe({ next: (data) => this.strumenti.set(data) });
    this.strumentoFiglioService.getAll().subscribe({ next: (data) => this.strumentiFigli.set(data) });

    this.caricaParti();
  }

  caricaParti(): void {
    this.loading.set(true);
    this.parteService.getByPartitura(this.partituraId).subscribe({
      next: (data) => {
        this.parti.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare le parti.');
        this.loading.set(false);
      }
    });
  }

  // ---------- selezione strumenti ----------
  onRicerca(valore: string): void {
    this.ricerca.set(valore);
    this.ricercaAperta.set(true);
  }

  chiudiSuggerimentiConRitardo(): void {
    setTimeout(() => this.ricercaAperta.set(false), 150);
  }

  aggiungiStrumento(strumento: StrumentoFiglio): void {
    this.selezionati.set([...this.selezionati(), strumento]);
    this.ricerca.set('');
    this.ricercaAperta.set(false);
  }

  togliStrumento(id: number): void {
    this.selezionati.set(this.selezionati().filter((s) => s.id !== id));
  }

  onFileScelto(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.pdfFile.set(input.files?.[0] ?? null);
  }

  // ---------- creazione strumento al volo ----------
  apriCreazione(): void {
    const nome = this.ricerca().trim();
    this.nuovoNome.set(nome);
    // "Flicorno tenore 2" -> strumento generico proposto: "Flicorno tenore"
    this.genitoreNome.set(nome.replace(/\s*\d+$/, '').trim());
    this.famigliaNome.set('');
    this.creazioneAperta.set(true);
    this.ricercaAperta.set(false);
  }

  annullaCreazione(): void {
    this.creazioneAperta.set(false);
  }

  scegliGenitore(strumento: Strumento): void {
    this.genitoreNome.set(strumento.nome);
  }

  scegliFamiglia(famiglia: Famiglia): void {
    this.famigliaNome.set(famiglia.nome);
  }

  confermaCreazione(): void {
    const nome = this.nuovoNome().trim();
    const genitore = this.genitoreNome().trim();

    if (!nome || !genitore) {
      this.error.set('Indica il nome dello strumento e a quale strumento generico appartiene.');
      return;
    }
    if (!this.genitoreEsistente() && !this.famigliaNome().trim()) {
      this.error.set('Indica la famiglia del nuovo strumento generico.');
      return;
    }

    this.creating.set(true);
    this.error.set(null);

    this.risolviStrumentoId(genitore)
      .pipe(switchMap((strumentoId) => this.strumentoFiglioService.create({ nome, strumentoId })))
      .subscribe({
        next: (nuovo) => {
          this.strumentiFigli.set([...this.strumentiFigli(), nuovo]);
          this.selezionati.set([...this.selezionati(), nuovo]);
          this.ricerca.set('');
          this.creazioneAperta.set(false);
          this.creating.set(false);
        },
        error: (err) => {
          this.creating.set(false);
          this.error.set(err.error?.message ?? 'Errore durante la creazione dello strumento.');
        }
      });
  }

  private risolviStrumentoId(nome: string): Observable<number> {
    const esistente = this.genitoreEsistente();
    if (esistente) {
      return of(esistente.id);
    }

    return this.risolviFamigliaId().pipe(
      switchMap((famigliaId) => this.strumentoService.create({ nome, famigliaId })),
      map((strumento) => {
        this.strumenti.set([...this.strumenti(), strumento]);
        return strumento.id;
      })
    );
  }

  private risolviFamigliaId(): Observable<number> {
    const esistente = this.famigliaEsistente();
    if (esistente) {
      return of(esistente.id);
    }

    return this.famigliaService.create({ nome: this.famigliaNome().trim() }).pipe(
      map((famiglia) => {
        this.famiglie.set([...this.famiglie(), famiglia]);
        return famiglia.id;
      })
    );
  }

  // ---------- salvataggio ----------
  aggiungiParte(): void {
    if (this.selezionati().length === 0) {
      this.error.set('Seleziona almeno uno strumento.');
      return;
    }

    this.saving.set(true);
    this.error.set(null);

    this.parteService
      .create({
        partituraId: this.partituraId,
        strumentoFiglioIds: this.selezionati().map((s) => s.id)
      })
      .subscribe({
        next: (parte) => {
          const file = this.pdfFile();
          if (!file) {
            this.concludi();
            return;
          }

          this.parteService.uploadPdf(parte.id, file).subscribe({
            next: () => this.concludi(),
            error: (err) => {
              this.concludi();
              this.error.set(
                (err.error?.message ?? 'Caricamento del PDF non riuscito') +
                  ' — la parte è stata creata, riprova a caricare il PDF dalla tabella.'
              );
            }
          });
        },
        error: (err) => {
          this.saving.set(false);
          this.error.set(err.error?.message ?? 'Errore durante il salvataggio della parte.');
        }
      });
  }

  private concludi(): void {
    this.saving.set(false);
    this.selezionati.set([]);
    this.ricerca.set('');
    this.pdfFile.set(null);
    const input = this.fileInput();
    if (input) {
      input.nativeElement.value = '';
    }
    this.caricaParti();
  }

  // ---------- azioni sulle righe ----------
  rimuoviRiga(riga: RigaParte): void {
    const { parte, strumento } = riga;

    if (parte.strumenti.length > 1) {
      const altri = parte.strumenti
        .filter((s) => s.id !== strumento.id)
        .map((s) => s.nome)
        .join(', ');
      const conferma = confirm(
        `Questo PDF è condiviso con: ${altri}.\nRimuovere solo «${strumento.nome}»? Il PDF resta per gli altri strumenti.`
      );
      if (!conferma) {
        return;
      }

      const restanti = parte.strumenti.filter((s) => s.id !== strumento.id).map((s) => s.id);
      this.parteService.updateStrumenti(parte.id, restanti).subscribe({
        next: () => this.caricaParti(),
        error: () => this.error.set('Errore durante la rimozione.')
      });
      return;
    }

    if (!confirm(`Eliminare la parte «${strumento.nome}» e il suo PDF?`)) {
      return;
    }

    this.parteService.delete(parte.id).subscribe({
      next: () => this.caricaParti(),
      error: () => this.error.set("Errore durante l'eliminazione.")
    });
  }

  onFilePerParte(event: Event, parteId: number): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      return;
    }

    this.uploadingId.set(parteId);
    this.parteService.uploadPdf(parteId, file).subscribe({
      next: () => {
        this.uploadingId.set(null);
        input.value = '';
        this.caricaParti();
      },
      error: (err) => {
        this.uploadingId.set(null);
        input.value = '';
        this.error.set(err.error?.message ?? 'Errore durante il caricamento del PDF.');
      }
    });
  }

  condivisoCon(riga: RigaParte): string {
    return riga.parte.strumenti
      .filter((s) => s.id !== riga.strumento.id)
      .map((s) => s.nome)
      .join(', ');
  }

  pdfUrl(parteId: number): string {
    return this.parteService.pdfUrl(parteId);
  }
}
