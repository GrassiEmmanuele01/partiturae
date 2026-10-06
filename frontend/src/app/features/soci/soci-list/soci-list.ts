import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Musicista } from '../../musicisti/musicista.model';
import { MusicistaService } from '../../musicisti/musicista.service';
import { Socio } from '../socio.model';
import { SocioService } from '../socio.service';

@Component({
  selector: 'app-soci-list',
  imports: [RouterLink],
  templateUrl: './soci-list.html',
  styleUrl: './soci-list.scss'
})
export class SociList {
  private socioService = inject(SocioService);
  private musicistaService = inject(MusicistaService);

  soci = signal<Socio[]>([]);
  musicisti = signal<Musicista[]>([]);

  loadingSoci = signal(true);
  loadingMusicisti = signal(true);
  error = signal<string | null>(null);
  annoCorrente = new Date().getFullYear();

  constructor() {
    this.loadSoci();
    this.loadMusicisti();
  }

  loadSoci(): void {
    this.loadingSoci.set(true);
    this.socioService.getAll().subscribe({
      next: (data) => {
        this.soci.set(data);
        this.loadingSoci.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare i soci. Controlla che il backend sia avviato.');
        this.loadingSoci.set(false);
      }
    });
  }

  loadMusicisti(): void {
    this.loadingMusicisti.set(true);
    this.musicistaService.getAll().subscribe({
      next: (data) => {
        this.musicisti.set(data);
        this.loadingMusicisti.set(false);
      },
      error: () => this.loadingMusicisti.set(false)
    });
  }

  removeSocio(id: number): void {
    if (!confirm('Eliminare questo socio?')) {
      return;
    }

    this.socioService.delete(id).subscribe({
      next: () => this.loadSoci(),
      error: () => this.error.set("Errore durante l'eliminazione. Controlla che non sia collegato a un musicista.")
    });
  }

  removeProfiloMusicale(musicistaId: number): void {
    if (!confirm('Rimuovere il profilo musicale? Il socio resterà nel libro soci.')) {
      return;
    }

    this.musicistaService.delete(musicistaId).subscribe({
      next: () => this.loadMusicisti(),
      error: () => this.error.set('Errore durante la rimozione del profilo musicale.')
    });
  }
}