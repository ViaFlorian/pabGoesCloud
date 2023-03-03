import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { Abrechnungsmonat } from '../../../../shared/model/sonstiges/abrechnungsmonat';
import { FormBuilder, FormGroup } from '@angular/forms';
import { getAbgeschlossenIcon } from 'src/app/shared/util/icon.util';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { MonatsabschlussAktionTabellendarstellung } from '../../model/monatsabschluss-aktion-tabellendarstellung';
import { NichtAbgeschlosseneMitarbeiterTabellendarstellung } from '../../model/nicht-abgeschlossene-mitarbeiter-tabellendarstellung';
import { forkJoin } from 'rxjs';
import { ProjektService } from '../../../../shared/service/projekt.service';
import { MitarbeiterService } from '../../../../shared/service/mitarbeiter.service';
import { ArbeitsnachweisService } from '../../../../shared/service/arbeitsnachweis.service';
import { AbrechnungsmonatService } from '../../../../shared/service/abrechnungsmonat.service';
import { SpinnerOverlayService } from '../../../../shared/service/spinner-overlay.service';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import * as CompareUtil from '../../../../shared/util/compare.util';
import { VerwaltungService } from '../../../../shared/service/verwaltung.service';
import { MonatsabschlussAktion } from '../../../../shared/model/verwaltung/monatsabschluss-aktion';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { MitarbeiterAnzeigeNamePipe } from '../../../../shared/pipe/mitarbeiter-anzeige-name.pipe';
import { MitarbeiterNichtBereitFuerMonatsabschluss } from '../../../../shared/model/verwaltung/mitarbeiter-nicht-bereit-fuer-monatsabschluss';
import { Arbeitsnachweis } from '../../../../shared/model/arbeitsnachweis/arbeitsnachweis';
import { IsInternToTextPipe } from '../../../../shared/pipe/is-intern-to-text.pipe';
import { StatusIdZuBearbeitungsstatusEnumPipe } from '../../../../shared/pipe/status-id-zu-bearbeitungsstatus-enum.pipe';
import { Projektabrechnung } from '../../../../shared/model/projektabrechnung/projektabrechnung';
import { NichtAbgeschlosseneProjekteTabellendarstellung } from '../../model/nicht-abgeschlossene-projekte-tabellendarstellung';
import { BearbeitungsstatusEnum } from '../../../../shared/enum/bearbeitungsstatus.enum';

@Component({
  selector: 'pab-monatsabschluss',
  templateUrl: './monatsabschluss.component.html',
  styleUrls: ['./monatsabschluss.component.scss'],
})
export class MonatsabschlussComponent implements OnInit, AfterViewInit {
  ueberschrift = 'Monatsabschluss';
  ueberschriftMitarbeiterTabelle = 'Noch nicht abgerechnete Mitarbeiter';
  ueberschriftProjekteTabelle = 'Noch nicht abgerechnete Projekte';

  monatsabschlussFormGroup: FormGroup;

  projektAuswahl: Projekt[] = [];
  mitarbeiterAuswahl: Mitarbeiter[] = [];
  sacharbeiterAuswahl: Mitarbeiter[] = [];
  abrechnungsmonateAuswahl: Abrechnungsmonat[] = [];
  arbeitsnachweisAuswahl: Arbeitsnachweis[] = [];

  monatsabschlussAktionenTabelle: TableData<MonatsabschlussAktionTabellendarstellung>;
  monatsabschlussAktionenSpalten = [
    { id: 'aktion', name: 'Aktion' },
    {
      id: 'mitarbeiter',
      name: 'Mitarbeiter/in',
    },
    { id: 'datum', name: 'Datum' },
  ];

  @ViewChild(MatMultiSort)
  mitarbeiterSort!: MatMultiSort;
  nichtAbgeschlosseneMitarbeiterTabelle: TableData<NichtAbgeschlosseneMitarbeiterTabellendarstellung>;
  nichtAbgeschlosseneMitarbeiterSpalten = [
    { id: 'aktion', name: '' },
    {
      id: 'mitarbeiter',
      name: 'Mitarbeiter/in',
    },
    { id: 'internExtern', name: 'Intern/Extern' },
    {
      id: 'sachbearbeiter',
      name: 'Sachbearbeiter/in',
    },
    {
      id: 'bearbeitungsstatus',
      name: 'Bearbeitungsstatus',
    },
  ];
  nichtAbgeschlosseneMitarbeiterTabellendarstellung: NichtAbgeschlosseneMitarbeiterTabellendarstellung[] = [];

