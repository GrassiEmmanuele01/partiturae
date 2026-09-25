import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { BandistiList } from './features/bandisti/bandisti-list/bandisti-list';
import { BandistaForm } from './features/bandisti/bandista-form/bandista-form';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'bandisti', component: BandistiList },
  { path: 'bandisti/nuovo', component: BandistaForm },
  { path: 'bandisti/:id', component: BandistaForm },
  { path: '**', redirectTo: '' }
];