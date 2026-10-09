import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Parte, ParteRequest } from './parte.model';

@Injectable({ providedIn: 'root' })
export class ParteService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/parti`;

  getByPartitura(partituraId: number): Observable<Parte[]> {
    return this.http.get<Parte[]>(this.baseUrl, { params: { partituraId } });
  }

  getByStrumentoFiglio(strumentoFiglioId: number): Observable<Parte[]> {
    return this.http.get<Parte[]>(this.baseUrl, { params: { strumentoFiglioId } });
  }

  getByStrumento(strumentoId: number): Observable<Parte[]> {
    return this.http.get<Parte[]>(this.baseUrl, { params: { strumentoId } });
  }

  create(request: ParteRequest): Observable<Parte> {
    return this.http.post<Parte>(this.baseUrl, request);
  }

  updateStrumenti(id: number, strumentoFiglioIds: number[]): Observable<Parte> {
    return this.http.put<Parte>(`${this.baseUrl}/${id}/strumenti`, { strumentoFiglioIds });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  uploadPdf(id: number, file: File): Observable<void> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<void>(`${this.baseUrl}/${id}/pdf`, formData);
  }

  pdfUrl(id: number): string {
    return `${this.baseUrl}/${id}/pdf`;
  }
}
