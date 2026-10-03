import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Banda, BandaRequest } from './banda.model';

@Injectable({ providedIn: 'root' })
export class BandaService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/banda`;

  get(): Observable<Banda> {
    return this.http.get<Banda>(this.baseUrl);
  }

  save(request: BandaRequest): Observable<Banda> {
    return this.http.put<Banda>(this.baseUrl, request);
  }

  uploadLogo(file: File): Observable<void> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<void>(`${this.baseUrl}/logo`, formData);
  }

  logoUrl(): string {
    return `${this.baseUrl}/logo`;
  }
}