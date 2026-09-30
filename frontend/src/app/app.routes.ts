import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { BandistiList } from './features/bandisti/bandisti-list/bandisti-list';
import { BandistaForm } from './features/bandisti/bandista-form/bandista-form';
import { PartitureList } from './features/partiture/partiture-list/partiture-list';
import { PartituraForm } from './features/partiture/partitura-form/partitura-form';
import { AutoriList } from './features/autori/autori-list/autori-list';
import { AutoreForm } from './features/autori/autore-form/autore-form';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'bandisti', component: BandistiList },
  { path: 'bandisti/nuovo', component: BandistaForm },
  { path: 'bandisti/:id', component: BandistaForm },
  { path: 'partiture', component: PartitureList },
  { path: 'partiture/nuovo', component: PartituraForm },
  { path: 'partiture/:id', component: PartituraForm },
  { path: 'autori', component: AutoriList },
  { path: 'autori/nuovo', component: AutoreForm },
  { path: 'autori/:id', component: AutoreForm },
  { path: '**', redirectTo: '' }
];