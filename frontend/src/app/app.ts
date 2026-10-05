import { Component, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  private router = inject(Router);

  protected title = 'Partiturae';

  private readonly bandaRoutes = ['/soci', '/banda', '/direttivo'];

  bandaMenuOpen = signal(this.isOnBandaRoute(this.router.url));

  constructor() {
    this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe((e) => {
        if (this.isOnBandaRoute(e.urlAfterRedirects)) {
          this.bandaMenuOpen.set(true);
        }
      });
  }

  toggleBandaMenu(): void {
    this.bandaMenuOpen.set(!this.bandaMenuOpen());
  }

  private isOnBandaRoute(url: string): boolean {
    return this.bandaRoutes.some((route) => url.startsWith(route));
  }
}