import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Evento, TIPO_EVENTO_LABELS } from '../evento.model';
import { EventoService } from '../evento.service';

@Component({
  selector: 'app-eventi-list',
  imports: [RouterLink],
  templateUrl: './eventi-list.html',
  styleUrl: './eventi-list.scss'
})
export class EventiList {
  private eventoService = inject(EventoService);

  eventi = signal<Evento[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  tipoLabels = TIPO_EVENTO_LABELS;

  constructor() {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.eventoService.getAll().subscribe({
      next: (data) => {
        this.eventi.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossibile caricare gli eventi. Controlla che il backend sia avviato.');
        this.loading.set(false);
      }
    });
  }

  remove(id: number): void {
    if (!confirm('Eliminare questo evento? Vengono eliminate anche le presenze registrate.')) {
      return;
    }

    this.eventoService.delete(id).subscribe({
      next: () => this.load(),
      error: () => this.error.set("Errore durante l'eliminazione.")
    });
  }
}