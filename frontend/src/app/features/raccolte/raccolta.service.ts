import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Raccolta, RaccoltaRequest } from './raccolta.model';

@Injectable({ providedIn: 'root' })
export class RaccoltaService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/raccolte`;

  getAll(): Observable<Raccolta[]> {
    return this.http.get<Raccolta[]>(this.baseUrl);
  }

  getById(id: number): Observable<Raccolta> {
    return this.http.get<Raccolta>(`${this.baseUrl}/${id}`);
  }

  create(request: RaccoltaRequest): Observable<Raccolta> {
    return this.http.post<Raccolta>(this.baseUrl, request);
  }

  update(id: number, request: RaccoltaRequest): Observable<Raccolta> {
    return this.http.put<Raccolta>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  addPartitura(raccoltaId: number, partituraId: number): Observable<Raccolta> {
    return this.http.post<Raccolta>(`${this.baseUrl}/${raccoltaId}/partiture`, { partituraId });
  }

  removePartitura(raccoltaId: number, partituraId: number): Observable<Raccolta> {
    return this.http.delete<Raccolta>(`${this.baseUrl}/${raccoltaId}/partiture/${partituraId}`);
  }

  reorder(raccoltaId: number, partituraIdsInOrdine: number[]): Observable<Raccolta> {
    return this.http.put<Raccolta>(`${this.baseUrl}/${raccoltaId}/partiture/ordine`, { partituraIdsInOrdine });
  }
}