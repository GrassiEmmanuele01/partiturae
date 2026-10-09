import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
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

  corrente = signal<StrumentoFiglio | null>(null);
  fratelli = signal<StrumentoFiglio[]>([]);
  parti = signal<Parte[]>([]);

  loading = signal(true);
  error = signal<string | null>(null);

  constructor() {
    // Ascoltiamo il parametro dell'URL: cliccando un'altra voce (es. da Ottavino 2 a Ottavino 1)
    // Angular riusa lo stesso componente, quindi i dati vanno ricaricati a ogni cambio di id.
    this.route.paramMap.pipe(takeUntilDestroyed()).subscribe((params) => {
      this.carica(Number(params.get('id')));
    });
  }

  private carica(id: number): void {
    this.loading.set(true);
    this.error.set(null);
    this.parti.set([]);

    this.strumentoFiglioService.getById(id).subscribe({
      next: (voce) => {
        this.corrente.set(voce);
        this.strumentoFiglioService.getByStrumento(voce.strumentoId).subscribe({
          next: (data) =>
            this.fratelli.set([...data].sort((a, b) => a.nome.localeCompare(b.nome, 'it', { numeric: true })))
        });
      },
      error: () => this.error.set('Impossibile caricare lo strumento.')
    });

    this.parteService.getByStrumentoFiglio(id).subscribe({
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

  altriStrumenti(parte: Parte, voceId: number): string {
    return parte.strumenti
      .filter((s) => s.id !== voceId)
      .map((s) => s.nome)
      .join(', ');
  }

  pdfUrl(parteId: number): string {
    return this.parteService.pdfUrl(parteId);
  }
}
