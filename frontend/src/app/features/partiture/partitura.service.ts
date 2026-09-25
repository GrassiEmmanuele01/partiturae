import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Partitura, PartituraRequest } from './partitura.model';

@Injectable({ providedIn: 'root' })
export class PartituraService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/partiture`;

  getAll(): Observable<Partitura[]> {
    return this.http.get<Partitura[]>(this.baseUrl);
  }

  getById(id: number): Observable<Partitura> {
    return this.http.get<Partitura>(`${this.baseUrl}/${id}`);
  }

  create(request: PartituraRequest): Observable<Partitura> {
    return this.http.post<Partitura>(this.baseUrl, request);
  }

  update(id: number, request: PartituraRequest): Observable<Partitura> {
    return this.http.put<Partitura>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}