import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ArbeitsnachweisUebersicht } from '../../../../shared/model/arbeitsnachweis/arbeitsnachweis-uebersicht';
import { LegendPosition } from '@swimlane/ngx-charts';
import { BearbeitungsstatusEnumZuStatusIdPipe } from '../../../../shared/pipe/bearbeitungsstatus-enum-zu-status-id.pipe';
import { BearbeitungsstatusEnum } from '../../../../shared/enum/bearbeitungsstatus.enum';
import { Constants } from '../../../../shared/util/constants.util';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { KonstantenService } from '../../../../shared/service/konstanten.service';
import { ProjektabrechnungUebersicht } from '../../../../shared/model/projektabrechnung/projektabrechnung-uebersicht';
import { Projekt } from '../../../../shared/model/projekt/projekt';

@Component({
  selector: 'pab-dashboard-uebersicht',
  templateUrl: './dashboard-uebersicht.component.html',
  styleUrls: ['./dashboard-uebersicht.component.scss'],
})
export class DashboardUebersichtComponent implements OnChanges {
  private nameAbgeschlossen = 'abgeschlossen';
  private nameAbgerechnet = 'abgerechnet';
  private nameErfasst = 'erfasst';
  private nameOffen = 'offen';
  private nameNichtBebucht = 'nicht bebucht';

  @Input()
  mitarbeiters: Mitarbeiter[] = [];

  @Input()
  projekte: Projekt[] = [];

  @Input()
  arbeitsnachweise: ArbeitsnachweisUebersicht[] = [];

  @Input()
  projektabrechnungen: ProjektabrechnungUebersicht[] = [];

  // Optionen
  ansicht: [number, number] = [425, 225];
  zeigeLegaendeAn: boolean = true;
  legendePosition: LegendPosition = LegendPosition.Below;
  legendeTitel: string = '';
  zeigeAlsDoughnutAn: boolean = true;
  farbenArbeitsnachweise = [
    { name: this.nameAbgeschlossen, value: Constants.FARBE_GRUEN },
    { name: this.nameAbgerechnet, value: Constants.FARBE_LILA },
    { name: this.nameErfasst, value: Constants.FARBE_GELB },
    { name: this.nameOffen, value: Constants.FARBE_ROT },
  ];
  farbenProjekte = [
    { name: this.nameAbgeschlossen, value: Constants.FARBE_GRUEN },
    { name: this.nameAbgerechnet, value: Constants.FARBE_LILA },
    { name: this.nameErfasst, value: Constants.FARBE_GELB },
    { name: this.nameNichtBebucht, value: Constants.FARBE_ROT },
  ];

  datenIntern: any[] = [];
  datenExtern: any[] = [];
  datenProjekte: any[] = [];

  constructor(
    private konstantenService: KonstantenService,
    private bearbeitungsstatusEnumZuStatusIdPipe: BearbeitungsstatusEnumZuStatusIdPipe
  ) {}

  ngOnChanges(changes: SimpleChanges) {
    if (changes['arbeitsnachweise']) {
      const arbeitsnachweise = changes['arbeitsnachweise'];
      if (
        arbeitsnachweise.currentValue &&
        arbeitsnachweise.previousValue &&
        arbeitsnachweise.currentValue.length !== arbeitsnachweise.previousValue.length
      ) {
        this.aktualisiereDiagrammDatenFuerArbeitsnachweise();
      }
    }

    if (changes['projektabrechnungen']) {
      const projektabrechnungen = changes['projektabrechnungen'];
      if (
        projektabrechnungen.currentValue &&
        projektabrechnungen.previousValue &&
        projektabrechnungen.currentValue.length !== projektabrechnungen.previousValue.length
      ) {
        this.aktualisiereDiagrammDatenFuerProjektabrechnungen();
      }
    }
  }

