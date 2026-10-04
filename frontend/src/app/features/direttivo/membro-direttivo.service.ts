import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { MembroDirettivo, MembroDirettivoRequest } from './membro-direttivo.model';

@Injectable({ providedIn: 'root' })
export class MembroDirettivoService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/direttivo`;

  getAll(): Observable<MembroDirettivo[]> {
    return this.http.get<MembroDirettivo[]>(this.baseUrl);
  }

  create(request: MembroDirettivoRequest): Observable<MembroDirettivo> {
    return this.http.post<MembroDirettivo>(this.baseUrl, request);
  }

  update(id: number, request: MembroDirettivoRequest): Observable<MembroDirettivo> {
    return this.http.put<MembroDirettivo>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}