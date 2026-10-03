import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Libretto, LibrettoRequest } from './libretto.model';

@Injectable({ providedIn: 'root' })
export class LibrettoService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/libretti`;

  getAll(): Observable<Libretto[]> {
    return this.http.get<Libretto[]>(this.baseUrl);
  }

  getById(id: number): Observable<Libretto> {
    return this.http.get<Libretto>(`${this.baseUrl}/${id}`);
  }

  create(request: LibrettoRequest): Observable<Libretto> {
    return this.http.post<Libretto>(this.baseUrl, request);
  }

  update(id: number, request: LibrettoRequest): Observable<Libretto> {
    return this.http.put<Libretto>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  addPartitura(librettoId: number, partituraId: number): Observable<Libretto> {
    return this.http.post<Libretto>(`${this.baseUrl}/${librettoId}/partiture`, { partituraId });
  }

  removePartitura(librettoId: number, partituraId: number): Observable<Libretto> {
    return this.http.delete<Libretto>(`${this.baseUrl}/${librettoId}/partiture/${partituraId}`);
  }

  reorder(librettoId: number, partituraIdsInOrdine: number[]): Observable<Libretto> {
    return this.http.put<Libretto>(`${this.baseUrl}/${librettoId}/partiture/ordine`, { partituraIdsInOrdine });
  }
}