  @ViewChild(MatMultiSort)
  projekteSort!: MatMultiSort;
  nichtAbgeschlosseneProjekteTabelle: TableData<NichtAbgeschlosseneProjekteTabellendarstellung>;
  nichtAbgeschlosseneProjekteSpalten = [
    { id: 'aktion', name: '' },
    {
      id: 'projektnummer',
      name: 'Projektnummer',
    },
    { id: 'projektbezeichnung', name: 'Projektbezeichnung' },
    {
      id: 'sachbearbeiter',
      name: 'Sachbearbeiter/in',
    },
    {
      id: 'bearbeitungsstatus',
      name: 'Bearbeitungsstatus',
    },
  ];
  nichtAbgeschlosseneProjekteTabellendarstellung: NichtAbgeschlosseneProjekteTabellendarstellung[] = [];

  constructor(
    private fb: FormBuilder,
    private projektService: ProjektService,
    private mitarbeiterService: MitarbeiterService,
    private arbeitsnachweisService: ArbeitsnachweisService,
    private abrechnungsmonatService: AbrechnungsmonatService,
    private verwaltungService: VerwaltungService,
    private spinnerOverlayService: SpinnerOverlayService,
    private mitarbeiterAnzeigeNamePipe: MitarbeiterAnzeigeNamePipe,
    private isInternToTextPipe: IsInternToTextPipe,
    private statusIdZuBearbeitungsstatusEnumPipe: StatusIdZuBearbeitungsstatusEnumPipe
  ) {
    this.monatsabschlussFormGroup = this.fb.nonNullable.group({
      abrechnungsmonat: [{} as Abrechnungsmonat],
    });

    this.monatsabschlussAktionenTabelle = new TableData<MonatsabschlussAktionTabellendarstellung>(
      this.monatsabschlussAktionenSpalten
    );
    this.monatsabschlussAktionenTabelle.dataSource =
      new MatMultiSortTableDataSource<MonatsabschlussAktionTabellendarstellung>(new MatMultiSort(), true);
    this.nichtAbgeschlosseneMitarbeiterTabelle = new TableData<NichtAbgeschlosseneMitarbeiterTabellendarstellung>(
      this.nichtAbgeschlosseneMitarbeiterSpalten
    );
    this.nichtAbgeschlosseneMitarbeiterTabelle.dataSource =
      new MatMultiSortTableDataSource<NichtAbgeschlosseneMitarbeiterTabellendarstellung>(new MatMultiSort(), true);
    this.nichtAbgeschlosseneProjekteTabelle = new TableData<NichtAbgeschlosseneProjekteTabellendarstellung>(
      this.nichtAbgeschlosseneProjekteSpalten
    );
    this.nichtAbgeschlosseneProjekteTabelle.dataSource =
      new MatMultiSortTableDataSource<NichtAbgeschlosseneProjekteTabellendarstellung>(new MatMultiSort(), true);
  }

  ngOnInit(): void {
    this.rufeInitialeDatenAb();
    this.reagiereAufAenderungenAbrechnungsmonat();
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenDaten();
    this.initialisiereTabellenSortierung();
  }

  sindAbrechnungsmonateGleich(abrechnungsmonat1: Abrechnungsmonat, abrechnungsmonat2: Abrechnungsmonat): boolean {
    return CompareUtil.sindAbrechnungsmonateGleich(abrechnungsmonat1, abrechnungsmonat2);
  }

  getAbgeschlossenIcon(abrechnungsmonat: Abrechnungsmonat): string {
    return getAbgeschlossenIcon(abrechnungsmonat);
  }

  reagiereAufAnzahlArbeitsnachweiseEvent(anzahlArbeitsnachweise: number) {
    this.ueberschrift = `Übersicht der Arbeitsnachweise [${anzahlArbeitsnachweise}]`;
  }

  private rufeInitialeDatenAb(): void {
    const data = {
      title: 'Daten werden geladen...',
    };
    const spinnerOverlay = this.spinnerOverlayService.open(data);
    forkJoin([
      this.projektService.getAlleProjekte(),
      this.mitarbeiterService.getMitarbeiterAuswahlFuerArbeitsnachweisBearbeiten(),
      this.mitarbeiterService.getAlleSachbearbeiter(),
      this.arbeitsnachweisService.getAlleAbrechnungsmonate(),
      this.arbeitsnachweisService.getAlleArbeitsnachweise(),
    ]).subscribe(([projekte, mitarbeiter, sacharbeiter, abrechnungsmonate, arbeitsnachweise]) => {
      this.projektAuswahl = projekte.slice();
      this.mitarbeiterAuswahl = mitarbeiter.slice();
      this.sacharbeiterAuswahl = sacharbeiter.slice();
      this.abrechnungsmonatService.sortiereAbrechnungsmonate(abrechnungsmonate);
      this.abrechnungsmonateAuswahl = abrechnungsmonate;
      this.arbeitsnachweisAuswahl = arbeitsnachweise;
      this.setzeAktuellenAbrechnungsmonat();
      spinnerOverlay.close();
    });
  }

