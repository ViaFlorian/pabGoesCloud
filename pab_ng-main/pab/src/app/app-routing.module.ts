import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MsalGuard } from '@azure/msal-angular';
import { DevelopmentGuard } from './struktur/guard/development.guard';

const routes: Routes = [
  {
    path: '',
    loadChildren: () => import('./routes/dashboard/dashboard.module').then((m) => m.DashboardModule),
    canActivate: [MsalGuard],
  },
  {
    path: 'arbeitsnachweis',
    loadChildren: () =>
      import('./routes/arbeitsnachweise/arbeitsnachweise.module').then((m) => m.ArbeitsnachweiseModule),
    canActivate: [MsalGuard],
  },
  {
    path: 'projektabrechnung',
    loadChildren: () =>
      import('./routes/projektabrechnung/projektabrechnung.module').then((m) => m.ProjektabrechnungModule),
    canActivate: [MsalGuard],
  },
  {
    path: 'mitarbeiter',
    loadChildren: () => import('./routes/mitarbeiter/mitarbeiter.module').then((m) => m.MitarbeiterModule),
    canActivate: [MsalGuard],
  },
  {
    path: 'berichte',
    loadChildren: () => import('./routes/berichte/berichte.module').then((m) => m.BerichteModule),
    canActivate: [MsalGuard],
  },
  {
    path: 'verwaltung',
    loadChildren: () => import('./routes/verwaltung/verwaltung.module').then((m) => m.VerwaltungModule),
    canActivate: [MsalGuard],
  },
  {
    path: 'testentry',
    loadChildren: () => import('./routes/testseite/testseite.module').then((m) => m.TestseiteModule),
    canMatch: [DevelopmentGuard],
  },
];

const isIframe = window !== window.parent && !window.opener;

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      initialNavigation: !isIframe ? 'enabledNonBlocking' : 'disabled',
    }),
  ],
  exports: [RouterModule],
})
export class AppRoutingModule {}
