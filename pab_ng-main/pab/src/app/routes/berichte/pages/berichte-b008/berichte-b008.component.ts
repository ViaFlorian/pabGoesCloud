import { AfterViewChecked, AfterViewInit, ChangeDetectorRef, Component, ViewChild } from '@angular/core';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { ErgebnisB008UebersichtTabellendarstellung } from '../../model/ergebnis-b008-uebersicht-tabellendarstellung';
import { BenachrichtigungService } from '../../../../shared/service/benachrichtigung.service';
import { MatDialog } from '@angular/material/dialog';
import { ActionBarButtonKonfiguration } from '../../model/action-bar-button-konfiguration';
import { Projekt } from 'src/app/shared/model/projekt/projekt';
import { Kunde } from 'src/app/shared/model/kunde/kunde';
import { Organisationseinheit } from 'src/app/shared/model/organisationseinheit/organisationseinheit';
import { MatDatepicker } from '@angular/material/datepicker';
import { SpinnerOverlayService } from '../../../../shared/service/spinner-overlay.service';
import { BerichteService } from '../../../../shared/service/berichte.service';
import { ErgebnisB008Uebersicht } from 'src/app/shared/model/berichte/ergebnis-b008-uebersicht';
import { forkJoin } from 'rxjs';
import { MitarbeiterService } from '../../../../shared/service/mitarbeiter.service';
import { ProjektService } from '../../../../shared/service/projekt.service';
import { KundeService } from '../../../../shared/service/kunde.service';
import { OrganisationseinheitService } from '../../../../shared/service/organisationseinheit.service';
import { CustomValidator } from '../../../../shared/validation/custom-validator';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';
import * as CompareUtil from '../../../../shared/util/compare.util';
import { AbrechnungsmonatAnzeigeNamePipe } from '../../../../shared/pipe/abrechnungsmonat-anzeige-name.pipe';

@Component({
  selector: 'pab-berichte-b008',
  templateUrl: './berichte-b008.component.html',
  styleUrls: ['./berichte-b008.component.scss'],
  providers: [
    {
      provide: MAT_DATE_FORMATS,
      useValue: {
        parse: {
          dateInput: 'MM.yy',
        },
        display: {
          dateInput: 'MM.yy',
          monthYearLabel: 'MMM y',
          dateA11yLabel: 'LL',
          monthYearA11yLabel: 'MMMM y',
        },
      },
    },
  ],
})
export class BerichteB008Component implements AfterViewInit, AfterViewChecked {
  ueberschrift: string = '';
  hinweisText: string = 'Berichtsrelevant sind nur Dienstleistungsprojekte mit Status "abgerechnet" oder höher';
  anzahlErgebnisse = 0;
  ladeBerichteSpinner: SpinnerRef = new SpinnerRef();

  actionBarButtonKonfiguration: ActionBarButtonKonfiguration = {
    erstelleExcelDeaktiviert: false,
    erstellePDFDeaktiviert: true,
    versendePDFDeaktiviert: true,
  };

  berichtFilterFormGroup: FormGroup;
  sachbearbeiterAuswahl: Mitarbeiter[] = [];
  projektAuswahl: Projekt[] = [];
  kundeAuswahl: Kunde[] = [];
  organisationseinheitAuswahl: Organisationseinheit[] = [];

  @ViewChild(MatMultiSort) sort!: MatMultiSort;
  spalten = [
    { id: 'projektnummer', name: 'Projektnummer' },
    { id: 'projektbezeichnung', name: 'Bezeichnung' },
    { id: 'abrechnungsmonat', name: 'Abrechnungsmonat' },
    { id: 'kosten', name: 'Kosten' },
    { id: 'leistungen', name: 'Leistungen' },
  ];
  tabelle: TableData<ErgebnisB008UebersichtTabellendarstellung>;

  private letzteFilterValues = {
    abrechnungsmonatAbWann: {} as Date,
    abrechnungsmonatBisWann: {} as Date,
    sachbearbeiter: {} as Mitarbeiter,
    projekt: {} as Projekt,
    kunde: {} as Kunde,
    organisationseinheit: {} as Organisationseinheit,
  };

