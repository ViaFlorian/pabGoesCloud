import { AfterViewChecked, AfterViewInit, ChangeDetectorRef, Component, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDatepicker } from '@angular/material/datepicker';
import { MatDialog } from '@angular/material/dialog';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { forkJoin } from 'rxjs';
import { SpinnerRef } from 'src/app/shared/component/spinner/spinner-ref';
import { ErgebnisB007Uebersicht } from 'src/app/shared/model/berichte/ergebnis-b007-uebersicht';
import { Kunde } from 'src/app/shared/model/kunde/kunde';
import { Mitarbeiter } from 'src/app/shared/model/mitarbeiter/mitarbeiter';
import { Organisationseinheit } from 'src/app/shared/model/organisationseinheit/organisationseinheit';
import { Projekt } from 'src/app/shared/model/projekt/projekt';
import { AbrechnungsmonatAnzeigeNamePipe } from 'src/app/shared/pipe/abrechnungsmonat-anzeige-name.pipe';
import { BerichteService } from 'src/app/shared/service/berichte.service';
import { KundeService } from 'src/app/shared/service/kunde.service';
import { MitarbeiterService } from 'src/app/shared/service/mitarbeiter.service';
import { OrganisationseinheitService } from 'src/app/shared/service/organisationseinheit.service';
import { ProjektService } from 'src/app/shared/service/projekt.service';
import { SpinnerOverlayService } from 'src/app/shared/service/spinner-overlay.service';
import { CustomValidator } from 'src/app/shared/validation/custom-validator';
import { ActionBarButtonKonfiguration } from '../../model/action-bar-button-konfiguration';
import { ErgebnisB007UebersichtTabellendarstellung } from '../../model/ergebnis-b007-uebersicht-tabellendarstellung';
import * as CompareUtil from '../../../../shared/util/compare.util';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { AktivInaktivEnum } from 'src/app/shared/enum/aktiv-inaktiv.enum';
import { BearbeitungsstatusEnum } from 'src/app/shared/enum/bearbeitungsstatus.enum';
import { BuchungstypBuchungOderKorrekturEnum } from 'src/app/shared/enum/buchungstyp-buchung-korrektur.enum';
import { ProjekttypEnum } from 'src/app/shared/enum/projekttyp.enum';
import { MitarbeitergruppeEnum } from 'src/app/shared/enum/mitarbeitergruppe.enum';
import { KostenartEnum } from 'src/app/shared/enum/kostenart.enum';
import {
  EmailDialogComponent,
  EmailDialogData,
} from '../../../../shared/component/email-dialog/email-dialog.component';
import { MitarbeiterQuittungEmailDaten } from '../../../../shared/model/arbeitsnachweis/mitarbeiter-quittung-email-daten';
import { BenachrichtigungService } from 'src/app/shared/service/benachrichtigung.service';

