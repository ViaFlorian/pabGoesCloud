import { AfterViewChecked, AfterViewInit, ChangeDetectorRef, Component, ViewChild } from '@angular/core';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MitarbeiterService } from '../../../../shared/service/mitarbeiter.service';
import { ArbeitsnachweisService } from '../../../../shared/service/arbeitsnachweis.service';
import { AbrechnungsmonatService } from '../../../../shared/service/abrechnungsmonat.service';
import { BerichteService } from '../../../../shared/service/berichte.service';
import { SpinnerOverlayService } from '../../../../shared/service/spinner-overlay.service';
import { BearbeitungsstatusEnum } from '../../../../shared/enum/bearbeitungsstatus.enum';
import * as CompareUtil from '../../../../shared/util/compare.util';
import { forkJoin } from 'rxjs';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { ErgebnisB004UebersichtTabellendarstellung } from '../../model/ergebnis-b004-uebersicht-tabellendarstellung';
import { ErgebnisB004Uebersicht } from '../../../../shared/model/berichte/ergebnis-b004-uebersicht';
import { StatusIdZuBearbeitungsstatusEnumPipe } from '../../../../shared/pipe/status-id-zu-bearbeitungsstatus-enum.pipe';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { BenachrichtigungService } from '../../../../shared/service/benachrichtigung.service';
import {
  EmailDialogComponent,
  EmailDialogData,
} from '../../../../shared/component/email-dialog/email-dialog.component';
import { MitarbeiterQuittungEmailDaten } from '../../../../shared/model/arbeitsnachweis/mitarbeiter-quittung-email-daten';
import { MatDialog } from '@angular/material/dialog';
import { ActionBarButtonKonfiguration } from '../../model/action-bar-button-konfiguration';
import { CustomValidator } from '../../../../shared/validation/custom-validator';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';
import { Abrechnungsmonat } from '../../../../shared/model/sonstiges/abrechnungsmonat';

@Component({
  selector: 'pab-berichte-b004',
  templateUrl: './berichte-b004.component.html',
  styleUrls: ['./berichte-b004.component.scss'],
})
export class BerichteB004Component implements AfterViewInit, AfterViewChecked {
  ueberschrift: string = '';
  hinweisText: string = 'Berichtsrelevant sind nur Arbeitsnachweise mit Status "abgerechnet" oder höher';
  ladeBerichteSpinner: SpinnerRef = new SpinnerRef();

  actionBarButtonKonfiguration: ActionBarButtonKonfiguration = {
    erstelleExcelDeaktiviert: true,
    erstellePDFDeaktiviert: false,
    versendePDFDeaktiviert: false,
  };

  berichtFilterFormGroup: FormGroup;
  mitarbeiterAuswahl: Mitarbeiter[] = [];
  sachbearbeiterAuswahl: Mitarbeiter[] = [];
  abrechnungsmonatAuswahl: Abrechnungsmonat[] = [];
  eBearbeitungsstatusEnum = [
    BearbeitungsstatusEnum.ALLE,
    BearbeitungsstatusEnum.ERFASST,
    BearbeitungsstatusEnum.ABGERECHNET,
    BearbeitungsstatusEnum.ABGESCHLOSSEN,
  ];
  @ViewChild(MatMultiSort) sort!: MatMultiSort;
  spalten = [
    { id: 'mitarbeiter', name: 'Mitarbeiter:in' },
    { id: 'sachbearbeiter', name: 'Sachbearbeiter:in' },
    { id: 'bearbeitungsstatus', name: 'Bearbeitungsstatus' },
    { id: 'projektstunden', name: 'Projektstunden' },
    { id: 'reisezeit', name: 'Reisezeit' },
    { id: 'sonderarbeitszeit', name: 'Sonderarbeitszeit' },
    { id: 'rufbereitschaft', name: 'Rufbereitschaft' },
  ];
  tabelle: TableData<ErgebnisB004UebersichtTabellendarstellung>;
  anzahlErgebnisse = 0;
  private letzteFilterValues = {
    abrechnungsmonat: {} as Abrechnungsmonat,
    mitarbeiter: {} as Mitarbeiter,
    sachbearbeiter: {} as Mitarbeiter,
    bearbeitungsstatus: BearbeitungsstatusEnum.ALLE,
  };

  constructor(
    private fb: FormBuilder,
    private mitarbeiterService: MitarbeiterService,
    private arbeitsnachweisService: ArbeitsnachweisService,
    private abrechnungsmonatService: AbrechnungsmonatService,
    private berichteService: BerichteService,
    private benachrichtigungsService: BenachrichtigungService,
    private changeDetectorRef: ChangeDetectorRef,
    private spinnerOverlayService: SpinnerOverlayService,
    private statusIdZuBearbeitungsstatusEnumPipe: StatusIdZuBearbeitungsstatusEnumPipe,
    public dialog: MatDialog
  ) {
    this.berichtFilterFormGroup = this.fb.nonNullable.group({
      abrechnungsmonat: [{} as Abrechnungsmonat],
      mitarbeiter: [{} as Mitarbeiter, [CustomValidator.valueIstObjektOderLeererString()]],
      sachbearbeiter: [{} as Mitarbeiter],
      bearbeitungsstatus: [BearbeitungsstatusEnum.ALLE],
    });

    this.tabelle = new TableData<ErgebnisB004UebersichtTabellendarstellung>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<ErgebnisB004UebersichtTabellendarstellung>(
      new MatMultiSort(),
      true
    );
  }

