import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../../shared/shared.module';
import { MaterialModule } from '../../shared/material/material.module';
import { RouterModule, Routes } from '@angular/router';
import { ProjektabrechnungUebersichtComponent } from './pages/projektabrechnung-uebersicht/projektabrechnung-uebersicht';
import { ProjektabrechnungBearbeitenComponent } from './pages/projektabrechnung-bearbeiten/projektabrechnung-bearbeiten.component';
import { ProjektabrechnungUebersichtActionBarComponent } from './components/projektabrechnung-uebersicht-action-bar/projektabrechnung-uebersicht-action-bar.component';
import { ProjektabrechnungBearbeitenActionBarComponent } from './components/projektabrechnung-bearbeiten-action-bar/projektabrechnung-bearbeiten-action-bar.component';
import { ProjektabrechnungUebersichtTabelleComponent } from './components/projektabrechnung-uebersicht-tabelle/projektabrechnung-uebersicht-tabelle.component';
import { ProjektabrechnungUebersichtFilterComponent } from './components/projektabrechnung-uebersicht-filter/projektabrechnung-uebersicht-filter.component';
import { ProjektabrechnungAuslagenComponent } from './pages/projektabrechnung-auslagen/projektabrechnung-auslagen.component';
import { ProjektabrechnungAuslagenActionBarComponent } from './components/projektabrechnung-auslagen-action-bar/projektabrechnung-auslagen-action-bar.component';
import { ProjektabrechnungAuslagenFilterComponent } from './components/projektabrechnung-auslagen-filter/projektabrechnung-auslagen-filter.component';
import { ProjektabrechnungAuslagenFormTabelleComponent } from './components/projektabrechnung-auslagen-form-tabelle/projektabrechnung-auslagen-form-tabelle.component';
import { ProjektabrechnungBearbeitenPabAuswahlFormComponent } from './components/projektabrechnung-bearbeiten-pab-auswahl-form/projektabrechnung-bearbeiten-pab-auswahl-form.component';
import { ProjektabrechnungBearbeitenTabReiseComponent } from './components/projektabrechnung-bearbeiten-tab-reise/projektabrechnung-bearbeiten-tab-reise.component';
import { ProjektabrechnungBearbeitenTabProjektzeitComponent } from './components/projektabrechnung-bearbeiten-tab-projektzeit/projektabrechnung-bearbeiten-tab-projektzeit.component';
import { ProjektabrechnungBearbeitenTabSonstigeComponent } from './components/projektabrechnung-bearbeiten-tab-sonstige/projektabrechnung-bearbeiten-tab-sonstige.component';
import { ProjektabrechnungBearbeitenTabSonderarbeitComponent } from './components/projektabrechnung-bearbeiten-tab-sonderarbeit/projektabrechnung-bearbeiten-tab-sonderarbeit.component';
import { ProjektabrechnungKorrekturbuchungComponent } from './pages/projektabrechnung-korrekturbuchung/projektabrechnung-korrekturbuchung.component';
import { ProjektabrechnungKorrekturbuchungActionBarComponent } from './components/projektabrechnung-korrekturbuchung-action-bar/projektabrechnung-korrekturbuchung-action-bar.component';
import { ProjektabrechnungKorrekturbuchungFilterComponent } from './components/projektabrechnung-korrekturbuchung-filter/projektabrechnung-korrekturbuchung-filter.component';
import { ProjektabrechnungKorrekturbuchungFormComponent } from './components/projektabrechnung-korrekturbuchung-form/projektabrechnung-korrekturbuchung-form.component';
import { ProjektabrechnungKorrekturbuchungTabelleComponent } from './components/projektabrechnung-korrekturbuchung-tabelle/projektabrechnung-korrekturbuchung-tabelle.component';
import { PipesModule } from '../../shared/pipes.module';
import { ProjektabrechnungBearbeitenTabReiseSpesenZuschlaegeDialogComponent } from './components/projektabrechnung-bearbeiten-tab-reise-spesen-zuschlaege-dialog/projektabrechnung-bearbeiten-tab-reise-spesen-zuschlaege-dialog.component';
import { ProjektabrechnungBearbeitenTabReiseBelegeDialogComponent } from './components/projektabrechnung-bearbeiten-tab-reise-belege-dialog/projektabrechnung-bearbeiten-tab-reise-belege-dialog.component';
import { ProjektabrechnungBearbeitenTabAuslagenDialogComponent } from './components/projektabrechnung-bearbeiten-tab-auslagen-dialog/projektabrechnung-bearbeiten-tab-auslagen-dialog.component';
import { ProjektabrechnungBearbeitenTabFakturfaehigeLeistungComponent } from './components/projektabrechnung-bearbeiten-tab-fakturfaehige-leistung/projektabrechnung-bearbeiten-tab-fakturfaehige-leistung.component';
import { ProjektabrechnungBearbeitenFertigstellungDialogComponent } from './components/projektabrechnung-bearbeiten-fertigstellung-dialog/projektabrechnung-bearbeiten-fertigstellung-dialog.component';

const routes: Routes = [
  {
    path: '',
    component: ProjektabrechnungUebersichtComponent,
  },
  {
    path: 'bearbeiten',
    component: ProjektabrechnungBearbeitenComponent,
  },
  {
    path: 'auslagen',
    component: ProjektabrechnungAuslagenComponent,
  },
  {
    path: 'korrekturbuchung',
    component: ProjektabrechnungKorrekturbuchungComponent,
  },
];

@NgModule({
  declarations: [
    ProjektabrechnungUebersichtComponent,
    ProjektabrechnungUebersichtActionBarComponent,
    ProjektabrechnungUebersichtFilterComponent,
    ProjektabrechnungUebersichtTabelleComponent,
    ProjektabrechnungBearbeitenComponent,
    ProjektabrechnungBearbeitenActionBarComponent,
    ProjektabrechnungBearbeitenPabAuswahlFormComponent,
    ProjektabrechnungBearbeitenFertigstellungDialogComponent,
    ProjektabrechnungBearbeitenTabProjektzeitComponent,
    ProjektabrechnungBearbeitenTabReiseComponent,
    ProjektabrechnungBearbeitenTabAuslagenDialogComponent,
    ProjektabrechnungBearbeitenTabFakturfaehigeLeistungComponent,
    ProjektabrechnungBearbeitenTabReiseBelegeDialogComponent,
    ProjektabrechnungBearbeitenTabReiseSpesenZuschlaegeDialogComponent,
    ProjektabrechnungBearbeitenTabSonderarbeitComponent,
    ProjektabrechnungBearbeitenTabSonstigeComponent,
    ProjektabrechnungAuslagenComponent,
    ProjektabrechnungAuslagenActionBarComponent,
    ProjektabrechnungAuslagenFilterComponent,
    ProjektabrechnungAuslagenFormTabelleComponent,
    ProjektabrechnungKorrekturbuchungComponent,
    ProjektabrechnungKorrekturbuchungActionBarComponent,
    ProjektabrechnungKorrekturbuchungFilterComponent,
    ProjektabrechnungKorrekturbuchungFormComponent,
    ProjektabrechnungKorrekturbuchungTabelleComponent,
  ],
  imports: [CommonModule, SharedModule, MaterialModule, PipesModule, RouterModule.forChild(routes)],
})
export class ProjektabrechnungModule {}