@Component({
  selector: 'pab-berichte-b007',
  templateUrl: './berichte-b007.component.html',
  styleUrls: ['./berichte-b007.component.scss'],
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
export class BerichteB007Component implements AfterViewInit, AfterViewChecked {
  ueberschrift: string = '';
  anzahlErgebnisse = 0;
  ladeBerichteSpinner: SpinnerRef = new SpinnerRef();
  ergebnisB007UebersichtTabellenDarstellung: ErgebnisB007UebersichtTabellendarstellung[] = [];

  actionBarButtonKonfiguration: ActionBarButtonKonfiguration = {
    erstelleExcelDeaktiviert: false,
    erstellePDFDeaktiviert: false,
    versendePDFDeaktiviert: false,
  };

  berichtFilterFormGroup: FormGroup;
  sachbearbeiterAuswahl: Mitarbeiter[] = [];
  eAktivInaktivAuswahl = AktivInaktivEnum;
  eBearbeitungsstatusAuswahl = [
    BearbeitungsstatusEnum.ALLE,
    BearbeitungsstatusEnum.ERFASST,
    BearbeitungsstatusEnum.ABGERECHNET,
    BearbeitungsstatusEnum.ABGESCHLOSSEN,
  ];
  eBuchungstypAuswahl = BuchungstypBuchungOderKorrekturEnum;
  projektAuswahl: Projekt[] = [];
  kundeAuswahl: Kunde[] = [];
  mitarbeiterAuswahl: Mitarbeiter[] = [];
  organisationseinheitAuswahl: Organisationseinheit[] = [];
  eProjekttypAuswahl = ProjekttypEnum;
  eMitarbeitergruppeAuswahl = MitarbeitergruppeEnum;
  eKostenartAuswahl = KostenartEnum;

  @ViewChild(MatMultiSort) sort!: MatMultiSort;
  spalten = [
    { id: 'projektnummer', name: 'Projektnummer' },
    { id: 'projektbezeichnung', name: 'Bezeichnung' },
    { id: 'abrechnungsmonat', name: 'Abrechnungsmonat' },
    { id: 'kosten', name: 'Kosten' },
    { id: 'leistungen', name: 'Leistungen' },
  ];
  tabelle: TableData<ErgebnisB007UebersichtTabellendarstellung>;

  private letzteFilterValues = {
    abrechnungsmonatAbWann: {} as Date,
    abrechnungsmonatBisWann: {} as Date,
    sachbearbeiter: {} as Mitarbeiter,
    aktivInaktiv: {} as AktivInaktivEnum,
    bearbeitungsstatus: {} as BearbeitungsstatusEnum,
    buchungstyp: {} as BuchungstypBuchungOderKorrekturEnum,
    projekt: {} as Projekt,
    kunde: {} as Kunde,
    mitarbeiter: {} as Mitarbeiter,
    organisationseinheit: {} as Organisationseinheit,
    projekttyp: {} as ProjekttypEnum,
    mitarbeitergruppe: {} as MitarbeitergruppeEnum,
    kostenarten: {} as Array<String>,
  };

  constructor(
    private fb: FormBuilder,
    private changeDetectorRef: ChangeDetectorRef,
    private spinnerOverlayService: SpinnerOverlayService,
    private berichteService: BerichteService,
    private mitarbeiterService: MitarbeiterService,
    private projektService: ProjektService,
    private benachrichtigungsService: BenachrichtigungService,
    private kundeService: KundeService,
    private organisationseinheitService: OrganisationseinheitService,
    private abrechnungsmonatAnzeigeNamePipe: AbrechnungsmonatAnzeigeNamePipe,
    public dialog: MatDialog
  ) {
    const abrechnungsmonatAbWannInit = new Date();
    abrechnungsmonatAbWannInit.setMonth(abrechnungsmonatAbWannInit.getMonth() - 1);
    const kostenartenInit: Array<String> = Object.values(KostenartEnum);

    this.berichtFilterFormGroup = this.fb.nonNullable.group({
      abrechnungsmonatAbWann: [abrechnungsmonatAbWannInit],
      abrechnungsmonatBisWann: [new Date()],
      sachbearbeiter: [{} as Mitarbeiter],
      aktivInaktiv: [AktivInaktivEnum.ALLE],
      bearbeitungsstatus: [BearbeitungsstatusEnum.ALLE],
      buchungstyp: [BuchungstypBuchungOderKorrekturEnum.ALLE],
      projekt: [{} as Projekt, [CustomValidator.valueIstObjektOderLeererString()]],
      kunde: [{} as Kunde, [CustomValidator.valueIstObjektOderLeererString()]],
      mitarbeiter: [{} as Mitarbeiter, [CustomValidator.valueIstObjektOderLeererString()]],
      organisationseinheit: [{} as Organisationseinheit],
      projekttyp: [ProjekttypEnum.ALLE],
      mitarbeitergruppe: [MitarbeitergruppeEnum.ALLE],
      kostenarten: [kostenartenInit],
      istKostenartdetails: true,
    });

    this.letzteFilterValues = {
      abrechnungsmonatAbWann: abrechnungsmonatAbWannInit,
      abrechnungsmonatBisWann: new Date(),
      sachbearbeiter: {} as Mitarbeiter,
      aktivInaktiv: AktivInaktivEnum.ALLE,
      bearbeitungsstatus: BearbeitungsstatusEnum.ALLE,
      buchungstyp: BuchungstypBuchungOderKorrekturEnum.ALLE,
      projekt: {} as Projekt,
      kunde: {} as Kunde,
      mitarbeiter: {} as Mitarbeiter,
      organisationseinheit: {} as Organisationseinheit,
      projekttyp: ProjekttypEnum.ALLE,
      mitarbeitergruppe: MitarbeitergruppeEnum.ALLE,
      kostenarten: kostenartenInit,
    };

    this.tabelle = new TableData<ErgebnisB007UebersichtTabellendarstellung>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<ErgebnisB007UebersichtTabellendarstellung>(
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
    this.berichteService.erstelleUndOeffneB007AlsExcel
      .apply(this.berichteService, this.leseAlleFormfelderAus())
      .subscribe((excelBlob: Blob) => {
        this.berichteService.downloadFile(excelBlob, `B007_${this.berichteService.erstelleFileDateExtension()}.xlsx`);
        spinnerOverlay.close();
      });
  }

  erstelleUndZeigePdf() {
    const dataSpinner = {
      title: 'PDF wird generiert...',
    };
    const spinnerOverlay = this.spinnerOverlayService.open(dataSpinner);

    this.berichteService.erstelleUndOeffneB007AlsPdf
      .apply(this.berichteService, this.leseAlleFormfelderAus())
      .subscribe((pdfBlob: Blob) => {
        this.berichteService.downloadFile(pdfBlob, `B007_${this.berichteService.erstelleFileDateExtension()}.pdf`);
        spinnerOverlay.close();
      });
  }

  versendePdfAlsEmail() {
    const data: EmailDialogData = {
      empfanger: '',
      empfangerBearbeitbar: true,
      mitarbeiterEmails: this.getEmailAdressenVonMitarbeitern(),
      betreff: 'Kosten, Leistung pro Projekt',
      nachricht: 'Email automatisch durch PAB 2.0 erstellt',
      dateiname: `B007_${this.berichteService.erstelleFileDateExtension()}.pdf`,
      sumbmitButtonText: 'Bericht senden',
      abbruchButtonText: 'Abbruch',
    };
    const emailDialogRef = this.dialog.open(EmailDialogComponent, {
      data,
      panelClass: 'dialog-container-default',
      disableClose: true,
      autoFocus: false,
    });
    emailDialogRef.componentInstance.sendeEmailEvent.subscribe((emailDaten: MitarbeiterQuittungEmailDaten) => {
      this.berichteService
        .sendeB007Email(
          emailDaten,
          this.berichtFilterFormGroup.getRawValue().abrechnungsmonatAbWann,
          this.berichtFilterFormGroup.getRawValue().abrechnungsmonatBisWann,
          this.berichtFilterFormGroup.getRawValue().sachbearbeiter,
          this.berichtFilterFormGroup.getRawValue().aktivInaktiv,
          this.berichtFilterFormGroup.getRawValue().bearbeitungsstatus,
          this.berichtFilterFormGroup.getRawValue().buchungstyp,
          this.berichtFilterFormGroup.getRawValue().projekt,
          this.berichtFilterFormGroup.getRawValue().kunde,
          this.berichtFilterFormGroup.getRawValue().organisationseinheit,
          this.berichtFilterFormGroup.getRawValue().mitarbeiter,
          this.berichtFilterFormGroup.getRawValue().projekttyp,
          this.berichtFilterFormGroup.getRawValue().mitarbeitergruppe,
          this.berichtFilterFormGroup.getRawValue().istKostenartdetails,
          this.berichtFilterFormGroup.getRawValue().kostenarten
        )
        .subscribe(() => {
          this.benachrichtigungsService.erstelleBenachrichtigung('Email erfolgreich versendet');
        });
    });
    emailDialogRef.componentInstance.oeffnePdfAnhangEvent.subscribe(() => {
      this.erstelleUndZeigePdf();
    });
  }

  private initialisiereTabellenDaten(): void {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<ErgebnisB007UebersichtTabellendarstellung>(
      this.sort,
      true
    );
    this.tabelle.data = this.ergebnisB007UebersichtTabellenDarstellung;
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
        this.berichtFilterFormGroup.getRawValue().aktivInaktiv,
        this.berichtFilterFormGroup.getRawValue().bearbeitungsstatus,
        this.berichtFilterFormGroup.getRawValue().buchungstyp,
        this.berichtFilterFormGroup.getRawValue().projekt,
        this.berichtFilterFormGroup.getRawValue().kunde,
        this.berichtFilterFormGroup.getRawValue().organisationseinheit,
        this.berichtFilterFormGroup.getRawValue().mitarbeiter,
        this.berichtFilterFormGroup.getRawValue().projekttyp,
        this.berichtFilterFormGroup.getRawValue().mitarbeitergruppe,
        this.berichtFilterFormGroup.getRawValue().kostenarten
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
    if (
      !CompareUtil.sindMitarbeiterGleich(
        this.berichtFilterFormGroup.getRawValue().sachbearbeiter,
        this.letzteFilterValues.sachbearbeiter
      )
    ) {
      return true;
    }
    if (this.berichtFilterFormGroup.getRawValue().aktivInaktiv !== this.letzteFilterValues.aktivInaktiv) {
      return true;
    }
    if (this.berichtFilterFormGroup.getRawValue().bearbeitungsstatus !== this.letzteFilterValues.bearbeitungsstatus) {
      return true;
    }
    if (this.berichtFilterFormGroup.getRawValue().buchungstyp !== this.letzteFilterValues.buchungstyp) {
      return true;
    }
    if (
      !CompareUtil.sindProjekteGleich(
        this.berichtFilterFormGroup.getRawValue().projekt,
        this.letzteFilterValues.projekt
      )
    ) {
      return true;
    }
    if (!CompareUtil.sindKundenGleich(this.berichtFilterFormGroup.getRawValue().kunde, this.letzteFilterValues.kunde)) {
      return true;
    }
    if (
      !CompareUtil.sindMitarbeiterGleich(
        this.berichtFilterFormGroup.getRawValue().mitarbeiter,
        this.letzteFilterValues.mitarbeiter
      )
    ) {
      return true;
    }
    if (
      !CompareUtil.sindOrganisationseinheitenGleich(
        this.berichtFilterFormGroup.getRawValue().organisationseinheit,
        this.letzteFilterValues.organisationseinheit
      )
    ) {
      return true;
    }
    if (this.berichtFilterFormGroup.getRawValue().projekttyp !== this.letzteFilterValues.projekttyp) {
      return true;
    }
    if (this.berichtFilterFormGroup.getRawValue().mitarbeitergruppe !== this.letzteFilterValues.mitarbeitergruppe) {
      return true;
    }
    if (
      !CompareUtil.sindKostenartenGleich(
        this.berichtFilterFormGroup.getRawValue().kostenarten,
        this.letzteFilterValues.kostenarten
      )
    ) {
      return true;
    }
    return false;
  }

  private aktualisiereLetzteFilterWerte() {
    this.letzteFilterValues = {
      abrechnungsmonatAbWann: this.berichtFilterFormGroup.getRawValue().abrechnungsmonatAbWann,
      abrechnungsmonatBisWann: this.berichtFilterFormGroup.getRawValue().abrechnungsmonatBisWann,
      sachbearbeiter: this.berichtFilterFormGroup.getRawValue().sachbearbeiter,
      aktivInaktiv: this.berichtFilterFormGroup.getRawValue().aktivInaktiv,
      bearbeitungsstatus: this.berichtFilterFormGroup.getRawValue().bearbeitungsstatus,
      buchungstyp: this.berichtFilterFormGroup.getRawValue().buchungstyp,
      projekt: this.berichtFilterFormGroup.getRawValue().projekt,
      kunde: this.berichtFilterFormGroup.getRawValue().kunde,
      mitarbeiter: this.berichtFilterFormGroup.getRawValue().mitarbeiter,
      organisationseinheit: this.berichtFilterFormGroup.getRawValue().organisationseinheit,
      projekttyp: this.berichtFilterFormGroup.getRawValue().projekttyp,
      mitarbeitergruppe: this.berichtFilterFormGroup.getRawValue().mitarbeitergruppe,
      kostenarten: this.berichtFilterFormGroup.getRawValue().kostenarten,
    };
  }

  private aktualisiereBerichtDaten(
    abrechnungsmonatAbWann: Date,
    abrechnungsmonatBisWann: Date,
    sachbearbeiter: Mitarbeiter,
    aktivInaktiv: AktivInaktivEnum,
    bearbeitungsstatus: BearbeitungsstatusEnum,
    buchungstyp: BuchungstypBuchungOderKorrekturEnum,
    projekt: Projekt,
    kunde: Kunde,
    organisationseinheit: Organisationseinheit,
    mitarbeiter: Mitarbeiter,
    projekttyp: ProjekttypEnum,
    mitarbeitergruppe: MitarbeitergruppeEnum,
    kostenarten: Array<String>
  ) {
    this.ladeBerichteSpinner.open();
    this.berichteService
      .getB007Uebersicht(
        abrechnungsmonatAbWann,
        abrechnungsmonatBisWann,
        sachbearbeiter,
        aktivInaktiv,
        bearbeitungsstatus,
        buchungstyp,
        projekt,
        kunde,
        organisationseinheit,
        mitarbeiter,
        projekttyp,
        mitarbeitergruppe,
        kostenarten
      )
      .subscribe((b007Ergebnisse) => {
        this.tabelle.data = this.erstelleTabellenForm(b007Ergebnisse);
        this.anzahlErgebnisse = b007Ergebnisse.length;
        this.aktualisiereUeberschrift();
        this.aenderActionBarButtonKonfiguration();
        this.ladeBerichteSpinner.close();
      });
  }

  private erstelleTabellenForm(b007Ergebnisse: ErgebnisB007Uebersicht[]): ErgebnisB007UebersichtTabellendarstellung[] {
    return b007Ergebnisse.map((b007Eintrag) => {
      return {
        projektnummer: b007Eintrag.projektnummer,
        projektbezeichnung: b007Eintrag.projektbezeichnung,
        abrechnungsmonat: this.abrechnungsmonatAnzeigeNamePipe.transform(b007Eintrag),
        kosten: b007Eintrag.kosten,
        leistungen: b007Eintrag.leistungen,
        projektIstAktiv: b007Eintrag.projektIstAktiv,
      };
    });
  }

  private aktualisiereUeberschrift() {
    this.ueberschrift = `B007 - Kosten, Leistung pro Projekt [${this.anzahlErgebnisse}]`;
  }

  private aenderActionBarButtonKonfiguration() {
    this.actionBarButtonKonfiguration.erstelleExcelDeaktiviert = this.anzahlErgebnisse === 0;
    this.actionBarButtonKonfiguration.erstellePDFDeaktiviert = this.anzahlErgebnisse === 0;
    this.actionBarButtonKonfiguration.versendePDFDeaktiviert = this.anzahlErgebnisse === 0;
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
      this.mitarbeiterService.getMitarbeiterAuswahlFuerArbeitsnachweisBearbeiten(),
    ]).subscribe(([sachbearbeiter, projekt, kunde, organisationseinheit, mitarbeiter]) => {
      this.sachbearbeiterAuswahl = sachbearbeiter.slice();
      this.projektAuswahl = projekt.slice();
      this.kundeAuswahl = kunde.slice();
      this.organisationseinheitAuswahl = organisationseinheit.slice();
      this.mitarbeiterAuswahl = mitarbeiter.slice();

      this.aktualisiereBerichtDaten(
        this.berichtFilterFormGroup.getRawValue().abrechnungsmonatAbWann,
        this.berichtFilterFormGroup.getRawValue().abrechnungsmonatBisWann,
        this.berichtFilterFormGroup.getRawValue().sachbearbeiter,
        this.berichtFilterFormGroup.getRawValue().aktivInaktiv,
        this.berichtFilterFormGroup.getRawValue().bearbeitungsstatus,
        this.berichtFilterFormGroup.getRawValue().buchungstyp,
        this.berichtFilterFormGroup.getRawValue().projekt,
        this.berichtFilterFormGroup.getRawValue().kunde,
        this.berichtFilterFormGroup.getRawValue().organisationseinheit,
        this.berichtFilterFormGroup.getRawValue().mitarbeiter,
        this.berichtFilterFormGroup.getRawValue().projekttyp,
        this.berichtFilterFormGroup.getRawValue().mitarbeitergruppe,
        this.berichtFilterFormGroup.getRawValue().kostenarten
      );
    });
  }

  private getEmailAdressenVonMitarbeitern() {
    return this.mitarbeiterAuswahl
      .map((mitarbeiter) => {
        return mitarbeiter.email;
      })
      .filter((value, index, array) => {
        return array.indexOf(value) === index;
      });
  }

  private leseAlleFormfelderAus(): [
    Date,
    Date,
    Mitarbeiter,
    AktivInaktivEnum,
    BearbeitungsstatusEnum,
    BuchungstypBuchungOderKorrekturEnum,
    Projekt,
    Kunde,
    Organisationseinheit,
    Mitarbeiter,
    ProjekttypEnum,
    MitarbeitergruppeEnum,
    boolean,
    Array<String>
  ] {
    return [
      this.berichtFilterFormGroup.getRawValue().abrechnungsmonatAbWann,
      this.berichtFilterFormGroup.getRawValue().abrechnungsmonatBisWann,
      this.berichtFilterFormGroup.getRawValue().sachbearbeiter,
      this.berichtFilterFormGroup.getRawValue().aktivInaktiv,
      this.berichtFilterFormGroup.getRawValue().bearbeitungsstatus,
      this.berichtFilterFormGroup.getRawValue().buchungstyp,
      this.berichtFilterFormGroup.getRawValue().projekt,
      this.berichtFilterFormGroup.getRawValue().kunde,
      this.berichtFilterFormGroup.getRawValue().organisationseinheit,
      this.berichtFilterFormGroup.getRawValue().mitarbeiter,
      this.berichtFilterFormGroup.getRawValue().projekttyp,
      this.berichtFilterFormGroup.getRawValue().mitarbeitergruppe,
      this.berichtFilterFormGroup.getRawValue().istKostenartdetails,
      this.berichtFilterFormGroup.getRawValue().kostenarten,
    ];
  }
}
