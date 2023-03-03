import { AfterViewChecked, AfterViewInit, ChangeDetectorRef, Component, ViewChild } from '@angular/core';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MitarbeiterService } from '../../../../shared/service/mitarbeiter.service';
import { forkJoin } from 'rxjs';
import { ArbeitsnachweisService } from '../../../../shared/service/arbeitsnachweis.service';
import { SpinnerOverlayService } from '../../../../shared/service/spinner-overlay.service';
import { AbrechnungsmonatService } from '../../../../shared/service/abrechnungsmonat.service';
import { BerichteService } from '../../../../shared/service/berichte.service';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { ErgebnisB002UebersichtTabellendarstellung } from '../../model/ergebnis-b002-uebersicht-tabellendarstellung';
import { ErgebnisB002Uebersicht } from '../../../../shared/model/berichte/ergebnis-b002-uebersicht';
import * as CompareUtil from '../../../../shared/util/compare.util';
import { BenachrichtigungService } from '../../../../shared/service/benachrichtigung.service';
import * as IconUtil from '../../../../shared/util/icon.util';
import { ActionBarButtonKonfiguration } from '../../model/action-bar-button-konfiguration';
import { CustomValidator } from '../../../../shared/validation/custom-validator';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';
import { AbrechnungsmonatAnzeigeNamePipe } from '../../../../shared/pipe/abrechnungsmonat-anzeige-name.pipe';
import { Abrechnungsmonat } from '../../../../shared/model/sonstiges/abrechnungsmonat';

@Component({
  selector: 'pab-berichte-b002',
  templateUrl: './berichte-b002.component.html',
  styleUrls: ['./berichte-b002.component.scss'],
})
export class BerichteB002Component implements AfterViewInit, AfterViewChecked {
  ueberschrift: string = '';
  hinweisText: string = 'Berichtsrelevant sind nur Arbeitsnachweise mit Status "abgerechnet" oder höher';
  ladeBerichteSpinner: SpinnerRef = new SpinnerRef();

  actionBarButtonKonfiguration: ActionBarButtonKonfiguration = {
    erstelleExcelDeaktiviert: false,
    erstellePDFDeaktiviert: true,
    versendePDFDeaktiviert: true,
  };

  berichtFilterFormGroup: FormGroup;
  mitarbeiterAuswahl: Mitarbeiter[] = [];
  sachbearbeiterAuswahl: Mitarbeiter[] = [];
  abrechnungsmonatAuswahl: Abrechnungsmonat[] = [];

  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  spalten = [
    { id: 'abrechnungsmonat', name: 'Abrechnungsmonat' },
    { id: 'mitarbeiterPersonalnummer', name: 'Personalnummer' },
    { id: 'mitarbeiterFullname', name: 'Mitarbeiter:in' },
    { id: 'mitarbeiterKurzname', name: 'Kürzel' },
    { id: 'sachbearbeiterFullname', name: 'Sachbearbeiter:in' },
  ];
  tabelle: TableData<ErgebnisB002UebersichtTabellendarstellung>;

  anzahlErgebnisse = 0;

  private letzteFilterValues = {
    abrechnungsmonat: {} as Abrechnungsmonat,
    mitarbeiter: {} as Mitarbeiter,
    sachbearbeiter: {} as Mitarbeiter,
  };

