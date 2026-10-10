import { Component, computed, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';

import { RUOLO_LABELS } from './features/auth/auth.model';
import { AuthService } from './features/auth/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  private router = inject(Router);
  protected auth = inject(AuthService);

  protected title = 'Partiturae';

  private readonly formazioneRoutes = ['/soci', '/formazione', '/direttivo'];

  formazioneMenuOpen = signal(this.isOnFormazioneRoute(this.router.url));

  ruoliEtichette = computed(() => (this.auth.account()?.ruoli ?? []).map((ruolo) => RUOLO_LABELS[ruolo]));

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

  private isOnFormazioneRoute(url: string): boolean {
    return this.formazioneRoutes.some((route) => url.startsWith(route));
  }
}