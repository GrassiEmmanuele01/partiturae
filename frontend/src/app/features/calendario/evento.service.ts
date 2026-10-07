import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Evento, EventoRequest, Presenza } from './evento.model';

@Injectable({ providedIn: 'root' })
export class EventoService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/eventi`;

  getAll(): Observable<Evento[]> {
    return this.http.get<Evento[]>(this.baseUrl);
  }

  getById(id: number): Observable<Evento> {
    return this.http.get<Evento>(`${this.baseUrl}/${id}`);
  }

  create(request: EventoRequest): Observable<Evento> {
    return this.http.post<Evento>(this.baseUrl, request);
  }

  update(id: number, request: EventoRequest): Observable<Evento> {
    return this.http.put<Evento>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getPresenze(eventoId: number): Observable<Presenza[]> {
    return this.http.get<Presenza[]>(`${this.baseUrl}/${eventoId}/presenze`);
  }

  setPresenza(eventoId: number, socioId: number, presente: boolean): Observable<Presenza> {
    return this.http.put<Presenza>(`${this.baseUrl}/${eventoId}/presenze/${socioId}`, { presente });
  }
}