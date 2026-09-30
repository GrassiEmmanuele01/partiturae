import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Autore, AutoreRequest } from './autore.model';

@Injectable({ providedIn: 'root' })
export class AutoreService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/autori`;

  getAll(): Observable<Autore[]> {
    return this.http.get<Autore[]>(this.baseUrl);
  }

  getById(id: number): Observable<Autore> {
    return this.http.get<Autore>(`${this.baseUrl}/${id}`);
  }

  create(request: AutoreRequest): Observable<Autore> {
    return this.http.post<Autore>(this.baseUrl, request);
  }

  update(id: number, request: AutoreRequest): Observable<Autore> {
    return this.http.put<Autore>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}