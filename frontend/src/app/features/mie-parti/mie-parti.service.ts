import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { MiePartiRisposta } from './mie-parti.model';

@Injectable({ providedIn: 'root' })
export class MiePartiService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/mie-parti`;

  /** Le mie parti; se si indica una partitura, solo quelle di quella partitura. */
  get(partituraId?: number): Observable<MiePartiRisposta> {
    return this.http.get<MiePartiRisposta>(this.baseUrl, {
      params: partituraId === undefined ? {} : { partituraId }
    });
  }

  pdfUrl(parteId: number): string {
    return `${this.baseUrl}/${parteId}/pdf`;
  }
}