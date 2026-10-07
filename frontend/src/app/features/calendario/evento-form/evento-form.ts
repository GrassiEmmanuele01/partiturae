import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { TIPO_EVENTO_LABELS, TipoEvento } from '../evento.model';
import { EventoService } from '../evento.service';

@Component({
  selector: 'app-evento-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './evento-form.html',
  styleUrl: './evento-form.scss'
})
export class EventoForm {
  private fb = inject(FormBuilder);
  private eventoService = inject(EventoService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  eventoId = signal<number | null>(null);
  loading = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);

  tipi = Object.entries(TIPO_EVENTO_LABELS) as [TipoEvento, string][];

  form = this.fb.nonNullable.group({
    titolo: ['', Validators.required],
    tipo: this.fb.control<TipoEvento | null>(null, Validators.required),
    data: ['', Validators.required],
    ora: [''],
    luogo: [''],
    note: ['']
  });

  constructor() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.eventoId.set(id);
      this.loading.set(true);

      this.eventoService.getById(id).subscribe({
        next: (evento) => {
          this.form.patchValue({
            titolo: evento.titolo,
            tipo: evento.tipo,
            data: evento.data,
            ora: evento.ora ? evento.ora.substring(0, 5) : '',
            luogo: evento.luogo ?? '',
            note: evento.note ?? ''
          });
          this.loading.set(false);
        },
        error: () => {
          this.error.set("Impossibile caricare l'evento.");
          this.loading.set(false);
        }
      });
    }
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.error.set(null);

    const value = this.form.getRawValue();
    const request = {
      titolo: value.titolo,
      tipo: value.tipo as TipoEvento,
      data: value.data,
      ora: value.ora ? value.ora + ':00' : null,
      luogo: value.luogo || null,
      note: value.note || null
    };

    const id = this.eventoId();
    const request$ = id
      ? this.eventoService.update(id, request)
      : this.eventoService.create(request);

    request$.subscribe({
      next: () => this.router.navigate(['/calendario']),
      error: (err) => {
        this.saving.set(false);
        if (err.status === 400 && err.error?.errors) {
          this.error.set(Object.values(err.error.errors).join(', '));
        } else {
          this.error.set('Errore durante il salvataggio.');
        }
      }
    });
  }
}