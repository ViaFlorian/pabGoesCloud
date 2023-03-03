import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { MonatsabschlussComponent } from './pages/monatsabschluss/monatsabschluss.component';
import { SharedModule } from '../../shared/shared.module';
import { MaterialModule } from '../../shared/material/material.module';
import { PipesModule } from '../../shared/pipes.module';
import { MonatsabschlussActionBarComponent } from './components/monatsabschluss-action-bar/monatsabschluss-action-bar.component';
import { FakturuebersichtComponent } from './pages/fakturuebersicht/fakturuebersicht.component';
import { ProjektbudgetComponent } from './pages/projektbudget/projektbudget.component';
import { SkontoBuchungComponent } from './pages/skonto-buchung/skonto-buchung.component';
import { ProjektbudgetActionBarComponent } from './components/projektbudget-action-bar/projektbudget-action-bar.component';
import { ProjektbudgetAuswahlFormComponent } from './components/projektbudget-auswahl-form/projektbudget-auswahl-form.component';
import { ProjektbudgetBudgetFormTabelleComponent } from './components/projektbudget-budget-form-tabelle/projektbudget-budget-form-tabelle.component';
import { FakturuebersichtActionBarComponent } from './components/fakturuebersicht-action-bar/fakturuebersicht-action-bar.component';
import { FakturuebersichtAuswahlFormComponent } from './components/fakturuebersicht-auswahl-form/fakturuebersicht-auswahl-form.component';
import { FakturuebersichtFakturFormTabelleComponent } from './components/fakturuebersicht-faktur-form-tabelle/fakturuebersicht-faktur-form-tabelle.component';
import { SkontoBuchungActionBarComponent } from './components/skonto-buchung-action-bar/skonto-buchung-action-bar.component';
import { SkontoBuchungAuswahlFormComponent } from './components/skonto-buchung-auswahl-form/skonto-buchung-auswahl-form.component';
import { SkontoBuchungSkontoFormTabelleComponent } from './components/skonto-buchung-skonto-form-tabelle/skonto-buchung-skonto-form-tabelle.component';

const routes: Routes = [
  {
    path: 'monatsabschluss',
    component: MonatsabschlussComponent,
  },
  {
    path: 'fakturuebersicht',
    component: FakturuebersichtComponent,
  },
  {
    path: 'projektbudget',
    component: ProjektbudgetComponent,
  },
  {
    path: 'skonto-buchung',
    component: SkontoBuchungComponent,
  },
];

@NgModule({
  declarations: [
    MonatsabschlussComponent,
    MonatsabschlussActionBarComponent,
    FakturuebersichtComponent,
    FakturuebersichtActionBarComponent,
    FakturuebersichtAuswahlFormComponent,
    FakturuebersichtFakturFormTabelleComponent,
    ProjektbudgetComponent,
    ProjektbudgetActionBarComponent,
    ProjektbudgetAuswahlFormComponent,
    ProjektbudgetBudgetFormTabelleComponent,
    SkontoBuchungComponent,
    SkontoBuchungActionBarComponent,
    SkontoBuchungAuswahlFormComponent,
    SkontoBuchungSkontoFormTabelleComponent,
  ],
  imports: [CommonModule, SharedModule, MaterialModule, PipesModule, RouterModule.forChild(routes)],
})
export class VerwaltungModule {}
