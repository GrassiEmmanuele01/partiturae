import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Utilizzo } from '../../../shared/utilizzo.model';
import { Famiglia } from '../../famiglie/famiglia.model';
import { FamigliaService } from '../../famiglie/famiglia.service';
import { Strumento } from '../strumento.model';
import { StrumentoService } from '../strumento.service';
import { PuoDirective } from '../../auth/puo.directive';

interface FamigliaGroup {
  id: number;
  nome: string;
  strumenti: Strumento[];
}

@Component({
  selector: 'app-strumenti-list',
  imports: [RouterLink, PuoDirective],
  templateUrl: './strumenti-list.html',
  styleUrl: './strumenti-list.scss'
})
export class StrumentiList {
  private famigliaService = inject(FamigliaService);
  private strumentoService = inject(StrumentoService);

  famiglie = signal<Famiglia[]>([]);
  strumenti = signal<Strumento[]>([]);

  loading = signal(true);
  error = signal<string | null>(null);

  // --- nuovo strumento ---
  mostraForm = signal(false);
  nuovoNome = signal('');
  famigliaNome = signal('');
  famigliaScelta = signal<number | null>(null);
  suggerimenti = signal<Famiglia[]>([]);
  famigliaDaCreare = signal<string | null>(null);
  salvando = signal(false);

  // --- modifica ---
  inModificaId = signal<number | null>(null);
  nomeModifica = signal('');
  famigliaModificaId = signal<number | null>(null);
  salvandoModifica = signal(false);

  // --- eliminazione con elenco di dove è usato ---
  eliminazioneId = signal<number | null>(null);
  eliminazioneUtilizzo = signal<Utilizzo | null>(null);
  controllandoId = signal<number | null>(null);

  gruppi = computed<FamigliaGroup[]>(() =>
    this.famiglie().map((f) => ({
      id: f.id,
      nome: f.nome,
      strumenti: this.strumenti()
        .filter((s) => s.famigliaId === f.id)
        .sort((a, b) => a.nome.localeCompare(b.nome, 'it', { numeric: true }))
    }))
  );

  constructor() {
    this.carica();
  }

  carica(): void {
    this.loading.set(true);
    this.error.set(null);

    this.famigliaService.getAll().subscribe({
      next: (data) => {
        this.famiglie.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare gli strumenti. Controlla che il backend sia avviato.');
        this.loading.set(false);
      }
    });

    this.strumentoService.getAll().subscribe({ next: (data) => this.strumenti.set(data) });
  }

  // ---------- nuovo strumento ----------
  toggleForm(): void {
    this.mostraForm.set(!this.mostraForm());
    this.nuovoNome.set('');
    this.famigliaNome.set('');
    this.famigliaScelta.set(null);
    this.famigliaDaCreare.set(null);
    this.suggerimenti.set([]);
  }

  onFamiglia(valore: string): void {
    this.famigliaNome.set(valore);
    this.famigliaScelta.set(null);
    this.famigliaDaCreare.set(null);

    const term = valore.trim().toLowerCase();
    this.suggerimenti.set(
      term ? this.famiglie().filter((f) => f.nome.toLowerCase().includes(term)).slice(0, 8) : []
    );
  }

  scegliFamiglia(famiglia: Famiglia): void {
    this.famigliaNome.set(famiglia.nome);
    this.famigliaScelta.set(famiglia.id);
    this.suggerimenti.set([]);
  }

  chiudiSuggerimentiConRitardo(): void {
    setTimeout(() => this.suggerimenti.set([]), 150);
  }

  salva(): void {
    const nome = this.nuovoNome().trim();
    const famigliaNome = this.famigliaNome().trim();

    if (!nome || !famigliaNome) {
      this.error.set('Nome strumento e famiglia sono obbligatori.');
      return;
    }

    const scelta = this.famigliaScelta();
    if (scelta) {
      this.creaStrumento(nome, scelta);
      return;
    }

    const esatta = this.famiglie().find((f) => f.nome.toLowerCase() === famigliaNome.toLowerCase());
    if (esatta) {
      this.creaStrumento(nome, esatta.id);
      return;
    }

    // La famiglia non esiste: prima chiediamo conferma.
    this.famigliaDaCreare.set(famigliaNome);
  }

  confermaNuovaFamiglia(): void {
    const nome = this.famigliaDaCreare();
    if (!nome) {
      return;
    }

    this.salvando.set(true);
    this.famigliaService.create({ nome }).subscribe({
      next: (famiglia) => {
        this.famiglie.set([...this.famiglie(), famiglia]);
        this.famigliaDaCreare.set(null);
        this.creaStrumento(this.nuovoNome().trim(), famiglia.id);
      },
      error: () => {
        this.salvando.set(false);
        this.error.set('Errore durante la creazione della famiglia.');
      }
    });
  }

  annullaNuovaFamiglia(): void {
    this.famigliaDaCreare.set(null);
  }

  private creaStrumento(nome: string, famigliaId: number): void {
    this.salvando.set(true);
    this.error.set(null);

    this.strumentoService.create({ nome, famigliaId }).subscribe({
      next: (strumento) => {
        this.strumenti.set([...this.strumenti(), strumento]);
        this.salvando.set(false);
        this.toggleForm();
      },
      error: (err) => {
        this.salvando.set(false);
        this.error.set(err.error?.message ?? 'Errore durante la creazione dello strumento.');
      }
    });
  }

  // ---------- modifica ----------
  avviaModifica(strumento: Strumento): void {
    this.inModificaId.set(strumento.id);
    this.nomeModifica.set(strumento.nome);
    this.famigliaModificaId.set(strumento.famigliaId);
  }

  annullaModifica(): void {
    this.inModificaId.set(null);
  }

  salvaModifica(id: number): void {
    const nome = this.nomeModifica().trim();
    const famigliaId = this.famigliaModificaId();
    if (!nome || !famigliaId) {
      this.error.set('Nome e famiglia sono obbligatori.');
      return;
    }

    this.salvandoModifica.set(true);
    this.strumentoService.update(id, { nome, famigliaId }).subscribe({
      next: (aggiornato) => {
        this.strumenti.set(this.strumenti().map((s) => (s.id === id ? aggiornato : s)));
        this.salvandoModifica.set(false);
        this.inModificaId.set(null);
      },
      error: (err) => {
        this.salvandoModifica.set(false);
        this.error.set(err.error?.message ?? 'Errore durante la modifica.');
      }
    });
  }

  // ---------- eliminazione ----------
  richiediEliminazione(strumento: Strumento): void {
    this.controllandoId.set(strumento.id);

    this.strumentoService.getUtilizzo(strumento.id).subscribe({
      next: (utilizzo) => {
        this.controllandoId.set(null);
        if (utilizzo.count === 0) {
          if (confirm(`Eliminare «${strumento.nome}»?`)) {
            this.elimina(strumento.id);
          }
          return;
        }
        this.eliminazioneId.set(strumento.id);
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
    this.strumentoService.delete(id).subscribe({
      next: () => this.carica(),
      error: (err) => this.error.set(err.error?.message ?? "Errore durante l'eliminazione.")
    });
  }
}
