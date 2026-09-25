import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Autore } from './autore.model';

@Injectable({ providedIn: 'root' })
export class AutoreService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/autori`;

  getAll(): Observable<Autore[]> {
    return this.http.get<Autore[]>(this.baseUrl);
  }
}