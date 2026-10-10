import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Formazione, FormazioneRequest } from './formazione.model';

@Injectable({ providedIn: 'root' })
export class FormazioneService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/formazione`;

  get(): Observable<Formazione> {
    return this.http.get<Formazione>(this.baseUrl);
  }

  save(request: FormazioneRequest): Observable<Formazione> {
    return this.http.put<Formazione>(this.baseUrl, request);
  }

  uploadLogo(file: File): Observable<void> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<void>(`${this.baseUrl}/logo`, formData);
  }

  /** Il logo si chiede con il token (un tag <img> non può mandarlo) e si mostra come file in memoria. */
  caricaLogo(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/logo`, { responseType: 'blob' });
  }
}