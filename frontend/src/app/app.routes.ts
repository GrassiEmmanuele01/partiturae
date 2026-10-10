import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Login } from './features/auth/login/login';
import { authGuard, guestGuard, permessoGuard } from './features/auth/auth.guard';
import { Impostazioni } from './pages/impostazioni/impostazioni';
import { SociList } from './features/soci/soci-list/soci-list';
import { SocioForm } from './features/soci/socio-form/socio-form';
import { DirettivoList } from './features/direttivo/direttivo-list/direttivo-list';
import { DirettivoForm } from './features/direttivo/direttivo-form/direttivo-form';
import { MusicistiList } from './features/musicisti/musicisti-list/musicisti-list';
import { MusicistaForm } from './features/musicisti/musicista-form/musicista-form';
import { MusicistaMusicale } from './features/musicisti/musicista-musicale/musicista-musicale';
import { PartitureList } from './features/partiture/partiture-list/partiture-list';
import { PartituraForm } from './features/partiture/partitura-form/partitura-form';
import { PartituraParti } from './features/parti/partitura-parti/partitura-parti';
import { StrumentoParti } from './features/parti/strumento-parti/strumento-parti';
import { StrumentoFiglioParti } from './features/parti/strumento-figlio-parti/strumento-figlio-parti';
import { RaccolteList } from './features/raccolte/raccolte-list/raccolte-list';
import { RaccoltaForm } from './features/raccolte/raccolta-form/raccolta-form';
import { RaccoltaDettaglio } from './features/raccolte/raccolta-dettaglio/raccolta-dettaglio';
import { AutoriList } from './features/autori/autori-list/autori-list';
import { AutoreForm } from './features/autori/autore-form/autore-form';
import { FamiglieList } from './features/famiglie/famiglie-list/famiglie-list';
import { FamigliaForm } from './features/famiglie/famiglia-form/famiglia-form';
import { StrumentiList } from './features/strumenti/strumenti-list/strumenti-list';
import { StrumentoForm } from './features/strumenti/strumento-form/strumento-form';
import { StrumentiFigliList } from './features/strumenti-figli/strumenti-figli-list/strumenti-figli-list';
import { StrumentoFiglioForm } from './features/strumenti-figli/strumento-figlio-form/strumento-figlio-form';
import { FormazioneSettings } from './features/formazione/formazione-settings/formazione-settings';
import { EventiList } from './features/calendario/eventi-list/eventi-list';
import { EventoForm } from './features/calendario/evento-form/evento-form';
import { EventoPresenze } from './features/calendario/evento-presenze/evento-presenze';

export const routes: Routes = [
  { path: 'login', component: Login, canActivate: [guestGuard] },
  {
    path: '',
    canActivateChild: [authGuard],
    children: [
      { path: '', component: Home },
      { path: 'impostazioni', component: Impostazioni, canActivate: [permessoGuard('catalogo', 'scrivere')] },
      { path: 'soci', component: SociList, canActivate: [permessoGuard('soci')] },
      { path: 'soci/nuovo', component: SocioForm, canActivate: [permessoGuard('soci', 'scrivere')] },
      { path: 'soci/:id', component: SocioForm, canActivate: [permessoGuard('soci', 'scrivere')] },
      { path: 'direttivo', component: DirettivoList, canActivate: [permessoGuard('direttivo')] },
      { path: 'direttivo/nuovo', component: DirettivoForm, canActivate: [permessoGuard('direttivo', 'scrivere')] },
      { path: 'direttivo/:id', component: DirettivoForm, canActivate: [permessoGuard('direttivo', 'scrivere')] },
      { path: 'musicisti', component: MusicistiList, canActivate: [permessoGuard('musicisti')] },
      { path: 'musicisti/nuovo', component: MusicistaForm, canActivate: [permessoGuard('musicisti', 'scrivere')] },
      { path: 'musicisti/:id/musicale', component: MusicistaMusicale, canActivate: [permessoGuard('musicisti', 'scrivere')] },
      { path: 'musicisti/:id', component: MusicistaForm, canActivate: [permessoGuard('musicisti', 'scrivere')] },
      { path: 'partiture', component: PartitureList, canActivate: [permessoGuard('partiture')] },
      { path: 'partiture/nuovo', component: PartituraForm, canActivate: [permessoGuard('partiture', 'scrivere')] },
      { path: 'partiture/:id/parti', component: PartituraParti, canActivate: [permessoGuard('parti')] },
      { path: 'partiture/:id', component: PartituraForm, canActivate: [permessoGuard('partiture', 'scrivere')] },
      { path: 'raccolte', component: RaccolteList, canActivate: [permessoGuard('raccolte')] },
      { path: 'raccolte/nuovo', component: RaccoltaForm, canActivate: [permessoGuard('raccolte', 'scrivere')] },
      { path: 'raccolte/:id/modifica', component: RaccoltaForm, canActivate: [permessoGuard('raccolte', 'scrivere')] },
      { path: 'raccolte/:id', component: RaccoltaDettaglio, canActivate: [permessoGuard('raccolte')] },
      { path: 'autori', component: AutoriList, canActivate: [permessoGuard('catalogo')] },
      { path: 'autori/nuovo', component: AutoreForm, canActivate: [permessoGuard('catalogo', 'scrivere')] },
      { path: 'autori/:id', component: AutoreForm, canActivate: [permessoGuard('catalogo', 'scrivere')] },
      { path: 'famiglie', component: FamiglieList, canActivate: [permessoGuard('catalogo')] },
      { path: 'famiglie/nuovo', component: FamigliaForm, canActivate: [permessoGuard('catalogo', 'scrivere')] },
      { path: 'famiglie/:id', component: FamigliaForm, canActivate: [permessoGuard('catalogo', 'scrivere')] },
      { path: 'strumenti', component: StrumentiList, canActivate: [permessoGuard('catalogo')] },
      { path: 'strumenti/nuovo', component: StrumentoForm, canActivate: [permessoGuard('catalogo', 'scrivere')] },
      { path: 'strumenti/:id/parti', component: StrumentoParti, canActivate: [permessoGuard('parti')] },
      { path: 'strumenti/:id', component: StrumentoForm, canActivate: [permessoGuard('catalogo', 'scrivere')] },
      { path: 'strumenti-figli', component: StrumentiFigliList, canActivate: [permessoGuard('catalogo')] },
      { path: 'strumenti-figli/nuovo', component: StrumentoFiglioForm, canActivate: [permessoGuard('catalogo', 'scrivere')] },
      { path: 'strumenti-figli/:id/parti', component: StrumentoFiglioParti, canActivate: [permessoGuard('parti')] },
      { path: 'strumenti-figli/:id', component: StrumentoFiglioForm, canActivate: [permessoGuard('catalogo', 'scrivere')] },
      { path: 'formazione', component: FormazioneSettings, canActivate: [permessoGuard('formazione')] },
      { path: 'calendario', component: EventiList, canActivate: [permessoGuard('calendario')] },
      { path: 'calendario/nuovo', component: EventoForm, canActivate: [permessoGuard('calendario', 'scrivere')] },
      { path: 'calendario/:id/presenze', component: EventoPresenze, canActivate: [permessoGuard('presenze')] },
      { path: 'calendario/:id/modifica', component: EventoForm, canActivate: [permessoGuard('calendario', 'scrivere')] },
    ]
  },
  { path: '**', redirectTo: '' }
];