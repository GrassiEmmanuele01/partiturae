import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Utilizzo } from '../../../shared/utilizzo.model';
import { Strumento } from '../../strumenti/strumento.model';
import { StrumentoService } from '../../strumenti/strumento.service';
import { StrumentoFiglio } from '../../strumenti-figli/strumento-figlio.model';
import { StrumentoFiglioService } from '../../strumenti-figli/strumento-figlio.service';
import { Parte } from '../parte.model';
import { ParteService } from '../parte.service';
import { PuoDirective } from '../../auth/puo.directive';

interface GruppoParti {
  figlio: StrumentoFiglio;
  parti: Parte[];
}

@Component({
  selector: 'app-strumento-parti',
  imports: [RouterLink, PuoDirective],
  templateUrl: './strumento-parti.html',
  styleUrl: './strumento-parti.scss'
})
export class StrumentoParti {
  private route = inject(ActivatedRoute);
  private strumentoService = inject(StrumentoService);
  private strumentoFiglioService = inject(StrumentoFiglioService);
  private parteService = inject(ParteService);

  strumentoId = signal(0);
  strumento = signal<Strumento | null>(null);
  figli = signal<StrumentoFiglio[]>([]);
  parti = signal<Parte[]>([]);
  espansi = signal<Set<number>>(new Set());

  loading = signal(true);
  error = signal<string | null>(null);

  // modifica nome di un sottostrumento
  inModificaId = signal<number | null>(null);
  nomeModifica = signal('');
  salvandoModifica = signal(false);

  // nuovo sottostrumento
  nuovoNome = signal('');
  aggiungendo = signal(false);

  // eliminazione con elenco di dove è usato
  eliminazioneId = signal<number | null>(null);
  eliminazioneUtilizzo = signal<Utilizzo | null>(null);
  controllandoId = signal<number | null>(null);

  gruppi = computed<GruppoParti[]>(() =>
    [...this.figli()]
      .sort((a, b) => a.nome.localeCompare(b.nome, 'it', { numeric: true }))
      .map((figlio) => ({
        figlio,
        parti: this.parti().filter((p) => p.strumenti.some((s) => s.id === figlio.id))
      }))
  );

  constructor() {
    // Si ascolta il parametro dell'URL (non lo snapshot): se si naviga da uno strumento a un altro
    // restando sulla stessa pagina, i dati si ricaricano.
    this.route.paramMap.pipe(takeUntilDestroyed()).subscribe((params) => {
      const id = Number(params.get('id'));
      this.strumentoId.set(id);
      this.carica(id);
    });
  }

  private carica(id: number): void {
    this.loading.set(true);
    this.error.set(null);
    this.espansi.set(new Set());

    this.strumentoService.getById(id).subscribe({
      next: (data) => this.strumento.set(data),
      error: () => this.error.set('Impossibile caricare lo strumento.')
    });

    this.strumentoFiglioService.getByStrumento(id).subscribe({
      next: (data) => this.figli.set(data),
      error: () => this.error.set('Impossibile caricare i sottostrumenti.')
    });

    this.parteService.getByStrumento(id).subscribe({
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

  private ricarica(): void {
    this.carica(this.strumentoId());
  }

  isEspanso(id: number): boolean {
    return this.espansi().has(id);
  }

  toggle(id: number): void {
    const copia = new Set(this.espansi());
    if (copia.has(id)) {
      copia.delete(id);
    } else {
      copia.add(id);
    }
    this.espansi.set(copia);
  }

  altriStrumenti(parte: Parte, figlioId: number): string {
    return parte.strumenti
      .filter((s) => s.id !== figlioId)
      .map((s) => s.nome)
      .join(', ');
  }

  pdfUrl(parteId: number): string {
    return this.parteService.pdfUrl(parteId);
  }

  // ---------- aggiunta ----------
  aggiungi(): void {
    const nome = this.nuovoNome().trim();
    if (!nome) {
      return;
    }

    this.aggiungendo.set(true);
    this.strumentoFiglioService.create({ nome, strumentoId: this.strumentoId() }).subscribe({
      next: (nuovo) => {
        this.figli.set([...this.figli(), nuovo]);
        this.nuovoNome.set('');
        this.aggiungendo.set(false);
      },
      error: (err) => {
        this.aggiungendo.set(false);
        this.error.set(err.error?.message ?? 'Errore durante la creazione.');
      }
    });
  }

  // ---------- modifica ----------
  avviaModifica(figlio: StrumentoFiglio): void {
    this.inModificaId.set(figlio.id);
    this.nomeModifica.set(figlio.nome);
  }

  annullaModifica(): void {
    this.inModificaId.set(null);
  }

  salvaModifica(figlio: StrumentoFiglio): void {
    const nome = this.nomeModifica().trim();
    if (!nome) {
      this.error.set('Il nome non può essere vuoto.');
      return;
    }

    this.salvandoModifica.set(true);
    this.strumentoFiglioService.update(figlio.id, { nome, strumentoId: figlio.strumentoId }).subscribe({
      next: (aggiornato) => {
        this.figli.set(this.figli().map((f) => (f.id === figlio.id ? aggiornato : f)));
        this.salvandoModifica.set(false);
        this.inModificaId.set(null);
        this.ricarica();
      },
      error: (err) => {
        this.salvandoModifica.set(false);
        this.error.set(err.error?.message ?? 'Errore durante la modifica.');
      }
    });
  }

  // ---------- eliminazione ----------
  richiediEliminazione(figlio: StrumentoFiglio): void {
    this.controllandoId.set(figlio.id);

    this.strumentoFiglioService.getUtilizzo(figlio.id).subscribe({
      next: (utilizzo) => {
        this.controllandoId.set(null);
        if (utilizzo.count === 0) {
          if (confirm(`Eliminare «${figlio.nome}»?`)) {
            this.elimina(figlio.id);
          }
          return;
        }
        this.eliminazioneId.set(figlio.id);
        this.eliminazioneUtilizzo.set(utilizzo);
      },
      error: () => {
        this.controllandoId.set(null);
        this.error.set("Errore durante il controllo dell'utilizzo.");
      }
    });
  }

  confermaEliminazione(): void {
    const id = this.eliminazioneId();
    if (id) {
      this.elimina(id);
    }
    this.annullaEliminazione();
  }

  annullaEliminazione(): void {
    this.eliminazioneId.set(null);
    this.eliminazioneUtilizzo.set(null);
  }

  private elimina(id: number): void {
    this.strumentoFiglioService.delete(id).subscribe({
      next: () => this.ricarica(),
      error: (err) => this.error.set(err.error?.message ?? "Errore durante l'eliminazione.")
    });
  }
}
