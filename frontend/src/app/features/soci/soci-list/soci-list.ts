import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

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

  soci = signal<Socio[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  annoCorrente = new Date().getFullYear();

  constructor() {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.socioService.getAll().subscribe({
      next: (data) => {
        this.soci.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare i soci. Controlla che il backend sia avviato.');
        this.loading.set(false);
      }
    });
  }

  remove(id: number): void {
    if (!confirm('Eliminare questo socio?')) {
      return;
    }

    this.socioService.delete(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set("Errore durante l'eliminazione. Controlla che non sia collegato a un bandista.")
    });
  }
}