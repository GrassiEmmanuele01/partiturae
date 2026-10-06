import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Musicista, MusicistaRequest } from './musicista.model';

@Injectable({ providedIn: 'root' })
export class MusicistaService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/musicisti`;

  getAll(): Observable<Musicista[]> {
    return this.http.get<Musicista[]>(this.baseUrl);
  }

  getById(id: number): Observable<Musicista> {
    return this.http.get<Musicista>(`${this.baseUrl}/${id}`);
  }

  create(request: MusicistaRequest): Observable<Musicista> {
    return this.http.post<Musicista>(this.baseUrl, request);
  }

  update(id: number, request: MusicistaRequest): Observable<Musicista> {
    return this.http.put<Musicista>(`${this.baseUrl}/${id}`, request);
  }

  updateStrumenti(id: number, strumentoIds: number[]): Observable<Musicista> {
    return this.http.put<Musicista>(`${this.baseUrl}/${id}/strumenti`, { strumentoIds });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}