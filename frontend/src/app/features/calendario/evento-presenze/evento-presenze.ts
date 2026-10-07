import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Evento, Presenza, TIPO_EVENTO_LABELS } from '../evento.model';
import { EventoService } from '../evento.service';

@Component({
  selector: 'app-evento-presenze',
  imports: [RouterLink],
  templateUrl: './evento-presenze.html',
  styleUrl: './evento-presenze.scss'
})
export class EventoPresenze {
  private route = inject(ActivatedRoute);
  private eventoService = inject(EventoService);

  eventoId = Number(this.route.snapshot.paramMap.get('id'));

  evento = signal<Evento | null>(null);
  presenze = signal<Presenza[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  savingSocioId = signal<number | null>(null);
  tipoLabels = TIPO_EVENTO_LABELS;

  constructor() {
    this.eventoService.getById(this.eventoId).subscribe({
      next: (data) => this.evento.set(data),
      error: () => this.error.set("Impossibile caricare l'evento.")
    });

    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.eventoService.getPresenze(this.eventoId).subscribe({
      next: (data) => {
        this.presenze.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare le presenze.');
        this.loading.set(false);
      }
    });
  }

  toggle(presenza: Presenza): void {
    this.savingSocioId.set(presenza.socioId);
    const nuovoStato = !presenza.presente;

    this.eventoService.setPresenza(this.eventoId, presenza.socioId, nuovoStato).subscribe({
      next: () => {
        this.presenze.set(
          this.presenze().map((p) => (p.socioId === presenza.socioId ? { ...p, presente: nuovoStato } : p))
        );
        this.savingSocioId.set(null);
      },
      error: () => {
        this.savingSocioId.set(null);
        this.error.set('Errore durante il salvataggio della presenza.');
      }
    });
  }

  get presentiCount(): number {
    return this.presenze().filter((p) => p.presente).length;
  }
}