  private reagiereAufAenderungenAbrechnungsmonat() {
    this.monatsabschlussFormGroup
      .get('abrechnungsmonat')!
      .valueChanges.subscribe((abrechnungsmonat: Abrechnungsmonat) => {
        const data = {
          title: 'Daten werden geladen...',
        };
        const spinnerOverlay = this.spinnerOverlayService.open(data);
        forkJoin([
          this.verwaltungService.getMonatsabschlussAktion(abrechnungsmonat),
          this.verwaltungService.getMitarbeiterNichtBereitFuerMonatsabschluss(abrechnungsmonat),
          this.verwaltungService.getProjektNichtBereitFuerMonatsabschluss(abrechnungsmonat),
        ]).subscribe(
          ([
            monatsabschlussAktions,
            mitarbeiterNichtBereitFuerMonatsabschluss,
            projektNichtBereitFuerMonatsabschluss,
          ]) => {
            this.aktualisiereMonatsabschlussAktionTabellendarstellung(monatsabschlussAktions);
            this.aktualisiereNichtAbgeschlosseneMitarbeiterTabelleTabellendarstellung(
              mitarbeiterNichtBereitFuerMonatsabschluss
            );
            this.aktualisiereNichtAbgeschlosseneProjektTabelleTabellendarstellung(
              projektNichtBereitFuerMonatsabschluss
            );
            this.ueberschriftMitarbeiterTabelle =
              'Noch nicht abgerechnete Mitarbeiter ' + `[${mitarbeiterNichtBereitFuerMonatsabschluss.length}]`;
            this.ueberschriftProjekteTabelle =
              'Noch nicht abgerechnete Projekte ' + `[${projektNichtBereitFuerMonatsabschluss.length}]`;
            spinnerOverlay.close();
          }
        );
      });
  }

  private initialisiereTabellenDaten(): void {
    this.nichtAbgeschlosseneMitarbeiterTabelle.dataSource =
      new MatMultiSortTableDataSource<NichtAbgeschlosseneMitarbeiterTabellendarstellung>(this.mitarbeiterSort, true);

    this.nichtAbgeschlosseneProjekteTabelle.dataSource =
      new MatMultiSortTableDataSource<NichtAbgeschlosseneProjekteTabellendarstellung>(this.projekteSort, true);
  }

  private initialisiereTabellenSortierung(): void {
    this.nichtAbgeschlosseneMitarbeiterTabelle.sortParams = ['bearbeitungsstatus', 'mitarbeiter'];
    this.nichtAbgeschlosseneMitarbeiterTabelle.sortDirs = ['asc', 'asc'];
    this.nichtAbgeschlosseneMitarbeiterTabelle.updateSortHeaders();

    this.nichtAbgeschlosseneProjekteTabelle.sortParams = ['bearbeitungsstatus', 'projektnummer'];
    this.nichtAbgeschlosseneProjekteTabelle.sortDirs = ['asc', 'asc'];
    this.nichtAbgeschlosseneProjekteTabelle.updateSortHeaders();
  }

  private setzeAktuellenAbrechnungsmonat() {
    const abrechnungsmonat: Abrechnungsmonat | undefined =
      this.abrechnungsmonatService.erhalteVorauswahlFuerAbrechnungsmonat(this.abrechnungsmonateAuswahl);
    this.monatsabschlussFormGroup.get('abrechnungsmonat')?.setValue(abrechnungsmonat as Abrechnungsmonat);
  }

  private aktualisiereMonatsabschlussAktionTabellendarstellung(monatsabschlussAktions: MonatsabschlussAktion[]) {
    this.monatsabschlussAktionenTabelle.data = monatsabschlussAktions.map((monatsabschlussAktion) => {
      return this.mapMonatsabschlussAktionAufTabellendarstellung(monatsabschlussAktion);
    });
  }

