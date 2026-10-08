import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { StrumentoFiglio } from '../../strumenti-figli/strumento-figlio.model';
import { StrumentoFiglioService } from '../../strumenti-figli/strumento-figlio.service';
import { Parte } from '../parte.model';
import { ParteService } from '../parte.service';

@Component({
  selector: 'app-strumento-figlio-parti',
  imports: [RouterLink],
  templateUrl: './strumento-figlio-parti.html',
  styleUrl: './strumento-figlio-parti.scss'
})
export class StrumentoFiglioParti {
  private route = inject(ActivatedRoute);
  private strumentoFiglioService = inject(StrumentoFiglioService);
  private parteService = inject(ParteService);

  strumentoFiglioId = Number(this.route.snapshot.paramMap.get('id'));

  corrente = signal<StrumentoFiglio | null>(null);
  fratelli = signal<StrumentoFiglio[]>([]);
  parti = signal<Parte[]>([]);

  loading = signal(true);
  error = signal<string | null>(null);

  constructor() {
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.strumentoFiglioService.getById(this.strumentoFiglioId).subscribe({
      next: (sf) => {
        this.corrente.set(sf);
        this.strumentoFiglioService.getByStrumento(sf.strumentoId).subscribe({
          next: (data) => this.fratelli.set(data)
        });
      },
      error: () => this.error.set('Impossibile caricare lo strumento.')
    });

    this.parteService.getByStrumentoFiglio(this.strumentoFiglioId).subscribe({
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

  pdfUrl(parteId: number): string {
    return this.parteService.pdfUrl(parteId);
  }
}