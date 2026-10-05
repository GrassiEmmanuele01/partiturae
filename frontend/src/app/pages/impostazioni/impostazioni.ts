import { Component } from '@angular/core';

@Component({
  selector: 'app-impostazioni',
  template: `
    <div class="page-header">
      <h2>Impostazioni</h2>
    </div>
    <p>Qui arriverà la gestione di autori, strumenti, famiglie e sottostrumenti — con l'elenco di dove vengono usati prima di ogni eliminazione.</p>
  `,
  styles: [`
    .page-header { margin-bottom: 1.5rem; }
    p { color: var(--ink); opacity: 0.7; max-width: 60ch; }
  `]
})
export class Impostazioni {}