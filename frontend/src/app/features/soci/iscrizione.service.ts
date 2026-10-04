import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Iscrizione, IscrizioneSummary } from './socio.model';

@Injectable({ providedIn: 'root' })
export class IscrizioneService {
  private http = inject(HttpClient);

  private baseUrl(socioId: number): string {
    return `${environment.apiUrl}/soci/${socioId}/iscrizioni`;
  }

  getAll(socioId: number): Observable<Iscrizione[]> {
    return this.http.get<Iscrizione[]>(this.baseUrl(socioId));
  }

  getSummary(socioId: number): Observable<IscrizioneSummary> {
    return this.http.get<IscrizioneSummary>(`${this.baseUrl(socioId)}/summary`);
  }

  upsert(socioId: number, anno: number, iscritto: boolean, tesserato: boolean): Observable<Iscrizione> {
    return this.http.put<Iscrizione>(`${this.baseUrl(socioId)}/${anno}`, { iscritto, tesserato });
  }

  delete(socioId: number, anno: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl(socioId)}/${anno}`);
  }
}