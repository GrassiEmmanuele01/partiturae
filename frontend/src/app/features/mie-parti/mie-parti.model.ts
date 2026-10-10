import { Parte } from '../parti/parte.model';

/** Le parti degli strumenti del proprio profilo musicale. */
export interface MiePartiRisposta {
  /** False se la persona non è collegata a un socio con profilo musicale: serve a spiegare perché non c'è nulla. */
  profiloTrovato: boolean;
  /** I nomi degli strumenti del profilo (es. Tromba, Flicorno). */
  strumenti: string[];
  parti: Parte[];
}