import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Tesseramento, TesseramentoSummary } from './bandista.model';

@Injectable({ providedIn: 'root' })
export class TesseramentoService {
  private http = inject(HttpClient);

  private baseUrl(bandistaId: number): string {
    return `${environment.apiUrl}/bandisti/${bandistaId}/tesseramenti`;
  }

  getAll(bandistaId: number): Observable<Tesseramento[]> {
    return this.http.get<Tesseramento[]>(this.baseUrl(bandistaId));
  }

  getSummary(bandistaId: number): Observable<TesseramentoSummary> {
    return this.http.get<TesseramentoSummary>(`${this.baseUrl(bandistaId)}/summary`);
  }

  upsert(bandistaId: number, anno: number, tesserato: boolean): Observable<Tesseramento> {
    return this.http.put<Tesseramento>(`${this.baseUrl(bandistaId)}/${anno}`, { tesserato });
  }

  delete(bandistaId: number, anno: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl(bandistaId)}/${anno}`);
  }
}