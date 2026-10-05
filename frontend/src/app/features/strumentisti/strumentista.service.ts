import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Strumentista, StrumentistaRequest } from './strumentista.model';

@Injectable({ providedIn: 'root' })
export class StrumentistaService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/strumentisti`;

  getAll(): Observable<Strumentista[]> {
    return this.http.get<Strumentista[]>(this.baseUrl);
  }

  getById(id: number): Observable<Strumentista> {
    return this.http.get<Strumentista>(`${this.baseUrl}/${id}`);
  }

  create(request: StrumentistaRequest): Observable<Strumentista> {
    return this.http.post<Strumentista>(this.baseUrl, request);
  }

  update(id: number, request: StrumentistaRequest): Observable<Strumentista> {
    return this.http.put<Strumentista>(`${this.baseUrl}/${id}`, request);
  }

  updateStrumenti(id: number, strumentoIds: number[]): Observable<Strumentista> {
    return this.http.put<Strumentista>(`${this.baseUrl}/${id}/strumenti`, { strumentoIds });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}