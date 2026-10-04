import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Socio, SocioRequest } from './socio.model';

@Injectable({ providedIn: 'root' })
export class SocioService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/soci`;

  getAll(): Observable<Socio[]> {
    return this.http.get<Socio[]>(this.baseUrl);
  }

  getById(id: number): Observable<Socio> {
    return this.http.get<Socio>(`${this.baseUrl}/${id}`);
  }

  create(request: SocioRequest): Observable<Socio> {
    return this.http.post<Socio>(this.baseUrl, request);
  }

  update(id: number, request: SocioRequest): Observable<Socio> {
    return this.http.put<Socio>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}