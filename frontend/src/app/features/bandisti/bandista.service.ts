import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Bandista, BandistaRequest } from './bandista.model';

@Injectable({ providedIn: 'root' })
export class BandistaService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/bandisti`;

  getAll(): Observable<Bandista[]> {
    return this.http.get<Bandista[]>(this.baseUrl);
  }

  getById(id: number): Observable<Bandista> {
    return this.http.get<Bandista>(`${this.baseUrl}/${id}`);
  }

  create(request: BandistaRequest): Observable<Bandista> {
    return this.http.post<Bandista>(this.baseUrl, request);
  }

  update(id: number, request: BandistaRequest): Observable<Bandista> {
    return this.http.put<Bandista>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}