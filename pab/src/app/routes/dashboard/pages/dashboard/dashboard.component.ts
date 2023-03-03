import { Component, OnInit } from '@angular/core';
import { ArbeitsnachweisService } from '../../../../shared/service/arbeitsnachweis.service';
import { MitarbeiterService } from '../../../../shared/service/mitarbeiter.service';
import { forkJoin } from 'rxjs';
import { ArbeitsnachweisUebersicht } from '../../../../shared/model/arbeitsnachweis/arbeitsnachweis-uebersicht';
import { BenutzerService } from '../../../../shared/service/benutzer.service';
import { Abrechnungsmonat } from '../../../../shared/model/sonstiges/abrechnungsmonat';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { ProjektabrechnungService } from '../../../../shared/service/projektabrechnung.service';
import { ProjektabrechnungUebersicht } from '../../../../shared/model/projektabrechnung/projektabrechnung-uebersicht';
import { ProjektService } from '../../../../shared/service/projekt.service';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { AbrechnungsmonatService } from '../../../../shared/service/abrechnungsmonat.service';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';

@Component({
  selector: 'pab-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit {
  zeigeUebersichtOeLeiter: boolean = false;
  aktuellerAbrechnungsmonat!: Abrechnungsmonat;

  mitarbeiters: Mitarbeiter[] = [];
  projekte: Projekt[] = [];
  arbeitsnachweise: ArbeitsnachweisUebersicht[] = [];
  projektabrechnungen: ProjektabrechnungUebersicht[] = [];

  ladeDashboardSpinner: SpinnerRef = new SpinnerRef();

  constructor(
    private arbeitsnachweisService: ArbeitsnachweisService,
    private abrechnungsmonatService: AbrechnungsmonatService,
    private projektabrechnungService: ProjektabrechnungService,
    private mitarbeiterService: MitarbeiterService,
    private projektService: ProjektService,
    private benutzerService: BenutzerService
  ) {}

  ngOnInit(): void {
    this.aktuellerAbrechnungsmonat = this.abrechnungsmonatService.getAktuellenAbrechnungsmonat();

    this.zeigeUebersichtOeLeiter =
      this.benutzerService.hatRolleOeLeiter() &&
      !this.benutzerService.hatRolleGF() &&
      !this.benutzerService.hatRolleSachbearbeiter() &&
      !this.benutzerService.hatRolleAdministrator();

    if (this.zeigeUebersichtOeLeiter) {
      // TODO Daten abrufen
    } else {
      this.aktualisiereDatenArbeitsnachweise();
    }
  }

  private aktualisiereDatenArbeitsnachweise() {
    const abrechungsmonat: Date = this.abrechnungsmonatService.getAktuellenAbrechnungsmonatAsDate();
    this.ladeDashboardSpinner.open();

    forkJoin([
      this.mitarbeiterService.getAlleMitarbeiter(),
      this.projektService.getAlleProjekte(),
      this.arbeitsnachweisService.getArbeitsnachweiseUebersicht(abrechungsmonat, abrechungsmonat),
      this.arbeitsnachweisService.getFehlendeArbeitsnachweise(abrechungsmonat, abrechungsmonat),
      this.projektabrechnungService.getProjektabrechnungenUebersicht(abrechungsmonat, abrechungsmonat),
      this.projektabrechnungService.getUebersichtZuAlleProjekteFuerDieAbrechnungImAbrechnungsmonatFehlt(
        abrechungsmonat,
        abrechungsmonat
      ),
    ]).subscribe(
      ([
        mitarbeiters,
        projekte,
        arbeitsnachweise,
        fehlendeArbeitsnachweise,
        projektabrechnungen,
        fehlendeProjektabrechnungen,
      ]) => {
        this.mitarbeiters = mitarbeiters;
        this.projekte = projekte;
        this.arbeitsnachweise = arbeitsnachweise.concat(fehlendeArbeitsnachweise);
        this.projektabrechnungen = projektabrechnungen.concat(fehlendeProjektabrechnungen);
        this.ladeDashboardSpinner.close();
      }
    );
  }
}