  constructor(
    private fb: FormBuilder,
    private benachrichtigungsService: BenachrichtigungService,
    private changeDetectorRef: ChangeDetectorRef,
    private spinnerOverlayService: SpinnerOverlayService,
    private berichteService: BerichteService,
    private mitarbeiterService: MitarbeiterService,
    private projektService: ProjektService,
    private kundeService: KundeService,
    private organisationseinheitService: OrganisationseinheitService,
    private abrechnungsmonatAnzeigeNamePipe: AbrechnungsmonatAnzeigeNamePipe,
    public dialog: MatDialog
  ) {
    const abrechnungsmonatAbWannInit = new Date();
    abrechnungsmonatAbWannInit.setMonth(abrechnungsmonatAbWannInit.getMonth() - 1);

    this.berichtFilterFormGroup = this.fb.nonNullable.group({
      abrechnungsmonatAbWann: [abrechnungsmonatAbWannInit],
      abrechnungsmonatBisWann: [new Date()],
      sachbearbeiter: [{} as Mitarbeiter],
      projekt: [{} as Projekt, [CustomValidator.valueIstObjektOderLeererString()]],
      kunde: [{} as Kunde, [CustomValidator.valueIstObjektOderLeererString()]],
      organisationseinheit: [{} as Organisationseinheit],
      istDetailsAktiv: false,
      istAusgabeInPT: false,
    });

    this.letzteFilterValues = {
      abrechnungsmonatAbWann: abrechnungsmonatAbWannInit,
      abrechnungsmonatBisWann: new Date(),
      sachbearbeiter: {} as Mitarbeiter,
      projekt: {} as Projekt,
      kunde: {} as Kunde,
      organisationseinheit: {} as Organisationseinheit,
    };

    this.tabelle = new TableData<ErgebnisB008UebersichtTabellendarstellung>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<ErgebnisB008UebersichtTabellendarstellung>(
      new MatMultiSort(),
      true
    );
  }

  ngAfterViewInit(): void {
    this.reagiereAufFilterFormAenderung();
    this.initialisiereTabellenDaten();
    this.initialisiereTabellenSortierung();
    this.rufeInitialeDatenAb();
  }

  ngAfterViewChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  oeffneHinweisDialog() {
    this.benachrichtigungsService.erstelleInfoMessage(this.hinweisText);
  }

  onLeereFilter() {
    this.berichtFilterFormGroup.reset();
  }

  setAbrechnungsmonatAbWann(value: Date, datepicker: MatDatepicker<Date>) {
    const normalizedDatum = this.erzeugeDatumMitMonatUndJahr(value);
    this.berichtFilterFormGroup.get('abrechnungsmonatAbWann')?.setValue(normalizedDatum);
    datepicker.close();
  }

  setAbrechnungsmonatBisWann(value: Date, datepicker: MatDatepicker<Date>) {
    const normalizedDatum = this.erzeugeDatumMitMonatUndJahr(value);
    this.berichtFilterFormGroup.get('abrechnungsmonatBisWann')?.setValue(normalizedDatum);
    datepicker.close();
  }

  reagiereAufErstelleExcel() {
    const dataSpinner = {
      title: 'Excel Bericht wird erstellt',
    };
    const spinnerOverlay = this.spinnerOverlayService.open(dataSpinner);
    this.berichteService
      .erstelleUndOeffneB008AlsExcel(
        this.berichtFilterFormGroup.getRawValue().abrechnungsmonatAbWann,
        this.berichtFilterFormGroup.getRawValue().abrechnungsmonatBisWann,
        this.berichtFilterFormGroup.getRawValue().sachbearbeiter,
        this.berichtFilterFormGroup.getRawValue().projekt,
        this.berichtFilterFormGroup.getRawValue().kunde,
        this.berichtFilterFormGroup.getRawValue().organisationseinheit,
        this.berichtFilterFormGroup.getRawValue().istDetailsAktiv,
        this.berichtFilterFormGroup.getRawValue().istAusgabeInPT
      )
      .subscribe((excelBlob: Blob) => {
        this.berichteService.downloadFile(excelBlob, `B008_${this.berichteService.erstelleFileDateExtension()}.xlsx`);
        spinnerOverlay.close();
      });
  }

  private initialisiereTabellenDaten(): void {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<ErgebnisB008UebersichtTabellendarstellung>(
      this.sort,
      true
    );
    this.tabelle.data = [];
  }

  private initialisiereTabellenSortierung() {
    this.tabelle.sortParams = ['projektnummer'];
    this.tabelle.sortDirs = ['asc'];

    this.tabelle.updateSortHeaders();
  }

  private reagiereAufFilterFormAenderung() {
    this.berichtFilterFormGroup.valueChanges.subscribe(() => {
      if (!this.berichtFilterFormGroup.valid || !this.filterWerteHabenSichVeraendert()) {
        return;
      }
      this.aktualisiereLetzteFilterWerte();
      this.aktualisiereBerichtDaten(
        this.berichtFilterFormGroup.getRawValue().abrechnungsmonatAbWann,
        this.berichtFilterFormGroup.getRawValue().abrechnungsmonatBisWann,
        this.berichtFilterFormGroup.getRawValue().sachbearbeiter,
        this.berichtFilterFormGroup.getRawValue().projekt,
        this.berichtFilterFormGroup.getRawValue().kunde,
        this.berichtFilterFormGroup.getRawValue().organisationseinheit
      );
    });
  }

