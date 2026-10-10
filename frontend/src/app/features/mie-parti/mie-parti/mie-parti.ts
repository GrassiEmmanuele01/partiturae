import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Parte } from '../../parti/parte.model';
import { MiePartiRisposta } from '../mie-parti.model';
import { MiePartiService } from '../mie-parti.service';

@Component({
  selector: 'app-mie-parti',
  imports: [RouterLink],
  templateUrl: './mie-parti.html',
  styleUrl: './mie-parti.scss'
})
export class MieParti {
  private route = inject(ActivatedRoute);
  private service = inject(MiePartiService);

  /** Se la rotta indica una partitura, si vedono solo le parti di quella. */
  partituraId: number | null = this.leggiPartituraId();

  loading = signal(true);
  error = signal<string | null>(null);
  dati = signal<MiePartiRisposta | null>(null);
  ricerca = signal('');

  /** Le parti, filtrate dalla ricerca per nome di partitura. */
  parti = computed(() => {
    const termine = this.ricerca().trim().toLowerCase();
    const tutte = this.dati()?.parti ?? [];
    return termine ? tutte.filter((p) => p.partituraNome.toLowerCase().includes(termine)) : tutte;
  });

  nomePartitura = computed(() => this.dati()?.parti[0]?.partituraNome ?? null);

  constructor() {
    this.service.get(this.partituraId ?? undefined).subscribe({
      next: (dati) => {
        this.dati.set(dati);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare le tue parti. Riprova tra poco.');
        this.loading.set(false);
      }
    });
  }

  voci(parte: Parte): string {
    return parte.strumenti.map((s) => s.nome).join(', ');
  }

  pdfUrl(parteId: number): string {
    return this.service.pdfUrl(parteId);
  }

  private leggiPartituraId(): number | null {
    const id = this.route.snapshot.paramMap.get('id');
    return id === null ? null : Number(id);
  }
}