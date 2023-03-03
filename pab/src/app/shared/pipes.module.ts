import { NgModule } from '@angular/core';
import { AbrechnungsmonatAnzeigeNamePipe } from './pipe/abrechnungsmonat-anzeige-name.pipe';
import { BearbeitungsstatusEnumZuStatusIdPipe } from './pipe/bearbeitungsstatus-enum-zu-status-id.pipe';
import { MitarbeiterAnzeigeNamePipe } from './pipe/mitarbeiter-anzeige-name.pipe';
import { MitarbeiterAnzeigeNameKurzformPipe } from './pipe/mitarbeiter-anzeige-name-kurzform.pipe';
import { MitarbeiterToObsPipe } from './pipe/mitarbeiter-to-obs.pipe';
import { ProjekteAnzeigeNamePipe } from './pipe/projekte-anzeige-name.pipe';
import { KundeAnzeigeNamePipe } from './pipe/kunde-anzeige-name.pipe';
import { OrganisationseinheitAnzeigeNamePipe } from './pipe/organisationseinheit-anzeige-name.pipe';
import { StatusIdZuBearbeitungsstatusEnumPipe } from './pipe/status-id-zu-bearbeitungsstatus-enum.pipe';
import { BuchungstypIdStundenkontoZuTextPipe } from './pipe/buchungstyp-id-stundenkonto-zu-text.pipe';
import { ArbeitsnachweisBearbeitenUeberschriftPipe } from './pipe/arbeitsnachweis-bearbeiten-ueberschrift.pipe';
import { IsInternToTextPipe } from './pipe/is-intern-to-text.pipe';
import { SummiereBelegeBetraegePipe } from './pipe/summiere-belege-betraege.pipe';
import { TimeToTextPipe } from './pipe/time-to-text.pipe';
import { UsNummerZuDeStringPipe } from './pipe/us-nummer-zu-de-string.pipe';
import { DatePipe, DecimalPipe } from '@angular/common';
import { BearbeitungsstatusEnumZuFortschrittPipe } from './pipe/bearbeitungsstatus-enum-zu-fortschritt.pipe';
import { ProjektabrechnungBearbeitenUeberschriftPipe } from './pipe/projektabrechnung-bearbeiten-ueberschrift.pipe';
import { ProjaktabrechnungKostenLeistungAnzeigeNamePipe } from './pipe/projaktabrechnung-kosten-leistung-anzeige-name.pipe';
import { KostenartAnzeigeNamePipe } from './pipe/kostenart-anzeige-name.pipe';
import { SummiereAbwesenheitenZuschlaegePipe } from './pipe/summiere-abwesenheiten-zuschlaege.pipe';
import { SummiereProjektNichtFakturierStundenPipe } from './pipe/summiere-projekt-nicht-fakturier-stunden.pipe';
import { SummiereProjektStundenPipe } from './pipe/summiere-projekt-stunden.pipe';
import { SummiereAbwesenheitenSpesenPipe } from './pipe/summiere-abwesenheiten-spesen.pipe';
import { SummiereProjektStundenMitAngReisezeitPipe } from './pipe/summiere-projekt-stunden-mit-ang-reisezeit.pipe';
import { SummiereSonstigeProjektkostenKostenPipe } from './pipe/summiere-sonstige-projektkosten-kosten.pipe';
import { ErgaenzeProzentZeichenPipe } from './pipe/ergaenze-prozent-zeichen.pipe';
import { AnzeigeGeldbetraegeEuroPipe } from './pipe/geldbetrag-anzeige-euro.pipe';

const pipes = [
  AbrechnungsmonatAnzeigeNamePipe,
  ArbeitsnachweisBearbeitenUeberschriftPipe,
  BearbeitungsstatusEnumZuFortschrittPipe,
  BearbeitungsstatusEnumZuStatusIdPipe,
  BuchungstypIdStundenkontoZuTextPipe,
  ErgaenzeProzentZeichenPipe,
  IsInternToTextPipe,
  KundeAnzeigeNamePipe,
  KostenartAnzeigeNamePipe,
  MitarbeiterAnzeigeNamePipe,
  MitarbeiterAnzeigeNameKurzformPipe,
  MitarbeiterToObsPipe,
  ProjektabrechnungBearbeitenUeberschriftPipe,
  ProjaktabrechnungKostenLeistungAnzeigeNamePipe,
  ProjekteAnzeigeNamePipe,
  OrganisationseinheitAnzeigeNamePipe,
  StatusIdZuBearbeitungsstatusEnumPipe,
  SummiereAbwesenheitenZuschlaegePipe,
  SummiereBelegeBetraegePipe,
  SummiereAbwesenheitenSpesenPipe,
  SummiereProjektNichtFakturierStundenPipe,
  SummiereProjektStundenPipe,
  SummiereProjektStundenMitAngReisezeitPipe,
  SummiereSonstigeProjektkostenKostenPipe,
  TimeToTextPipe,
  UsNummerZuDeStringPipe,
  AnzeigeGeldbetraegeEuroPipe,
];

@NgModule({
  declarations: [pipes],
  providers: [pipes, DatePipe, DecimalPipe],
  exports: [pipes],
})
export class PipesModule {}