  private aktualisiereDiagrammDatenFuerProjektabrechnungen() {
    const statusIdAbgeschlossen: number = this.bearbeitungsstatusEnumZuStatusIdPipe.transform(
      BearbeitungsstatusEnum.ABGESCHLOSSEN
    );
    const statusIdAbgerechnet: number = this.bearbeitungsstatusEnumZuStatusIdPipe.transform(
      BearbeitungsstatusEnum.ABGERECHNET
    );
    const statusIdErfasst: number = this.bearbeitungsstatusEnumZuStatusIdPipe.transform(BearbeitungsstatusEnum.ERFASST);

    const anzahlAktiverProjekte: number = this.projektabrechnungen.filter((element) => {
      const projekt: Projekt = getObjektAusListeDurchId(element.projektId, this.projekte) as Projekt;
      return projekt.istAktiv;
    }).length;
    const anzahlInaktiverBebuchterProjekte: number = this.projektabrechnungen.filter((element) => {
      const projekt: Projekt = getObjektAusListeDurchId(element.projektId, this.projekte) as Projekt;
      return !projekt.istAktiv && element.anzahlMitarbeiter > 0;
    }).length;

    const anzahlProjekteAbgeschlossen: number = this.projektabrechnungen.filter(
      (element) => element.statusId === statusIdAbgeschlossen
    ).length;
    const anzahlProjekteAbgerechnet: number = this.projektabrechnungen.filter(
      (element) => element.statusId === statusIdAbgerechnet
    ).length;
    const anzahlProjekteErfasst: number = this.projektabrechnungen.filter(
      (element) => element.statusId === statusIdErfasst
    ).length;

    this.datenProjekte = [
      {
        name: this.nameAbgeschlossen,
        value: anzahlProjekteAbgeschlossen,
      },
      {
        name: this.nameAbgerechnet,
        value: anzahlProjekteAbgerechnet,
      },
      {
        name: this.nameErfasst,
        value: anzahlProjekteErfasst,
      },
      {
        name: this.nameNichtBebucht,
        value:
          anzahlAktiverProjekte +
          anzahlInaktiverBebuchterProjekte -
          anzahlProjekteAbgerechnet -
          anzahlProjekteErfasst -
          anzahlProjekteAbgeschlossen,
      },
    ];
  }

  private aktualisiereDiagrammDatenFuerArbeitsnachweise() {
    const arbeitsnachweiseInterne: ArbeitsnachweisUebersicht[] = [];
    const arbeitsnachweiseExterne: ArbeitsnachweisUebersicht[] = [];

    this.arbeitsnachweise.forEach((arbeitsnachweis) => {
      const mitarbeiter: Mitarbeiter = getObjektAusListeDurchId(
        arbeitsnachweis.mitarbeiterId,
        this.mitarbeiters
      ) as Mitarbeiter;
      if (
        mitarbeiter.intern &&
        mitarbeiter.mitarbeiterTypId === this.konstantenService.getMitarbeiterTypAngestellter().id
      ) {
        arbeitsnachweiseInterne.push(arbeitsnachweis);
      }
      if (!mitarbeiter.intern) {
        arbeitsnachweiseExterne.push(arbeitsnachweis);
      }
    });

    this.datenIntern = this.erzeugeDatenFuerArbeitsnachweise(arbeitsnachweiseInterne);
    this.datenExtern = this.erzeugeDatenFuerArbeitsnachweise(arbeitsnachweiseExterne);
  }

  private erzeugeDatenFuerArbeitsnachweise(arbeitsnachweise: ArbeitsnachweisUebersicht[]): any[] {
    const statusIdAbgeschlossen: number = this.bearbeitungsstatusEnumZuStatusIdPipe.transform(
      BearbeitungsstatusEnum.ABGESCHLOSSEN
    );
    const statusIdAbgerechnet: number = this.bearbeitungsstatusEnumZuStatusIdPipe.transform(
      BearbeitungsstatusEnum.ABGERECHNET
    );
    const statusIdErfasst: number = this.bearbeitungsstatusEnumZuStatusIdPipe.transform(BearbeitungsstatusEnum.ERFASST);
    return [
      {
        name: this.nameAbgeschlossen,
        value: arbeitsnachweise.filter((element) => element.statusId === statusIdAbgeschlossen).length,
      },
      {
        name: this.nameAbgerechnet,
        value: arbeitsnachweise.filter((element) => element.statusId === statusIdAbgerechnet).length,
      },
      {
        name: this.nameErfasst,
        value: arbeitsnachweise.filter((element) => element.statusId === statusIdErfasst).length,
      },
      {
        name: this.nameOffen,
        value: arbeitsnachweise.filter((element) => !element.arbeitsnachweisId).length,
      },
    ];
  }
}
