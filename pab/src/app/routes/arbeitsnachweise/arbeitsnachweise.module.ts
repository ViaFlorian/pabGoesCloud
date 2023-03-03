import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ArbeitsnachweisUebersichtComponent } from './pages/arbeitsnachweis-uebersicht/arbeitsnachweis-uebersicht.component';
import { SharedModule } from '../../shared/shared.module';
import { MaterialModule } from '../../shared/material/material.module';
import { RouterModule, Routes } from '@angular/router';
import { ArbeitsnachweisUebersichtTabelleComponent } from './components/arbeitsnachweis-uebersicht-tabelle/arbeitsnachweis-uebersicht-tabelle.component';
import { ArbeitsnachweisBearbeitenComponent } from './pages/arbeitsnachweis-bearbeiten/arbeitsnachweis-bearbeiten.component';
import { ArbeitsnachweisUebersichtActionBarComponent } from './components/arbeitsnachweis-uebersicht-action-bar/arbeitsnachweis-uebersicht-action-bar.component';
import { ArbeitsnachweisUebersichtFilterComponent } from './components/arbeitsnachweis-uebersicht-filter/arbeitsnachweis-uebersicht-filter.component';
import { ArbeitsnachweisBearbeitenActionBarComponent } from './components/arbeitsnachweis-bearbeiten-action-bar/arbeitsnachweis-bearbeiten-action-bar.component';
import { ArbeitsnachweisBearbeitenTabProjektstundenComponent } from './components/arbeitsnachweis-bearbeiten-tab-projektstunden/arbeitsnachweis-bearbeiten-tab-projektstunden.component';
import { ArbeitsnachweisBearbeitenAnwAuswahlFormComponent } from './components/arbeitsnachweis-bearbeiten-anw-auswahl-form/arbeitsnachweis-bearbeiten-anw-auswahl-form.component';
import { ArbeitsnachweisBearbeitenTabBelegeComponent } from './components/arbeitsnachweis-bearbeiten-tab-belege/arbeitsnachweis-bearbeiten-tab-belege.component';
import { ArbeitsnachweisBearbeitenTabAbwesenheitenComponent } from './components/arbeitsnachweis-bearbeiten-tab-abwesenheiten/arbeitsnachweis-bearbeiten-tab-abwesenheiten.component';
import { ArbeitsnachweisBearbeitenTabSonderzeitenComponent } from './components/arbeitsnachweis-bearbeiten-tab-sonderzeiten/arbeitsnachweis-bearbeiten-tab-sonderzeiten.component';
import { ArbeitsnachweisBearbeitenTabAbrechnungComponent } from './components/arbeitsnachweis-bearbeiten-tab-abrechnung/arbeitsnachweis-bearbeiten-tab-abrechnung.component';
import { ArbeitsnachweisBearbeitenTabAbrechnungDialogComponent } from './components/arbeitsnachweis-bearbeiten-tab-abrechnung-dialog/arbeitsnachweis-bearbeiten-tab-abrechnung-dialog.component';
import { ArbeitsnachweisBearbeitenTabAbrechnungLohnartenzuordnungDialogComponent } from './components/arbeitsnachweis-bearbeiten-tab-abrechnung-lohnartenzuordnung-dialog/arbeitsnachweis-bearbeiten-tab-abrechnung-lohnartenzuordnung-dialog.component';
import { ArbeitsnachweisBearbeitenTabAbrechnungLohnartenberechnungLogDialogComponent } from './components/arbeitsnachweis-bearbeiten-tab-abrechnung-lohnartenberechnung-log-dialog/arbeitsnachweis-bearbeiten-tab-abrechnung-lohnartenberechnung-log-dialog.component';
import { ArbeitsnachweisBearbeitenTabAbwesenheitenDreimonatsregelDialogComponent } from './components/arbeitsnachweis-bearbeiten-tab-abwesenheiten-dreimonatsregel-dialog/arbeitsnachweis-bearbeiten-tab-abwesenheiten-dreimonatsregel-dialog.component';
import { ArbeitsnachweisBearbeitenAnwImportDialogComponent } from './components/arbeitsnachweis-bearbeiten-anw-import-dialog/arbeitsnachweis-bearbeiten-anw-import-dialog.component';
import { ArbeitsnachweisBearbeitenUploadFehlerlogDialogComponent } from './components/arbeitsnachweis-bearbeiten-upload-fehlerlog-dialog/arbeitsnachweis-bearbeiten-upload-fehlerlog-dialog.component';
import { PipesModule } from '../../shared/pipes.module';
import { ArbeitsnachweisBearbeitenBelegeImportDialogComponent } from './components/arbeitsnachweis-bearbeiten-belege-import-dialog/arbeitsnachweis-bearbeiten-belege-import-dialog.component';

const routes: Routes = [
  {
    path: '',
    component: ArbeitsnachweisUebersichtComponent,
  },
  {
    path: 'bearbeiten',
    component: ArbeitsnachweisBearbeitenComponent,
  },
];

@NgModule({
  declarations: [
    ArbeitsnachweisUebersichtComponent,
    ArbeitsnachweisUebersichtActionBarComponent,
    ArbeitsnachweisUebersichtFilterComponent,
    ArbeitsnachweisUebersichtTabelleComponent,
    ArbeitsnachweisBearbeitenComponent,
    ArbeitsnachweisBearbeitenActionBarComponent,
    ArbeitsnachweisBearbeitenAnwAuswahlFormComponent,
    ArbeitsnachweisBearbeitenTabProjektstundenComponent,
    ArbeitsnachweisBearbeitenTabBelegeComponent,
    ArbeitsnachweisBearbeitenTabAbwesenheitenComponent,
    ArbeitsnachweisBearbeitenTabSonderzeitenComponent,
    ArbeitsnachweisBearbeitenTabAbrechnungComponent,
    ArbeitsnachweisBearbeitenTabAbrechnungDialogComponent,
    ArbeitsnachweisBearbeitenTabAbrechnungLohnartenzuordnungDialogComponent,
    ArbeitsnachweisBearbeitenTabAbrechnungLohnartenberechnungLogDialogComponent,
    ArbeitsnachweisBearbeitenTabAbwesenheitenDreimonatsregelDialogComponent,
    ArbeitsnachweisBearbeitenAnwImportDialogComponent,
    ArbeitsnachweisBearbeitenBelegeImportDialogComponent,
    ArbeitsnachweisBearbeitenUploadFehlerlogDialogComponent,
  ],
  providers: [],
  imports: [CommonModule, SharedModule, MaterialModule, PipesModule, RouterModule.forChild(routes)],
})
export class ArbeitsnachweiseModule {}