  ngAfterViewInit(): void {
    this.reagiereAufFilterFormAenderung();
    this.initialisiereTabelleUndSortierung();
    this.rufeInitialeDatenAb();
  }

  ngAfterViewChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  sindAbrechnungsmonateGleich(abrechnungsmonat1: Abrechnungsmonat, abrechnungsmonat2: Abrechnungsmonat): boolean {
    return CompareUtil.sindAbrechnungsmonateGleich(abrechnungsmonat1, abrechnungsmonat2);
  }

  sindMitarbeiterGleich(mitarbeiter1: Mitarbeiter, mitarbeiter2: Mitarbeiter): boolean {
    return CompareUtil.sindMitarbeiterGleich(mitarbeiter1, mitarbeiter2);
  }

  onLeereFilter() {
    this.berichtFilterFormGroup.reset();
    this.setzeAktuellenAbrechnungsmonat();
  }

  oeffneHinweisDialog() {
    this.benachrichtigungsService.erstelleInfoMessage(this.hinweisText);
  }

  erstelleUndZeigePdf() {
    const dataSpinner = {
      title: 'PDF wird generiert...',
    };
    const spinnerOverlay = this.spinnerOverlayService.open(dataSpinner);

    this.berichteService
      .erstelleUndOeffneB004AlsPdf(
        this.berichtFilterFormGroup.getRawValue().abrechnungsmonat,
        this.berichtFilterFormGroup.getRawValue().mitarbeiter,
        this.berichtFilterFormGroup.getRawValue().sachbearbeiter,
        this.berichtFilterFormGroup.getRawValue().bearbeitungsstatus
      )
      .subscribe((pdfBlob: Blob) => {
        this.berichteService.downloadFile(pdfBlob, `B004_${this.berichteService.erstelleFileDateExtension()}.pdf`);
        spinnerOverlay.close();
      });
  }

