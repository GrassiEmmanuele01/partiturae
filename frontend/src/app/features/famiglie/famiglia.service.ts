import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Famiglia, FamigliaRequest } from './famiglia.model';

@Injectable({ providedIn: 'root' })
export class FamigliaService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/famiglie`;

  getAll(): Observable<Famiglia[]> {
    return this.http.get<Famiglia[]>(this.baseUrl);
  }

  getById(id: number): Observable<Famiglia> {
    return this.http.get<Famiglia>(`${this.baseUrl}/${id}`);
  }

  create(request: FamigliaRequest): Observable<Famiglia> {
    return this.http.post<Famiglia>(this.baseUrl, request);
  }

  update(id: number, request: FamigliaRequest): Observable<Famiglia> {
    return this.http.put<Famiglia>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}