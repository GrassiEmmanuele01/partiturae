import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { BandistiList } from './features/bandisti/bandisti-list/bandisti-list';
import { BandistaForm } from './features/bandisti/bandista-form/bandista-form';
import { PartitureList } from './features/partiture/partiture-list/partiture-list';
import { PartituraForm } from './features/partiture/partitura-form/partitura-form';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'bandisti', component: BandistiList },
  { path: 'bandisti/nuovo', component: BandistaForm },
  { path: 'bandisti/:id', component: BandistaForm },
  { path: 'partiture', component: PartitureList },
  { path: 'partiture/nuovo', component: PartituraForm },
  { path: 'partiture/:id', component: PartituraForm },
  { path: '**', redirectTo: '' }
];