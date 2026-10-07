import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Utilizzo } from '../../shared/utilizzo.model';
import { StrumentoFiglio, StrumentoFiglioRequest } from './strumento-figlio.model';

@Injectable({ providedIn: 'root' })
export class StrumentoFiglioService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/strumenti-figli`;

  getAll(): Observable<StrumentoFiglio[]> {
    return this.http.get<StrumentoFiglio[]>(this.baseUrl);
  }

  getByStrumento(strumentoId: number): Observable<StrumentoFiglio[]> {
    return this.http.get<StrumentoFiglio[]>(this.baseUrl, { params: { strumentoId } });
  }

  getById(id: number): Observable<StrumentoFiglio> {
    return this.http.get<StrumentoFiglio>(`${this.baseUrl}/${id}`);
  }

  getUtilizzo(id: number): Observable<Utilizzo> {
    return this.http.get<Utilizzo>(`${this.baseUrl}/${id}/utilizzo`);
  }

  create(request: StrumentoFiglioRequest): Observable<StrumentoFiglio> {
    return this.http.post<StrumentoFiglio>(this.baseUrl, request);
  }

  update(id: number, request: StrumentoFiglioRequest): Observable<StrumentoFiglio> {
    return this.http.put<StrumentoFiglio>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}