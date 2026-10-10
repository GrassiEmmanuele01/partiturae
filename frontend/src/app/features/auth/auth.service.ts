import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, finalize, map, of, shareReplay, tap } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Account, AuthResponse, Ruolo } from './auth.model';
import { Area, haPermesso } from './permessi';

// Promemoria (non segreto) che dice "in questo browser c'è stata una sessione": serve solo
// a evitare una richiesta di rinnovo inutile quando si apre il sito per la prima volta.
const SESSIONE_HINT = 'partiturae.sessione';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private readonly baseUrl = `${environment.apiUrl}/auth`;

  // Il token di accesso vive solo in memoria. Ricaricando la pagina si recupera con il cookie di sessione
  // (HttpOnly, quindi invisibile al JavaScript).
  private readonly tokenSignal = signal<string | null>(null);
  private readonly accountSignal = signal<Account | null>(null);
  private rinnovoInCorso$: Observable<AuthResponse> | null = null;

  readonly accessToken = this.tokenSignal.asReadonly();
  readonly account = this.accountSignal.asReadonly();
  readonly isAuthenticated = computed(() => this.accountSignal() !== null);
  readonly isSuperadmin = computed(() => this.accountSignal()?.superadmin ?? false);
  readonly bandaCorrente = computed(() => this.accountSignal()?.bandaCorrente ?? null);
  readonly bande = computed(() => this.accountSignal()?.bande ?? []);

  readonly nomeVisualizzato = computed(() => {
    const account = this.accountSignal();
    if (!account) {
      return '';
    }
    const nomeCompleto = [account.nome, account.cognome].filter(Boolean).join(' ');
    return nomeCompleto || account.email;
  });

  login(email: string, password: string): Observable<Account> {
    return this.http
      .post<AuthResponse>(`${this.baseUrl}/login`, { email, password }, { withCredentials: true })
      .pipe(
        tap((risposta) => this.impostaSessione(risposta)),
        map((risposta) => risposta.account)
      );
  }

  /** Passa a un'altra banda in cui si è presenti: il server emette un nuovo token (con i ruoli di quella banda). */
  cambiaBanda(bandaId: number): Observable<Account> {
    return this.http
      .post<AuthResponse>(`${this.baseUrl}/banda`, { bandaId }, { withCredentials: true })
      .pipe(
        tap((risposta) => this.impostaSessione(risposta)),
        map((risposta) => risposta.account)
      );
  }

  /**
   * Ottiene un nuovo token usando il cookie di sessione. Se più richieste lo chiedono nello stesso
   * momento si fa una sola chiamata: ogni cookie vale una volta sola, e usarlo due volte
   * verrebbe scambiato dal server per un furto di sessione.
   */
  refresh(): Observable<AuthResponse> {
    if (!this.rinnovoInCorso$) {
      this.rinnovoInCorso$ = this.http
        .post<AuthResponse>(`${this.baseUrl}/refresh`, {}, { withCredentials: true })
        .pipe(
          tap((risposta) => this.impostaSessione(risposta)),
          finalize(() => {
            this.rinnovoInCorso$ = null;
          }),
          shareReplay({ bufferSize: 1, refCount: false })
        );
    }
    return this.rinnovoInCorso$;
  }

  /** Chiamato all'avvio dell'app: se c'è una sessione valida la ripristina, altrimenti resta "non entrato". */
  restoreSession(): Observable<boolean> {
    if (!localStorage.getItem(SESSIONE_HINT)) {
      return of(false);
    }

    return this.refresh().pipe(
      map(() => true),
      catchError((errore: HttpErrorResponse) => {
        // 401 = sessione davvero finita. Altri errori (es. server spento) non cancellano il promemoria.
        if (errore.status === 401) {
          localStorage.removeItem(SESSIONE_HINT);
        }
        return of(false);
      })
    );
  }

  logout(): void {
    this.http
      .post<void>(`${this.baseUrl}/logout`, {}, { withCredentials: true })
      .pipe(catchError(() => of(null)))
      .subscribe();

    this.azzeraSessione();
    this.router.navigate(['/login']);
  }

  /** La sessione non è più rinnovabile: si torna al login, ricordando la pagina da cui si veniva. */
  sessioneScaduta(): void {
    const urlCorrente = this.router.url;
    this.azzeraSessione();

    if (!urlCorrente.startsWith('/login')) {
      this.router.navigate(['/login'], { queryParams: { redirect: urlCorrente } });
    }
  }

  // I dati di lavoro esistono solo dentro una banda: senza banda (es. un superadmin appena entrato)
  // non c'è niente da leggere né da modificare.
  puoLeggere(area: Area): boolean {
    return this.bandaCorrente() !== null && haPermesso(this.accountSignal()?.ruoli ?? [], area, 'leggere');
  }

  puoScrivere(area: Area): boolean {
    return this.bandaCorrente() !== null && haPermesso(this.accountSignal()?.ruoli ?? [], area, 'scrivere');
  }

  hasRole(...ruoli: Ruolo[]): boolean {
    const mieiRuoli = this.accountSignal()?.ruoli ?? [];
    return ruoli.some((ruolo) => mieiRuoli.includes(ruolo));
  }

  private impostaSessione(risposta: AuthResponse): void {
    this.tokenSignal.set(risposta.accessToken);
    this.accountSignal.set(risposta.account);
    localStorage.setItem(SESSIONE_HINT, '1');
  }

  private azzeraSessione(): void {
    this.tokenSignal.set(null);
    this.accountSignal.set(null);
    localStorage.removeItem(SESSIONE_HINT);
  }
}