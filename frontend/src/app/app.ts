import { Component, HostListener, computed, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';

import { environment } from '../environments/environment';
import { RUOLO_LABELS } from './features/auth/auth.model';
import { AuthService } from './features/auth/auth.service';
import { FileService } from './shared/file.service';
import { NotificheService } from './shared/notifiche.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  private router = inject(Router);
  private file = inject(FileService);
  protected auth = inject(AuthService);
  protected notifiche = inject(NotificheService);

  protected title = 'Partiturae';

  private readonly formazioneRoutes = ['/soci', '/formazione', '/direttivo'];

  formazioneMenuOpen = signal(this.isOnFormazioneRoute(this.router.url));

  ruoliEtichette = computed(() => (this.auth.account()?.ruoli ?? []).map((ruolo) => RUOLO_LABELS[ruolo]));

  // Il gruppo "Formazione" compare solo se dentro c'è almeno una voce permessa.
  mostraGruppoFormazione = computed(() => this.auth.puoLeggere('soci') || this.auth.puoLeggere('formazione'));

  constructor() {
    this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe((e) => {
        if (this.isOnFormazioneRoute(e.urlAfterRedirects)) {
          this.formazioneMenuOpen.set(true);
        }
      });
  }

  toggleFormazioneMenu(): void {
    this.formazioneMenuOpen.set(!this.formazioneMenuOpen());
  }

  esci(): void {
    this.auth.logout();
  }

  /**
   * Un normale link verso l'API (es. il PDF di una parte) non può mandare il token di accesso:
   * lo intercettiamo e il file viene scaricato con una richiesta autenticata.
   */
  @HostListener('document:click', ['$event'])
  gestisciLinkApi(evento: MouseEvent): void {
    if (evento.defaultPrevented || evento.button !== 0 || evento.ctrlKey || evento.metaKey || evento.shiftKey) {
      return;
    }

    const link = (evento.target as HTMLElement | null)?.closest?.('a[href]') as HTMLAnchorElement | null;
    if (!link || !link.href.startsWith(`${environment.apiUrl}/`)) {
      return;
    }

    evento.preventDefault();
    this.file.scarica(link.href);
  }

  private isOnFormazioneRoute(url: string): boolean {
    return this.formazioneRoutes.some((route) => url.startsWith(route));
  }
}