  private filterWerteHabenSichVeraendert() {
    if (
      CompareUtil.istDatumVonAbrechnungsmonatGleich(
        this.berichtFilterFormGroup.getRawValue().abrechnungsmonatAbWann,
        this.letzteFilterValues.abrechnungsmonatAbWann
      )
    ) {
      return true;
    }
    if (
      CompareUtil.istDatumVonAbrechnungsmonatGleich(
        this.berichtFilterFormGroup.getRawValue().abrechnungsmonatBisWann,
        this.letzteFilterValues.abrechnungsmonatBisWann
      )
    ) {
      return true;
    }
    if (this.berichtFilterFormGroup.getRawValue().sachbearbeiter !== this.letzteFilterValues.sachbearbeiter) {
      return true;
    }
    if (
      this.berichtFilterFormGroup.getRawValue().organisationseinheit !== this.letzteFilterValues.organisationseinheit
    ) {
      return true;
    }
    if (this.berichtFilterFormGroup.getRawValue().projekt !== this.letzteFilterValues.projekt) {
      return true;
    }
    if (this.berichtFilterFormGroup.getRawValue().kunde !== this.letzteFilterValues.kunde) {
      return true;
    }
    return false;
  }

  private aktualisiereLetzteFilterWerte() {
    this.letzteFilterValues = {
      abrechnungsmonatAbWann: this.berichtFilterFormGroup.getRawValue().abrechnungsmonatAbWann,
      abrechnungsmonatBisWann: this.berichtFilterFormGroup.getRawValue().abrechnungsmonatBisWann,
      sachbearbeiter: this.berichtFilterFormGroup.getRawValue().sachbearbeiter,
      organisationseinheit: this.berichtFilterFormGroup.getRawValue().organisationseinheit,
      projekt: this.berichtFilterFormGroup.getRawValue().projekt,
      kunde: this.berichtFilterFormGroup.getRawValue().kunde,
    };
  }

  private aktualisiereBerichtDaten(
    abrechnungsmonatAbWann: Date,
    abrechnungsmonatBisWann: Date,
    sachbearbeiter: Mitarbeiter,
    projekt: Projekt,
    kunde: Kunde,
    organisationseinheit: Organisationseinheit
  ) {
    this.ladeBerichteSpinner.open();
    this.berichteService
      .getB008Uebersicht(
        abrechnungsmonatAbWann,
        abrechnungsmonatBisWann,
        sachbearbeiter,
        projekt,
        kunde,
        organisationseinheit
      )
      .subscribe((b008Ergebnisse) => {
        this.tabelle.data = this.erstelleTabellenForm(b008Ergebnisse);
        this.anzahlErgebnisse = b008Ergebnisse.length;
        this.aktualisiereUeberschrift();
        this.aenderActionBarButtonKonfiguration();
        this.ladeBerichteSpinner.close();
      });
  }

  private erstelleTabellenForm(b008Ergebnisse: ErgebnisB008Uebersicht[]): ErgebnisB008UebersichtTabellendarstellung[] {
    return b008Ergebnisse.map((b008Eintrag) => {
      return {
        projektnummer: b008Eintrag.projektnummer,
        projektbezeichnung: b008Eintrag.projektbezeichnung,
        abrechnungsmonat: this.abrechnungsmonatAnzeigeNamePipe.transform(b008Eintrag),
        kosten: b008Eintrag.kosten,
        leistungen: b008Eintrag.leistungen,
        projektIstAktiv: b008Eintrag.projektIstAktiv,
      };
    });
  }

  private aktualisiereUeberschrift() {
    this.ueberschrift = `B008 - Rechnungsvorlage pro Projekt [${this.anzahlErgebnisse}]`;
  }

  private aenderActionBarButtonKonfiguration() {
    this.actionBarButtonKonfiguration.erstelleExcelDeaktiviert = this.anzahlErgebnisse === 0;
  }

  private erzeugeDatumMitMonatUndJahr(value: Date) {
    return new Date(value.getFullYear(), value.getMonth(), 1);
  }

  private rufeInitialeDatenAb() {
    forkJoin([
      this.mitarbeiterService.getAlleSachbearbeiter(),
      this.projektService.getAlleProjekte(),
      this.kundeService.getAlleKunden(),
      this.organisationseinheitService.getAlleOrganisationseinheiten(),
    ]).subscribe(([sachbearbeiter, projekt, kunde, organisationseinheit]) => {
      this.sachbearbeiterAuswahl = sachbearbeiter.slice();
      this.projektAuswahl = projekt.slice();
      this.kundeAuswahl = kunde.slice();
      this.organisationseinheitAuswahl = organisationseinheit.slice();

      this.aktualisiereBerichtDaten(
        this.berichtFilterFormGroup.getRawValue().abrechnungsmonatAbWann,
        this.berichtFilterFormGroup.getRawValue().abrechnungsmonatBisWann,
        this.berichtFilterFormGroup.getRawValue().sachbearbeiter,
        this.berichtFilterFormGroup.getRawValue().projekt,
        this.berichtFilterFormGroup.getRawValue().kunde,
        this.berichtFilterFormGroup.getRawValue().organisationseinheit
      );
    });
  }
}
