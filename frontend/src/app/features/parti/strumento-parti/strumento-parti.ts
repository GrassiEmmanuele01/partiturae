import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Strumento } from '../../strumenti/strumento.model';
import { StrumentoService } from '../../strumenti/strumento.service';
import { StrumentoFiglio } from '../../strumenti-figli/strumento-figlio.model';
import { StrumentoFiglioService } from '../../strumenti-figli/strumento-figlio.service';
import { Parte } from '../parte.model';
import { ParteService } from '../parte.service';

interface GruppoParti {
  strumentoFiglioId: number;
  strumentoFiglioNome: string;
  parti: Parte[];
}

@Component({
  selector: 'app-strumento-parti',
  imports: [RouterLink],
  templateUrl: './strumento-parti.html',
  styleUrl: './strumento-parti.scss'
})
export class StrumentoParti {
  private route = inject(ActivatedRoute);
  private strumentoService = inject(StrumentoService);
  private strumentoFiglioService = inject(StrumentoFiglioService);
  private parteService = inject(ParteService);

  strumentoId = Number(this.route.snapshot.paramMap.get('id'));

  strumento = signal<Strumento | null>(null);
  strumentiFigli = signal<StrumentoFiglio[]>([]);
  parti = signal<Parte[]>([]);
  expandedIds = signal<Set<number>>(new Set());

  loading = signal(true);
  error = signal<string | null>(null);

  gruppi = computed<GruppoParti[]>(() =>
    this.strumentiFigli().map((sf) => ({
      strumentoFiglioId: sf.id,
      strumentoFiglioNome: sf.nome,
      parti: this.parti().filter((p) => p.strumentoFiglioId === sf.id)
    }))
  );

  constructor() {
    this.strumentoService.getById(this.strumentoId).subscribe({
      next: (data) => this.strumento.set(data),
      error: () => this.error.set('Impossibile caricare lo strumento.')
    });

    this.strumentoFiglioService.getByStrumento(this.strumentoId).subscribe({
      next: (data) => this.strumentiFigli.set(data)
    });

    this.parteService.getByStrumento(this.strumentoId).subscribe({
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

  isExpanded(id: number): boolean {
    return this.expandedIds().has(id);
  }

  toggleExpand(id: number): void {
    const current = new Set(this.expandedIds());
    if (current.has(id)) {
      current.delete(id);
    } else {
      current.add(id);
    }
    this.expandedIds.set(current);
  }

  pdfUrl(parteId: number): string {
    return this.parteService.pdfUrl(parteId);
  }
}