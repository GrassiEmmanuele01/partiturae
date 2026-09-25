import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { BandistiList } from './features/bandisti/bandisti-list';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'bandisti', component: BandistiList },
  { path: '**', redirectTo: '' }
];