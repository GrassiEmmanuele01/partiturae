import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Utilizzo } from '../../shared/utilizzo.model';
import { Strumento, StrumentoRequest } from './strumento.model';

@Injectable({ providedIn: 'root' })
export class StrumentoService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/strumenti`;

  getAll(): Observable<Strumento[]> {
    return this.http.get<Strumento[]>(this.baseUrl);
  }

  getByFamiglia(famigliaId: number): Observable<Strumento[]> {
    return this.http.get<Strumento[]>(this.baseUrl, { params: { famigliaId } });
  }

  getById(id: number): Observable<Strumento> {
    return this.http.get<Strumento>(`${this.baseUrl}/${id}`);
  }

  getUtilizzo(id: number): Observable<Utilizzo> {
    return this.http.get<Utilizzo>(`${this.baseUrl}/${id}/utilizzo`);
  }

  create(request: StrumentoRequest): Observable<Strumento> {
    return this.http.post<Strumento>(this.baseUrl, request);
  }

  update(id: number, request: StrumentoRequest): Observable<Strumento> {
    return this.http.put<Strumento>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}