  private aktualisiereNichtAbgeschlosseneMitarbeiterTabelleTabellendarstellung(
    mitarbeiterNichtBereitFuerMonatsabschluss: MitarbeiterNichtBereitFuerMonatsabschluss[]
  ) {
    this.nichtAbgeschlosseneMitarbeiterTabelle.data = mitarbeiterNichtBereitFuerMonatsabschluss.map(
      (nichtBereiteMitarbeiter) => {
        return this.mapMitarbeiterNichtBereitFuerMonatsabschlussAufTabellendarstellung(nichtBereiteMitarbeiter);
      }
    );
  }

  private aktualisiereNichtAbgeschlosseneProjektTabelleTabellendarstellung(
    projektNichtBereitFuerMonatsabschluss: Projektabrechnung[]
  ) {
    this.nichtAbgeschlosseneProjekteTabelle.data = projektNichtBereitFuerMonatsabschluss.map((nichtBereiteProjekte) => {
      return this.mapProjektNichtBereitFuerMonatsabschlussAufTabellendarstellung(nichtBereiteProjekte);
    });
  }

  private mapMonatsabschlussAktionAufTabellendarstellung(
    monatsabschlussAktion: MonatsabschlussAktion
  ): MonatsabschlussAktionTabellendarstellung {
    const mitarbeiter: Mitarbeiter = getObjektAusListeDurchId(
      monatsabschlussAktion.mitarbeiterID,
      this.mitarbeiterAuswahl
    ) as Mitarbeiter;
    return {
      aktion: monatsabschlussAktion.titel,
      mitarbeiter: this.mitarbeiterAnzeigeNamePipe.transform(mitarbeiter),
      datum: monatsabschlussAktion.durchgefuehrtAm,
    };
  }

  private mapMitarbeiterNichtBereitFuerMonatsabschlussAufTabellendarstellung(
    mitarbeiterNichtBereitFuerMonatsabschluss: MitarbeiterNichtBereitFuerMonatsabschluss
  ): NichtAbgeschlosseneMitarbeiterTabellendarstellung {
    const mitarbeiter: Mitarbeiter = getObjektAusListeDurchId(
      mitarbeiterNichtBereitFuerMonatsabschluss.mitarbeiterId,
      this.mitarbeiterAuswahl
    ) as Mitarbeiter;
    let sachbearbeiter;
    if (mitarbeiter && mitarbeiter.sachbearbeiterId) {
      sachbearbeiter = getObjektAusListeDurchId(mitarbeiter.sachbearbeiterId, this.sacharbeiterAuswahl) as Mitarbeiter;
    }
    return {
      mitarbeiterName: this.mitarbeiterAnzeigeNamePipe.transform(mitarbeiter),
      mitarbeiter: mitarbeiter,
      internExtern: this.isInternToTextPipe.transform(mitarbeiter),
      sachbearbeiterName: sachbearbeiter ? this.mitarbeiterAnzeigeNamePipe.transform(sachbearbeiter) : '',
      bearbeitungsstatus: mitarbeiterNichtBereitFuerMonatsabschluss.statusId
        ? this.statusIdZuBearbeitungsstatusEnumPipe.transform(mitarbeiterNichtBereitFuerMonatsabschluss.statusId)
        : BearbeitungsstatusEnum.OFFEN,
    };
  }

  private mapProjektNichtBereitFuerMonatsabschlussAufTabellendarstellung(
    projektNichtBereitFuerMonatsabschluss: Projektabrechnung
  ): NichtAbgeschlosseneProjekteTabellendarstellung {
    const projekt = getObjektAusListeDurchId(
      projektNichtBereitFuerMonatsabschluss.projektId,
      this.projektAuswahl
    ) as Projekt;
    let sachbearbeiter;
    if (projekt && projekt.sachbearbeiterId) {
      sachbearbeiter = getObjektAusListeDurchId(projekt.sachbearbeiterId, this.sacharbeiterAuswahl) as Mitarbeiter;
    }
    return {
      projektnummer: projekt.projektnummer,
      projektbezeichnung: projekt.bezeichnung,
      sachbearbeiter: sachbearbeiter ? this.mitarbeiterAnzeigeNamePipe.transform(sachbearbeiter) : '',
      bearbeitungsstatus: projektNichtBereitFuerMonatsabschluss.statusId
        ? this.statusIdZuBearbeitungsstatusEnumPipe.transform(projektNichtBereitFuerMonatsabschluss.statusId)
        : BearbeitungsstatusEnum.NICHTGEBUCHT,
    };
  }
}
