import { Directive, TemplateRef, ViewContainerRef, effect, inject, input } from '@angular/core';

import { AuthService } from './auth.service';
import { Area, Azione } from './permessi';

/** Un permesso nella forma "area:azione", per esempio 'partiture:eliminare'. */
export type Permesso = `${Area}:${Azione}`;

/**
 * Mostra l'elemento solo se l'utente ha il permesso nella banda corrente.
 * Si usa con l'asterisco, per esempio:
 *
 *     <button *puo="'partiture:eliminare'">Elimina</button>
 *
 * È solo una comodità dell'interfaccia: la protezione vera sta sul server.
 */
@Directive({ selector: '[puo]' })
export class PuoDirective {
  private auth = inject(AuthService);
  private template = inject(TemplateRef<unknown>);
  private contenitore = inject(ViewContainerRef);

  readonly puo = input.required<Permesso>();

  private mostrato = false;

  constructor() {
    effect(() => {
      const [area, azione] = this.puo().split(':') as [Area, Azione];
      const consentito =
        azione === 'leggere'
          ? this.auth.puoLeggere(area)
          : azione === 'scrivere'
            ? this.auth.puoScrivere(area)
            : this.auth.puoEliminare(area);

      if (consentito && !this.mostrato) {
        this.contenitore.createEmbeddedView(this.template);
        this.mostrato = true;
      } else if (!consentito && this.mostrato) {
        this.contenitore.clear();
        this.mostrato = false;
      }
    });
  }
}