  versendePdfAlsEmail() {
    const data: EmailDialogData = {
      empfanger: '',
      empfangerBearbeitbar: true,
      mitarbeiterEmails: this.getEmailAdressenVonMitarbeitern(),
      betreff: 'Arbeitsnachweis pro Mitarbeiter:in und Abrechnungsmonat',
      nachricht: 'Email automatisch durch PAB 2.0 erstellt',
      dateiname: `B004_${this.berichteService.erstelleFileDateExtension()}.pdf`,
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
        .sendeB004Email(
          emailDaten,
          this.berichtFilterFormGroup.getRawValue().abrechnungsmonat,
          this.berichtFilterFormGroup.getRawValue().mitarbeiter,
          this.berichtFilterFormGroup.getRawValue().sachbearbeiter,
          this.berichtFilterFormGroup.getRawValue().bearbeitungsstatus
        )
        .subscribe(() => {
          this.benachrichtigungsService.erstelleBenachrichtigung('Email erfolgreich versendet');
        });
    });
    emailDialogRef.componentInstance.oeffnePdfAnhangEvent.subscribe(() => {
      this.erstelleUndZeigePdf();
    });
  }

  private initialisiereTabelleUndSortierung() {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<ErgebnisB004UebersichtTabellendarstellung>(
      this.sort,
      true
    );

    this.tabelle.sortParams = ['mitarbeiter'];
    this.tabelle.sortDirs = ['asc'];

    this.tabelle.updateSortHeaders();
  }

  private rufeInitialeDatenAb() {
    forkJoin([
      this.mitarbeiterService.getMitarbeiterAuswahlFuerArbeitsnachweisBearbeiten(),
      this.mitarbeiterService.getAlleSachbearbeiter(),
      this.arbeitsnachweisService.getAlleAbrechnungsmonate(),
    ]).subscribe(([mitarbeiter, sachbearbeiter, abrechnungsmonate]) => {
      this.mitarbeiterAuswahl = mitarbeiter.slice();
      this.sachbearbeiterAuswahl = sachbearbeiter.slice();
      this.abrechnungsmonatAuswahl = abrechnungsmonate.slice();

      this.abrechnungsmonatService.sortiereAbrechnungsmonate(abrechnungsmonate);
      this.abrechnungsmonatAuswahl = abrechnungsmonate;
      this.setzeAktuellenAbrechnungsmonat();
    });
  }

  private aktualisiereBerichtDaten(
    abrechnungsmonat: Abrechnungsmonat,
    mitarbeiter?: Mitarbeiter,
    sachbearbeiter?: Mitarbeiter,
    bearbeitungsstatus?: BearbeitungsstatusEnum
  ) {
    if (!abrechnungsmonat || !abrechnungsmonat.jahr) {
      return;
    }
    this.ladeBerichteSpinner.open();
    this.berichteService
      .getB004Uebersicht(abrechnungsmonat, mitarbeiter, sachbearbeiter, bearbeitungsstatus)
      .subscribe((b004Ergebnisse) => {
        this.tabelle.data = this.erstelleTabellenForm(b004Ergebnisse);
        this.anzahlErgebnisse = b004Ergebnisse.length;
        this.aktualisiereUeberschrift();
        this.aenderActionBarButtonKonfiguration();
        this.ladeBerichteSpinner.close();
      });
  }

  private aktualisiereUeberschrift() {
    this.ueberschrift = `Arbeitsnachweis pro Mitarbeiter:in und Abrechnungsmonat [${this.anzahlErgebnisse}]`;
  }

  private aenderActionBarButtonKonfiguration() {
    this.actionBarButtonKonfiguration.erstellePDFDeaktiviert = this.anzahlErgebnisse === 0;
    this.actionBarButtonKonfiguration.versendePDFDeaktiviert = this.anzahlErgebnisse === 0;
  }

  private setzeAktuellenAbrechnungsmonat() {
    const abrechnungsmonat: Abrechnungsmonat | undefined =
      this.abrechnungsmonatService.erhalteVorauswahlFuerAbrechnungsmonat(this.abrechnungsmonatAuswahl);
    this.berichtFilterFormGroup.get('abrechnungsmonat')?.setValue(abrechnungsmonat as Abrechnungsmonat);
  }

  private reagiereAufFilterFormAenderung() {
    this.berichtFilterFormGroup.valueChanges.subscribe(() => {
      if (!this.berichtFilterFormGroup.valid || !this.filterWerteHabenSichVeraendert()) {
        return;
      }
      this.aktualisiereLetzteFilterWerte();
      this.aktualisiereBerichtDaten(
        this.berichtFilterFormGroup.getRawValue().abrechnungsmonat,
        this.berichtFilterFormGroup.getRawValue().mitarbeiter,
        this.berichtFilterFormGroup.getRawValue().sachbearbeiter,
        this.berichtFilterFormGroup.getRawValue().bearbeitungsstatus
      );
    });
  }

  private filterWerteHabenSichVeraendert() {
    if (
      !this.sindAbrechnungsmonateGleich(
        this.berichtFilterFormGroup.getRawValue().abrechnungsmonat,
        this.letzteFilterValues.abrechnungsmonat
      )
    ) {
      return true;
    }

    if (
      !this.sindMitarbeiterGleich(
        this.berichtFilterFormGroup.getRawValue().mitarbeiter,
        this.letzteFilterValues.mitarbeiter
      )
    ) {
      return true;
    }
    if (
      !this.sindMitarbeiterGleich(
        this.berichtFilterFormGroup.getRawValue().sachbearbeiter,
        this.letzteFilterValues.sachbearbeiter
      )
    ) {
      return true;
    }
    if (this.berichtFilterFormGroup.getRawValue().bearbeitungsstatus !== this.letzteFilterValues.bearbeitungsstatus) {
      return true;
    }

    return false;
  }

  private aktualisiereLetzteFilterWerte() {
    this.letzteFilterValues = {
      abrechnungsmonat: this.berichtFilterFormGroup.getRawValue().abrechnungsmonat,
      mitarbeiter: this.berichtFilterFormGroup.getRawValue().mitarbeiter,
      sachbearbeiter: this.berichtFilterFormGroup.getRawValue().sachbearbeiter,
      bearbeitungsstatus: this.berichtFilterFormGroup.getRawValue().bearbeitungsstatus,
    };
  }

  private erstelleTabellenForm(b004Ergebnisse: ErgebnisB004Uebersicht[]): ErgebnisB004UebersichtTabellendarstellung[] {
    return b004Ergebnisse.map((b004Eintrag) => {
      const mitarbeiter: Mitarbeiter = getObjektAusListeDurchId(
        b004Eintrag.mitarbeiterId,
        this.mitarbeiterAuswahl
      ) as Mitarbeiter;
      const sachbearbeiter: Mitarbeiter = getObjektAusListeDurchId(
        b004Eintrag.sachbearbeiterId,
        this.mitarbeiterAuswahl
      ) as Mitarbeiter;
      return {
        mitarbeiter: `${mitarbeiter.nachname}, ${mitarbeiter.vorname}`,
        sachbearbeiter: `${sachbearbeiter.nachname}, ${sachbearbeiter.vorname}`,
        bearbeitungsstatus: this.statusIdZuBearbeitungsstatusEnumPipe.transform(b004Eintrag.statusId),
        projektstunden: b004Eintrag.projektstunden,
        reisezeit: b004Eintrag.reisezeit,
        sonderarbeitszeit: b004Eintrag.sonderarbeitszeit,
        rufbereitschaft: b004Eintrag.rufbereitschaft,
      };
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
}