  constructor(
    private fb: FormBuilder,
    private mitarbeiterService: MitarbeiterService,
    private arbeitsnachweisService: ArbeitsnachweisService,
    private abrechnungsmonatService: AbrechnungsmonatService,
    private berichteService: BerichteService,
    private benachrichtigungsService: BenachrichtigungService,
    private changeDetectorRef: ChangeDetectorRef,
    private abrechnungsmonatAnzeigeNamePipe: AbrechnungsmonatAnzeigeNamePipe,
    private spinnerOverlayService: SpinnerOverlayService
  ) {
    this.berichtFilterFormGroup = this.fb.nonNullable.group({
      abrechnungsmonat: [{} as Abrechnungsmonat],
      mitarbeiter: [{} as Mitarbeiter, [CustomValidator.valueIstObjektOderLeererString()]],
      sachbearbeiter: [{} as Mitarbeiter],
    });

    this.tabelle = new TableData<ErgebnisB002UebersichtTabellendarstellung>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<ErgebnisB002UebersichtTabellendarstellung>(
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

  onLeereFilter() {
    this.berichtFilterFormGroup.reset();
    this.setzeAktuellenAbrechnungsmonat();
  }

  oeffneHinweisDialog() {
    this.benachrichtigungsService.erstelleInfoMessage(this.hinweisText);
  }

  sindAbrechnungsmonateGleich(abrechnungsmonat1: Abrechnungsmonat, abrechnungsmonat2: Abrechnungsmonat): boolean {
    return CompareUtil.sindAbrechnungsmonateGleich(abrechnungsmonat1, abrechnungsmonat2);
  }

  sindMitarbeiterGleich(mitarbeiter1: Mitarbeiter, mitarbeiter2: Mitarbeiter): boolean {
    return CompareUtil.sindMitarbeiterGleich(mitarbeiter1, mitarbeiter2);
  }

  getAbgeschlossenIcon(abrechnungsmonat: Abrechnungsmonat): string {
    return IconUtil.getAbgeschlossenIcon(abrechnungsmonat);
  }

  reagiereAufErstelleExcel() {
    const dataSpinner = {
      title: 'Excel Bericht wird erstellt',
    };
    const spinnerOverlay = this.spinnerOverlayService.open(dataSpinner);
    this.berichteService
      .erstelleUndOeffneB002AlsExcel(
        this.berichtFilterFormGroup.getRawValue().abrechnungsmonat,
        this.berichtFilterFormGroup.getRawValue().mitarbeiter,
        this.berichtFilterFormGroup.getRawValue().sachbearbeiter
      )
      .subscribe((excelBlob: Blob) => {
        this.berichteService.downloadFile(excelBlob, `B002_${this.berichteService.erstelleFileDateExtension()}.xlsx`);
        spinnerOverlay.close();
      });
  }

  private initialisiereTabelleUndSortierung() {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<ErgebnisB002UebersichtTabellendarstellung>(
      this.sort,
      true
    );
    this.tabelle.sortParams = ['sachbearbeiterFullname'];
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

      this.abrechnungsmonatService.sortiereAbrechnungsmonate(abrechnungsmonate);
      this.abrechnungsmonatAuswahl = abrechnungsmonate;
      this.setzeAktuellenAbrechnungsmonat();
    });
  }

  private aktualisiereBerichtDaten(
    abrechnungsmonat: Abrechnungsmonat,
    mitarbeiter?: Mitarbeiter,
    sachbearbeiter?: Mitarbeiter
  ) {
    if (!abrechnungsmonat || !abrechnungsmonat.jahr) {
      return;
    }
    this.ladeBerichteSpinner.open();
    this.berichteService
      .getB002Uebersicht(abrechnungsmonat, mitarbeiter, sachbearbeiter)
      .subscribe((b002Ergebnisse) => {
        this.tabelle.data = this.erstelleTabellenForm(b002Ergebnisse);
        this.anzahlErgebnisse = b002Ergebnisse.length;
        this.aktualisiereUeberschrift();
        this.aenderActionBarButtonKonfiguration();
        this.ladeBerichteSpinner.close();
      });
  }

  private aktualisiereUeberschrift() {
    this.ueberschrift = `B002 - Rufbereitschaft, Sonderarbeit pro Mitarbeiter:in, Projekt [${this.anzahlErgebnisse}]`;
  }

  private aenderActionBarButtonKonfiguration() {
    this.actionBarButtonKonfiguration.erstelleExcelDeaktiviert = this.anzahlErgebnisse === 0;
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
        this.berichtFilterFormGroup.getRawValue().sachbearbeiter
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

    return false;
  }

  private aktualisiereLetzteFilterWerte() {
    this.letzteFilterValues = {
      abrechnungsmonat: this.berichtFilterFormGroup.getRawValue().abrechnungsmonat,
      mitarbeiter: this.berichtFilterFormGroup.getRawValue().mitarbeiter,
      sachbearbeiter: this.berichtFilterFormGroup.getRawValue().sachbearbeiter,
    };
  }

  private erstelleTabellenForm(b002Ergebnisse: ErgebnisB002Uebersicht[]): ErgebnisB002UebersichtTabellendarstellung[] {
    return b002Ergebnisse.map((b002Eintrag) => {
      return {
        abrechnungsmonat: this.abrechnungsmonatAnzeigeNamePipe.transform(b002Eintrag),
        mitarbeiterPersonalnummer: b002Eintrag.mitarbeiterPersonalnummer,
        mitarbeiterFullname: `${b002Eintrag.mitarbeiterNachname}, ${b002Eintrag.mitarbeiterVorname}`,
        mitarbeiterKurzname: b002Eintrag.mitarbeiterKurzname,
        sachbearbeiterFullname: `${b002Eintrag.sachbearbeiterNachname}, ${b002Eintrag.sachbearbeiterVorname}`,
        mitarbeiterIstAktiv: b002Eintrag.mitarbeiterIstAktiv,
      };
    });
  }
}
