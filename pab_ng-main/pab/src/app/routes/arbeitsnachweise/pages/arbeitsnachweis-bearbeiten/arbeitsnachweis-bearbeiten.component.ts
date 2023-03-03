import { AfterViewChecked, AfterViewInit, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { Belegart } from '../../../../shared/model/konstanten/belegart';
import { StatusListen } from '../../../../shared/model/sonstiges/status-listen';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { SmartphoneBesitzArtenEnum } from '../../../../shared/enum/smartphone-besitz-arten.enum';
import { MitarbeiterService } from '../../../../shared/service/mitarbeiter.service';
import { ArbeitsnachweisService } from '../../../../shared/service/arbeitsnachweis.service';
import { CustomValidatorArbeitsnachweis } from '../../validation/custom-validator-arbeitsnachweis';
import { Arbeitsnachweis } from '../../../../shared/model/arbeitsnachweis/arbeitsnachweis';
import { StatusIdZuBearbeitungsstatusEnumPipe } from '../../../../shared/pipe/status-id-zu-bearbeitungsstatus-enum.pipe';
import { BearbeitungsstatusEnum } from '../../../../shared/enum/bearbeitungsstatus.enum';
import { BenachrichtigungService } from '../../../../shared/service/benachrichtigung.service';
import { ProjektService } from '../../../../shared/service/projekt.service';
import { KonstantenService } from '../../../../shared/service/konstanten.service';
import { ProjektstundeTyp } from '../../../../shared/model/konstanten/projektstunde-typ';
import { ArbeitsnachweisAbrechnung } from '../../../../shared/model/arbeitsnachweis/arbeitsnachweis-abrechnung';
import { ActivatedRoute } from '@angular/router';
import { filter, forkJoin } from 'rxjs';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { ArbeitsnachweisBearbeitenQueryParams } from '../../../../shared/model/query-params/arbeitsnachweis-bearbeiten-query-params';
import { arbeitsnachweisAbrechnungLeer } from '../../data/arbeitsnachweis-abrechnung-leer';
import { LohnartberechnungLog } from '../../../../shared/model/arbeitsnachweis/lohnartberechnung-log';
import { LohnartZuordnung } from '../../../../shared/model/arbeitsnachweis/lohnart-zuordnung';
import { Lohnart } from '../../../../shared/model/konstanten/lohnart';
import { MitarbeiterAbrechnungsmonat } from '../../../../shared/model/mitarbeiter/mitarbeiter-abrechnungsmonat';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';
import { Kunde } from '../../../../shared/model/kunde/kunde';
import { KundeService } from '../../../../shared/service/kunde.service';
import { Projektstunde } from '../../../../shared/model/arbeitsnachweis/projektstunde';
import { convertUsNummerZuDeString } from '../../../../shared/util/nummer-converter.util';
import { DreiMonatsRegel } from '../../../../shared/model/arbeitsnachweis/drei-monats-regel';
import { SpinnerOverlayService } from '../../../../shared/service/spinner-overlay.service';
import { MatDialog } from '@angular/material/dialog';
import {
  ArbeitsnachweisBearbeitenAnwImportData,
  ArbeitsnachweisBearbeitenAnwImportDialogComponent,
} from '../../components/arbeitsnachweis-bearbeiten-anw-import-dialog/arbeitsnachweis-bearbeiten-anw-import-dialog.component';
import { Fehlerlog } from '../../../../shared/model/arbeitsnachweis/fehlerlog';
import { ImportFehlerklassenEnum } from '../../../../shared/enum/import-fehlerklassen.enum';
import {
  ArbeitsnachweisBearbeitenUploadFehlerlogDialogComponent,
  ArbeitsnachweisBearbeitenUploadFehlerlogDialogData,
} from '../../components/arbeitsnachweis-bearbeiten-upload-fehlerlog-dialog/arbeitsnachweis-bearbeiten-upload-fehlerlog-dialog.component';
import { ExcelImportErgebnis } from '../../../../shared/model/arbeitsnachweis/excel-import-ergebnis';
import { Beleg } from '../../../../shared/model/arbeitsnachweis/beleg';
import { Abwesenheit } from '../../../../shared/model/arbeitsnachweis/abwesenheit';
import { AbrechnungsmonatService } from '../../../../shared/service/abrechnungsmonat.service';
import { MitarbeiterStundenkontoQueryParams } from '../../../../shared/model/query-params/mitarbeiter-stundenkonto-query-params';
import { NavigationService } from '../../../../shared/service/navigation.service';
import { SpinnerOverlayRef } from '../../../../shared/component/spinner-overlay/spinner-overlay-ref';
import {
  EmailDialogComponent,
  EmailDialogData,
} from '../../../../shared/component/email-dialog/email-dialog.component';
import { MitarbeiterQuittungEmailDaten } from '../../../../shared/model/arbeitsnachweis/mitarbeiter-quittung-email-daten';
import { BerichteService } from '../../../../shared/service/berichte.service';
import { ArbeitsnachweisBearbeitenBelegeImportDialogComponent } from '../../components/arbeitsnachweis-bearbeiten-belege-import-dialog/arbeitsnachweis-bearbeiten-belege-import-dialog.component';
import { CustomValidator } from '../../../../shared/validation/custom-validator';
import {
  ArbeitsnachweisBearbeitenTabAbrechnungLohnartDialogData,
  ArbeitsnachweisBearbeitenTabAbrechnungLohnartenzuordnungDialogComponent,
} from '../../components/arbeitsnachweis-bearbeiten-tab-abrechnung-lohnartenzuordnung-dialog/arbeitsnachweis-bearbeiten-tab-abrechnung-lohnartenzuordnung-dialog.component';
import { LohnartZuordnungTabellendarstellung } from '../../model/lohnart-zuordnung-tabellendarstellung';
import {
  ArbeitsnachweisBearbeitenTabAbrechnungLohnartberechnungLogDialogData,
  ArbeitsnachweisBearbeitenTabAbrechnungLohnartenberechnungLogDialogComponent,
} from '../../components/arbeitsnachweis-bearbeiten-tab-abrechnung-lohnartenberechnung-log-dialog/arbeitsnachweis-bearbeiten-tab-abrechnung-lohnartenberechnung-log-dialog.component';
import {
  ArbeitsnachweisBearbeitenTabAbwesenheitDreimonatsregelDialogData,
  ArbeitsnachweisBearbeitenTabAbwesenheitenDreimonatsregelDialogComponent,
} from '../../components/arbeitsnachweis-bearbeiten-tab-abwesenheiten-dreimonatsregel-dialog/arbeitsnachweis-bearbeiten-tab-abwesenheiten-dreimonatsregel-dialog.component';
import { SpeichernPostAktionEnum } from '../../../../shared/enum/speichern-post-aktion.enum';

@Component({
  selector: 'pab-arbeitsnachweis-bearbeiten',
  templateUrl: './arbeitsnachweis-bearbeiten.component.html',
  styleUrls: ['./arbeitsnachweis-bearbeiten.component.scss'],
})
export class ArbeitsnachweisBearbeitenComponent implements OnInit, AfterViewInit, AfterViewChecked {
  // Initial sind alle Felder gesperrt, bis ein entsprechender ANW geladen ist
  aktuellerArbeitsnachweis: Arbeitsnachweis | undefined = undefined;
  aktuellerMitarbeiter: Mitarbeiter | undefined = undefined;
  ladeArbeitsnachweisSpinner: SpinnerRef = new SpinnerRef();
  ladeAbrechnungSpinner: SpinnerRef = new SpinnerRef();
  ausgewaehlterTab: number = 0;

  mitarbeiterAuswahl: Mitarbeiter[] = [];
  sachbearbeiterAuswahl: Mitarbeiter[] = [];
  abrechnungsmonatAuswahl: MitarbeiterAbrechnungsmonat[] = [];
  projektAuswahl: Projekt[] = [];
  belegartenAuswahl: Belegart[] = [];
  kundeAuswahl: Kunde[] = [];
  einsatzortAuswahl: string[] = [];
  projektstundeTypAuswahl: ProjektstundeTyp[] = [];
  lohnarten: Lohnart[] = [];
  angerechneteReisezeit: number = 0;
  lohnartZuordnungen: LohnartZuordnung[] = [];
  lohnartberechnungLog: LohnartberechnungLog[] = [];
  dreiMonatsRegeln: DreiMonatsRegel[] = [];

  anwAuswahlFormGroup: FormGroup;

  projektstundenFormGroup: FormGroup;
  projektstundenListen: StatusListen;

  belegeFormGroup: FormGroup;
  smartphoneAuswahlFormGroup: FormGroup;
  belegeListen: StatusListen;
  dmsBelegUrl: string = '';

  abwesenheitenFormGroup: FormGroup;
  abwesenheitsListen: StatusListen;

  sonderzeitenFormGroup: FormGroup;

  abrechnungAuszahlungFormGroup: FormGroup;
  arbeitsnachweisAbrechnung: ArbeitsnachweisAbrechnung = arbeitsnachweisAbrechnungLeer;

  private queryParams: ArbeitsnachweisBearbeitenQueryParams | undefined = undefined;

  constructor(
    private fb: FormBuilder,
    private changeDetectorRef: ChangeDetectorRef,
    private route: ActivatedRoute,
    private mitarbeiterService: MitarbeiterService,
    private arbeitsnachweisService: ArbeitsnachweisService,
    private berichteService: BerichteService,
    private abrechnungsmonatService: AbrechnungsmonatService,
    private projektService: ProjektService,
    private konstantenService: KonstantenService,
    private benachrichtigungService: BenachrichtigungService,
    private kundenService: KundeService,
    private spinnerOverlayService: SpinnerOverlayService,
    private navigationService: NavigationService,
    public dialog: MatDialog,
    private statusIdZuBearbeitungsstatusEnumPipe: StatusIdZuBearbeitungsstatusEnumPipe
  ) {
    this.anwAuswahlFormGroup = this.fb.nonNullable.group({
      mitarbeiter: [{} as Mitarbeiter],
      abrechnungsmonat: [{} as MitarbeiterAbrechnungsmonat],
      vorherigerAbrechnungsmonat: [{} as MitarbeiterAbrechnungsmonat],
      internExtern: [{ value: '', disabled: true }],
      geschaeftsstelle: [{ value: '', disabled: true }],
      sachbearbeiter: [{ value: '', disabled: true }],
    });

    this.projektstundenFormGroup = this.fb.nonNullable.group({
      id: [''],
      tag: ['', [Validators.required, CustomValidatorArbeitsnachweis.tagIstInMonat(this.anwAuswahlFormGroup)]],
      stunden: ['', Validators.required],
      projekt: [{} as Projekt, [Validators.required, CustomValidator.objektIstNichtLeer()]],
      bemerkung: [''],
      nichtFakturierfaehig: [false],
    });

    this.belegeFormGroup = this.fb.nonNullable.group({
      id: [''],
      tag: ['', [Validators.required, CustomValidatorArbeitsnachweis.tagIstInMonat(this.anwAuswahlFormGroup)]],
      projekt: [{} as Projekt, [Validators.required, CustomValidator.objektIstNichtLeer()]],
      belegart: [{} as Belegart, [Validators.required, CustomValidator.objektIstNichtLeer()]],
      betrag: [''],
      kilometer: [''],
      einsatzort: [''],
      bemerkung: [''],
      belegNr: [''],
    });

    this.smartphoneAuswahlFormGroup = this.fb.nonNullable.group({
      smartphoneBesitzArt: SmartphoneBesitzArtenEnum.KEIN,
    });

    this.abwesenheitenFormGroup = this.fb.nonNullable.group(
      {
        id: [''],
        tagVon: ['', [Validators.required, CustomValidatorArbeitsnachweis.tagIstInMonat(this.anwAuswahlFormGroup)]],
        hhVon: ['', [Validators.required, Validators.max(24)]],
        mmVon: ['', [Validators.required, Validators.max(60)]],
        tagBis: ['', [Validators.required, CustomValidatorArbeitsnachweis.tagIstInMonat(this.anwAuswahlFormGroup)]],
        hhBis: ['', [Validators.required, Validators.max(24)]],
        mmBis: ['', [Validators.required, Validators.max(60)]],
        projekt: [{} as Projekt, [Validators.required, CustomValidator.objektIstNichtLeer()]],
        einsatzort: ['', Validators.required],
        bemerkung: [''],
        dreimonatsregelAktiv: [false],
        mitUebernachtung: [false],
        mitFruehstueck: [false],
        mitMitagessen: [false],
        mitAbendessen: [false],
      },
      { validators: CustomValidatorArbeitsnachweis.zeitpunktBisIstNachVon(this.anwAuswahlFormGroup) }
    );

    this.sonderzeitenFormGroup = this.fb.nonNullable.group(
      {
        id: [''],
        projektstundeTyp: ['', Validators.required],
        projekt: [{} as Projekt, [Validators.required, CustomValidator.objektIstNichtLeer()]],
        tagVon: ['', [Validators.required, CustomValidatorArbeitsnachweis.tagIstInMonat(this.anwAuswahlFormGroup)]],
        hhVon: ['', [Validators.required, Validators.max(24)]],
        mmVon: ['', [Validators.required, Validators.max(60)]],
        tagBis: ['', [Validators.required, CustomValidatorArbeitsnachweis.tagIstInMonat(this.anwAuswahlFormGroup)]],
        hhBis: ['', [Validators.required, Validators.max(24)]],
        mmBis: ['', [Validators.required, Validators.max(60)]],
        stunden: [
          '',
          [Validators.required, CustomValidatorArbeitsnachweis.sonderzeitenAbhanegigesMaxNichtUeberschritten()],
        ],
        bemerkung: [''],
      },
      { validators: CustomValidatorArbeitsnachweis.zeitpunktBisIstNachVon(this.anwAuswahlFormGroup) }
    );

    this.abrechnungAuszahlungFormGroup = this.fb.nonNullable.group({
      sollstunden: '0',
      auszahlung: '0',
    });

    this.projektstundenListen = new StatusListen([]);
    this.belegeListen = new StatusListen([]);
    this.abwesenheitsListen = new StatusListen([]);
  }

  ngOnInit(): void {
    this.reagiereAufAenderungMitarbeiter();
    this.reagiereAufAenderungAbrechnungsmonat();
    this.reagiereAufAenderungenSmartphoneAuswahl();
  }

  ngAfterViewInit(): void {
    this.rufeInitialeDatenAb();
  }

  ngAfterViewChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  reagiereAufReisezeitAktualisiertEvent() {
    this.arbeitsnachweisService
      .berechneAngerechneteReisezeitFuerAlleProjektstunden(
        this.filtereProjektstundenNachProjektstundeTyp(
          this.projektstundenListen.getAnzeigeListe() as Projektstunde[],
          this.konstantenService.getProjektstundenTypTatsaechlicheReise()
        )
      )
      .subscribe((angerechneteReisezeit: Projektstunde[]) => {
        this.angerechneteReisezeit = angerechneteReisezeit.reduce((sum, p) => sum + p.anzahlStunden, 0);
      });
  }

  pruefeUndReagiereAufAbrechnungsAuswahl(tabIndex: number) {
    if (tabIndex === 4 && this.aktuellerArbeitsnachweis) {
      this.aktualisiereArbeitsnachweisAbrechnung();
    }
  }

  reagiereAufAuszahlungUndSonderstundenAktualisiertEvent() {
    if (this.aktuellerArbeitsnachweis) {
      this.aktuellerArbeitsnachweis.auszahlung = this.arbeitsnachweisAbrechnung.auszahlung;
      this.aktuellerArbeitsnachweis.sollstunden = this.arbeitsnachweisAbrechnung.sollstunden;
    }
  }

  oeffneStundenkonto() {
    if (!this.aktuellerMitarbeiter) {
      return;
    }

    // Prüfe, ob Änderungen existieren und warne den Anwender ggf.
    const queryParams: MitarbeiterStundenkontoQueryParams = {
      mitarbeiterId: this.aktuellerMitarbeiter?.id,
    };
    const url: string = '/mitarbeiter/stundenkonto';
    if (this.wurdeArbeitsnachweisBearbeitet()) {
      const titel: string = 'Änderungen verwerfen?';
      const message: string = 'Sollen die ungesicherten Änderungen verworfen werden?';
      const dialogRef = this.benachrichtigungService.erstelleBestaetigungMessage(message, titel);
      dialogRef.afterClosed().subscribe((result) => {
        if (!result) {
          return;
        }
        this.navigationService.navigiereZuMitQueryParams(url, false, queryParams);
      });
    } else {
      this.navigationService.navigiereZuMitQueryParams(url, false, queryParams);
    }
  }

  reagiereAufSpeichernEvent(speichernPostAktion: SpeichernPostAktionEnum) {
    const dataSpinner = {
      title: 'Änderungen werden gespeichert...',
    };
    const spinnerOverlay = this.spinnerOverlayService.open(dataSpinner);
    this.arbeitsnachweisService
      .speichereArbeitsnachweis(
        this.aktuellerArbeitsnachweis!,
        this.anwAuswahlFormGroup.get('mitarbeiter')!.value.id,
        this.projektstundenListen,
        this.belegeListen,
        this.abwesenheitsListen
      )
      .subscribe((speichernResponse) => {
        const abrechnungsmonatAktualisiert = {
          ...this.anwAuswahlFormGroup.getRawValue().abrechnungsmonat,
          arbeitsnachweisId: speichernResponse.arbeitsnachweis.id,
        };
        this.ladeArbeitsnachweisUndSetzeInMaske(abrechnungsmonatAktualisiert);

        this.abrechnungsmonatService.ersetzeAbrechnungsmonat(
          this.abrechnungsmonatAuswahl,
          abrechnungsmonatAktualisiert
        );

        this.anwAuswahlFormGroup
          .get('abrechnungsmonat')
          ?.setValue(abrechnungsmonatAktualisiert as MitarbeiterAbrechnungsmonat, { emitEvent: false });

        spinnerOverlay.close();
        this.benachrichtigungService.erstelleBenachrichtigung('Arbeitsnachweis wurde erfolgreich gespeichert.');
        this.fuehreAktionNachSpeichernAus(speichernPostAktion);
      });
  }

  istAktuellerArbeitsnachweisOffen(): boolean {
    if (!this.aktuellerArbeitsnachweis) {
      return false;
    }
    return (
      this.statusIdZuBearbeitungsstatusEnumPipe.transform(this.aktuellerArbeitsnachweis.statusId) !==
      BearbeitungsstatusEnum.ABGESCHLOSSEN
    );
  }

  istFehlerlogVorhanden(): boolean {
    return !!(
      this.aktuellerArbeitsnachweis &&
      this.aktuellerArbeitsnachweis.id &&
      this.aktuellerArbeitsnachweis.fehlerlog
    );
  }

  istAenderungVorhanden(): boolean {
    // Gewisse Formen wie z.B. für Belege werden bewusst ignoriert, da Eingaben hier solange keine
    // Änderung darstellen, bis diese der StatusListe hinzugefügt werden.
    return (
      this.projektstundenListen.istVeraendert() ||
      this.smartphoneAuswahlFormGroup.touched ||
      this.belegeListen.istVeraendert() ||
      this.abwesenheitsListen.istVeraendert() ||
      this.abrechnungAuszahlungFormGroup.touched
    );
  }

  reagiereAufAnwImportierenEvent() {
    const dataAnwImport: ArbeitsnachweisBearbeitenAnwImportData = {
      validierungsModus: false,
    };
    const anwImportDialogRef = this.dialog.open(ArbeitsnachweisBearbeitenAnwImportDialogComponent, {
      data: dataAnwImport,
      panelClass: 'dialog-container-small',
    });

    anwImportDialogRef.componentInstance.anwHochgeladenEvent.subscribe((ausgewaehlterANW: File) => {
      const dataSpinner = {
        title: 'Arbeitsnachweis wird verarbeitet...',
      };
      const spinnerOverlay = this.spinnerOverlayService.open(dataSpinner);
      this.arbeitsnachweisService
        .verarbeiteArbeitsnachweisImport(ausgewaehlterANW, this.anwAuswahlFormGroup.getRawValue().mitarbeiter)
        .subscribe((excelImportErgebnis) => {
          anwImportDialogRef.close();

          // Prüfe auf Fehler, anzeigen und return
          if (this.pruefeObFehlerExistiertUndZeigeWarnungen(excelImportErgebnis.fehlerlog, spinnerOverlay)) {
            return;
          }
          // Prüfe, ob in anderen Abrechnungsmonat gesprungen werden würde und öffne dialog wenn
          if (this.aktuellerAbrechnungsmonatUngleichAnwAbrechnungsmonat(excelImportErgebnis.arbeitsnachweis)) {
            const titel: string = 'Abrechnungsmonat wechseln?';
            const message: string = `Es wird in den Abrechnungsmonat ${excelImportErgebnis.arbeitsnachweis.jahr}/${excelImportErgebnis.arbeitsnachweis.monat} gewechselt.
Ungesicherte Änderungen werden dabei verworfen. Fortfahren?`;
            const dialogRef = this.benachrichtigungService.erstelleBestaetigungMessage(message, titel);
            dialogRef.afterClosed().subscribe((result) => {
              if (result) {
                // Nur bei Bestätigung werden Änderungen durchgeführt
                this.speichereWarnungenInArbeitsnachweisUndZeigeAn(excelImportErgebnis);
                this.fuehreArbeitsnachweisWechselNachExcelImportDurch(excelImportErgebnis);
              }
              spinnerOverlay.close();
            });
            return;
          } else {
            spinnerOverlay.close();
            this.speichereWarnungenInArbeitsnachweisUndZeigeAn(excelImportErgebnis);
            this.fuehreArbeitsnachweisWechselNachExcelImportDurch(excelImportErgebnis);
          }
        });
    });
  }

  reagiereAufBelegImportierenEvent() {
    const belegImportDialogRef = this.dialog.open(ArbeitsnachweisBearbeitenBelegeImportDialogComponent, {
      panelClass: 'dialog-container-small',
    });

    belegImportDialogRef.componentInstance.belegHochgeladenEvent.subscribe((ausgewaehlterBeleg: File) => {
      const dataSpinner = {
        title: 'Beleg wird an das DMS übertragen...',
      };
      const spinnerOverlay = this.spinnerOverlayService.open(dataSpinner);
      this.arbeitsnachweisService
        .verarbeiteBelegImport(ausgewaehlterBeleg, this.aktuellerArbeitsnachweis!)
        .subscribe((belegUploadErgebnis) => {
          belegImportDialogRef.close();

          // Prüfe auf Fehler, anzeigen und return
          spinnerOverlay.close();
          if (belegUploadErgebnis.fehlerhaft && belegUploadErgebnis.message) {
            this.benachrichtigungService.erstelleErrorMessage(belegUploadErgebnis.message);
            return;
          }

          this.benachrichtigungService.erstelleBenachrichtigung('Beleg erfolgreich übertragen!');
        });
    });
  }

  reagiereAufArbeitsnachweisAbrechnenEvent() {
    if (
      !this.abrechnungsmonatService.istMonatAktuellerAbrechnungsmonat(
        this.anwAuswahlFormGroup.getRawValue().abrechnungsmonat
      )
    ) {
      const aktuellerAbrechnungsmonat = this.abrechnungsmonatService.getAktuellenAbrechnungsmonat();
      this.benachrichtigungService.erstelleWarnung(
        `Aktuell dürfen nur Arbeitsnachweise des Abrechnungsmonats ${aktuellerAbrechnungsmonat.jahr}/${aktuellerAbrechnungsmonat.monat}  abgerechnet werden`
      );
      return;
    }
    if (!this.aktuellerArbeitsnachweis) {
      return;
    }
    const arbeitsnachweis = this.aktuellerArbeitsnachweis;
    this.arbeitsnachweisService.rechneArbeitsnachweisAb(this.aktuellerArbeitsnachweis).subscribe(() => {
      this.ladeArbeitsnachweisUndSetzeInMaske(this.anwAuswahlFormGroup.getRawValue().abrechnungsmonat);

      if (this.aktuellerMitarbeiter?.intern) {
        this.emailFuerInterneMitarbeiterSchicken(arbeitsnachweis);
      }
    });
  }

  pruefeUndReagiereAufProjektstundenEingabe() {
    this.pruefeUndReagiereAufSonderzeitenEingabe();
    this.arbeitsnachweisService.pruefeUndWarneBeiProjektstundenInkosistenz(
      this.anwAuswahlFormGroup.getRawValue().mitarbeiter.id,
      this.anwAuswahlFormGroup.getRawValue().abrechnungsmonat,
      this.projektstundenListen.getAnzeigeListe() as Projektstunde[]
    );
  }

  pruefeUndReagiereAufSonderzeitenEingabe() {
    this.arbeitsnachweisService.pruefeUndWarneBeiSonderzeitenInkosistenz(
      this.projektstundenListen.getAnzeigeListe() as Projektstunde[]
    );
  }

  zeigeDreimonatsRegeln() {
    const data: ArbeitsnachweisBearbeitenTabAbwesenheitDreimonatsregelDialogData = {
      dreiMonatsRegeln: this.dreiMonatsRegeln,
      kundeAuswahl: this.kundeAuswahl,
      mitarbeiter: this.aktuellerMitarbeiter,
    };
    this.dialog.open(ArbeitsnachweisBearbeitenTabAbwesenheitenDreimonatsregelDialogComponent, {
      data,
      panelClass: 'dialog-container-default',
    });
  }

  oeffneLohnartenDialog() {
    const data: ArbeitsnachweisBearbeitenTabAbrechnungLohnartDialogData = {
      lohnartZuordnungen: this.lohnartZuordnungen,
      lohnarten: this.lohnarten,
    };

    const lohnartDialogRef = this.dialog.open(ArbeitsnachweisBearbeitenTabAbrechnungLohnartenzuordnungDialogComponent, {
      data,
      panelClass: 'dialog-container-default',
    });

    lohnartDialogRef.componentInstance.lohnartZuordnungKlick.subscribe(
      (lohnartZuordnung: LohnartZuordnungTabellendarstellung) => {
        this.oeffneLohnartenberechnungLogDialog(lohnartZuordnung);
      }
    );
  }

  oeffneFehlerlogDialog(fehlerlog: Fehlerlog[]) {
    const data: ArbeitsnachweisBearbeitenUploadFehlerlogDialogData = {
      fehlerlog: fehlerlog,
    };

    this.dialog.open(ArbeitsnachweisBearbeitenUploadFehlerlogDialogComponent, {
      data,
      panelClass: 'dialog-container-default',
    });
  }

  private oeffneLohnartenberechnungLogDialog(lohnartZuordnung: LohnartZuordnungTabellendarstellung) {
    const data: ArbeitsnachweisBearbeitenTabAbrechnungLohnartberechnungLogDialogData = {
      lohnartberechnungLog: this.lohnartberechnungLog.filter((lohnartberechnung) => {
        return lohnartberechnung.konto === lohnartZuordnung.konto;
      }),
      konto: lohnartZuordnung.konto,
      monat: this.aktuellerArbeitsnachweis ? this.aktuellerArbeitsnachweis.monat : 0,
      jahr: this.aktuellerArbeitsnachweis ? this.aktuellerArbeitsnachweis.jahr : 0,
      mitarbeiterVorname: this.aktuellerMitarbeiter ? this.aktuellerMitarbeiter.vorname : '',
      mitarbeiterNachname: this.aktuellerMitarbeiter ? this.aktuellerMitarbeiter.nachname : '',
    };

    this.dialog.open(ArbeitsnachweisBearbeitenTabAbrechnungLohnartenberechnungLogDialogComponent, {
      data,
      panelClass: 'dialog-container-default',
    });
  }

  private fuehreAktionNachSpeichernAus(speichernPostAktion: SpeichernPostAktionEnum) {
    switch (speichernPostAktion) {
      case SpeichernPostAktionEnum.KEINE:
        break;
      case SpeichernPostAktionEnum.SCHLIESSEN:
        this.navigationService.geheZurueck();
        break;
      case SpeichernPostAktionEnum.NEU:
        this.navigationService.initialisiereRoute('/arbeitsnachweis/bearbeiten');
        break;
    }
  }

  private filtereProjektstundenNachProjektstundeTyp(
    projektstunden: Projektstunde[],
    projektstundeTyp: ProjektstundeTyp
  ) {
    return projektstunden.filter(
      (projektstunde: Projektstunde) => projektstunde.projektstundeTypId === projektstundeTyp.id
    );
  }

  private rufeInitialeDatenAb(): void {
    forkJoin([
      this.mitarbeiterService.getMitarbeiterAuswahlFuerArbeitsnachweisBearbeiten(),
      this.mitarbeiterService.getAlleSachbearbeiter(),
      this.projektService.getAlleProjekte(),
      this.kundenService.getAlleKunden(),
      this.konstantenService.getStadtAlle(),
      this.konstantenService.getBelegartenAll(),
      this.konstantenService.getProjektstundeTypAll(),
      this.konstantenService.getLohnartAll(),
    ]).subscribe(
      ([mitarbeiter, sachbearbeiter, projekte, kunden, einsatzorte, belegarten, projektstundeTypen, lohnarten]) => {
        this.mitarbeiterAuswahl = mitarbeiter.slice();
        this.sachbearbeiterAuswahl = sachbearbeiter.slice();
        this.projektAuswahl = projekte.slice();
        this.kundeAuswahl = kunden.slice();
        this.einsatzortAuswahl = einsatzorte.slice();
        this.belegartenAuswahl = belegarten.slice();
        this.projektstundeTypAuswahl = projektstundeTypen.slice();
        this.lohnarten = lohnarten.slice();
        this.verarbeiteQueryParameter();
      }
    );
  }

  private verarbeiteQueryParameter(): void {
    this.route.queryParams.subscribe((params) => {
      if (!params['jahr']) {
        return;
      }
      this.queryParams = {
        jahr: parseInt(params['jahr']),
        monat: parseInt(params['monat']),
        mitarbeiterId: params['mitarbeiterId'],
        belegeTab: params['belegeTab'],
      };
      const mitarbeiter: Mitarbeiter = getObjektAusListeDurchId(
        this.queryParams.mitarbeiterId,
        this.mitarbeiterAuswahl
      ) as Mitarbeiter;
      this.anwAuswahlFormGroup.get('mitarbeiter')!.setValue(mitarbeiter);

      if (this.queryParams.belegeTab) {
        this.ausgewaehlterTab = 1;
      }
    });
  }

  private reagiereAufAenderungMitarbeiter(): void {
    this.anwAuswahlFormGroup
      .get('mitarbeiter')
      ?.valueChanges.pipe(filter((mitarbeiter) => mitarbeiter === '' || typeof mitarbeiter !== 'string'))
      .subscribe((mitarbeiter) => {
        this.ladeArbeitsnachweisSpinner.open();
        this.datenZuruecksetzenBeiWechselMitarbeiter();
        if (!mitarbeiter || (mitarbeiter && !mitarbeiter.id)) {
          this.anwAuswahlFormGroup.get('abrechnungsmonat')?.reset();
          this.ladeArbeitsnachweisSpinner.close();
          return;
        }

        this.aktuellerMitarbeiter = mitarbeiter;
        this.arbeitsnachweisService
          .getAbrechnungsmonateByMitarbeiter(mitarbeiter)
          .subscribe((abrechnungsmonate: MitarbeiterAbrechnungsmonat[]) => {
            this.abrechnungsmonatService.ergaenzeFehlendenAbrechnungsmonateMitDummy(abrechnungsmonate);
            this.abrechnungsmonatService.sortiereAbrechnungsmonate(abrechnungsmonate);
            this.setzeVorauswahlFuerAbrechnungsmonat(abrechnungsmonate);
            this.abrechnungsmonatAuswahl = abrechnungsmonate.slice();
            this.ladeArbeitsnachweisSpinner.close();
          });
      });
  }

  private setzeVorauswahlFuerAbrechnungsmonat(abrechnungsmonate: MitarbeiterAbrechnungsmonat[]): void {
    const abrechnungsmonat: MitarbeiterAbrechnungsmonat | undefined =
      this.abrechnungsmonatService.erhalteVorauswahlFuerAbrechnungsmonat(abrechnungsmonate, this.queryParams);
    if (this.queryParams) {
      this.queryParams = undefined;
    }
    if (!abrechnungsmonat) {
      return;
    }
    this.anwAuswahlFormGroup.get('abrechnungsmonat')?.setValue(abrechnungsmonat as MitarbeiterAbrechnungsmonat);
  }

  private reagiereAufAenderungenSmartphoneAuswahl() {
    this.smartphoneAuswahlFormGroup
      .get('smartphoneBesitzArt')
      ?.valueChanges.subscribe((smartphoneBesitzArt: string) => {
        if (!this.aktuellerArbeitsnachweis) {
          return;
        }
        this.aktuellerArbeitsnachweis.smartphoneEigen =
          smartphoneBesitzArt === SmartphoneBesitzArtenEnum.KEIN
            ? null
            : smartphoneBesitzArt === SmartphoneBesitzArtenEnum.EIGEN;
      });
  }

  private reagiereAufAenderungAbrechnungsmonat(): void {
    this.anwAuswahlFormGroup
      .get('abrechnungsmonat')
      ?.valueChanges.subscribe((abrechnungsmonat: MitarbeiterAbrechnungsmonat) => {
        const vorherigerAbrechnungsmonat: MitarbeiterAbrechnungsmonat =
          this.anwAuswahlFormGroup.get('vorherigerAbrechnungsmonat')!.value;
        // Wenn Abrechnungsmonat nicht verändert, muss auch nichts getan werden
        if (
          vorherigerAbrechnungsmonat.monat === abrechnungsmonat.monat &&
          vorherigerAbrechnungsmonat.jahr === abrechnungsmonat.jahr
        ) {
          return;
        }

        // Prüfe, ob Änderungen existieren und warne den Anwender ggf.
        if (this.wurdeArbeitsnachweisBearbeitet()) {
          const titel: string = 'Änderungen verwerfen?';
          const message: string = 'Sollen die ungesicherten Änderungen verworfen werden?';
          const dialogRef = this.benachrichtigungService.erstelleBestaetigungMessage(message, titel);
          dialogRef.afterClosed().subscribe((result) => {
            if (!result) {
              // Vorherigen Abrechnungsmonat wiederherstellen
              this.anwAuswahlFormGroup.get('abrechnungsmonat')?.setValue(vorherigerAbrechnungsmonat);
              return;
            }
            // Neuen Abrechnungsmonat ablegen und Wechsel durchführen
            this.anwAuswahlFormGroup.get('vorherigerAbrechnungsmonat')?.setValue(abrechnungsmonat);
            this.ladeArbeitsnachweisUndSetzeInMaske(abrechnungsmonat);
          });
          return;
        }

        // Keine Änderungen, neuen Abrechnungsmonat ablegen und Wechsel durchführen
        this.anwAuswahlFormGroup.get('vorherigerAbrechnungsmonat')?.setValue(abrechnungsmonat);
        this.ladeArbeitsnachweisUndSetzeInMaske(abrechnungsmonat);
      });
  }

  private wurdeArbeitsnachweisBearbeitet(): boolean {
    return (
      this.projektstundenFormGroup.dirty ||
      this.belegeFormGroup.dirty ||
      this.smartphoneAuswahlFormGroup.dirty ||
      this.abwesenheitenFormGroup.dirty ||
      this.abrechnungAuszahlungFormGroup.dirty ||
      this.projektstundenListen.istVeraendert() ||
      this.belegeListen.istVeraendert() ||
      this.abwesenheitsListen.istVeraendert()
    );
  }

  private ladeArbeitsnachweisUndSetzeInMaske(abrechnungsmonat: MitarbeiterAbrechnungsmonat): void {
    this.ladeArbeitsnachweisSpinner.open();

    // Erstmal alles wegwerfen
    this.datenZuruecksetzenBeiWechselArbeitsnachweis();

    // Kein Monat ausgewählt
    if (!abrechnungsmonat || !abrechnungsmonat.monat) {
      this.ladeArbeitsnachweisSpinner.close();
      return;
    }

    if (!abrechnungsmonat.arbeitsnachweisId) {
      // Dummy-Monat ausgewählt -> Dummy-Arbeitsnachweis erzeugen und laden
      const dummyArbeitsnachweis: Arbeitsnachweis = this.erstelleDummyArbeitsnachweis(abrechnungsmonat);
      this.geladenenArbeitsnachweisVerarbeiten(dummyArbeitsnachweis);
      // Abrechnung explizit triggern
      this.aktualisiereArbeitsnachweisAbrechnung();
      this.ladeArbeitsnachweisSpinner.close();
    } else {
      // Neuen Arbeitsnachweis laden
      this.arbeitsnachweisService
        .getArbeitsnachweisById(abrechnungsmonat.arbeitsnachweisId)
        .subscribe((arbeitsnachweis: Arbeitsnachweis) => {
          this.geladenenArbeitsnachweisVerarbeiten(arbeitsnachweis);
          this.weitereDatenFuerArbeitsnachweisNachladen(arbeitsnachweis);
        });
    }
  }

  private geladenenArbeitsnachweisVerarbeiten(arbeitsnachweis: Arbeitsnachweis): void {
    // Arbeitsnachweis merken
    this.aktuellerArbeitsnachweis = arbeitsnachweis;

    // Angaben zu Smartphone
    const smartphoneBesitzArtEnum: SmartphoneBesitzArtenEnum =
      arbeitsnachweis.smartphoneEigen === null
        ? SmartphoneBesitzArtenEnum.KEIN
        : arbeitsnachweis.smartphoneEigen
        ? SmartphoneBesitzArtenEnum.EIGEN
        : SmartphoneBesitzArtenEnum.FIRMA;
    this.smartphoneAuswahlFormGroup.get('smartphoneBesitzArt')?.setValue(smartphoneBesitzArtEnum);
  }

  private weitereDatenFuerArbeitsnachweisNachladen(arbeitsnachweis: Arbeitsnachweis): void {
    forkJoin([
      this.arbeitsnachweisService.getProjektstundenByArbeitsnachweis(arbeitsnachweis),
      this.arbeitsnachweisService.getBelegByArbeitsnachweis(arbeitsnachweis),
      this.arbeitsnachweisService.getAbwesenheitenByArbeitsnachweis(arbeitsnachweis),
    ]).subscribe(([projektstunden, belege, abwesenheiten]) => {
      this.angerechneteReisezeit = this.berechneAngerechneteReisezeitAusProjektstunden(projektstunden);
      this.projektstundenListen = new StatusListen(projektstunden.slice());
      this.belegeListen = new StatusListen(belege.slice());
      this.abwesenheitsListen = new StatusListen(abwesenheiten.slice());

      this.aktualisiereArbeitsnachweisAbrechnung();

      this.ladeArbeitsnachweisSpinner.close();
    });

    this.arbeitsnachweisService.getDmsUrlByArbeitsnachweis(arbeitsnachweis).subscribe((url) => {
      this.dmsBelegUrl = url;
    });

    this.arbeitsnachweisService
      .getLohnartZuordnungenByArbeitsnachweis(arbeitsnachweis)
      .subscribe((lohnartZuordnungen: LohnartZuordnung[]) => {
        this.lohnartZuordnungen = lohnartZuordnungen.slice();
      });

    this.arbeitsnachweisService
      .getLohnartberechnungLogsByArbeitsnachweis(arbeitsnachweis)
      .subscribe((lohnartberechnungLog) => {
        this.lohnartberechnungLog = lohnartberechnungLog.slice();
      });

    this.arbeitsnachweisService
      .getDreiMonatsRegelnByArbeitsnachweis(arbeitsnachweis)
      .subscribe((dreiMonatsRegeln: DreiMonatsRegel[]) => {
        this.dreiMonatsRegeln = dreiMonatsRegeln;
      });

    this.arbeitsnachweisService.getFehlerlogsByArbeitsnachweis(arbeitsnachweis).subscribe((fehlerlog) => {
      arbeitsnachweis.fehlerlog = fehlerlog;
    });
  }

  private berechneAngerechneteReisezeitAusProjektstunden(projektstunden: Projektstunde[]) {
    return this.filtereProjektstundenNachProjektstundeTyp(
      projektstunden,
      this.konstantenService.getProjektstundenTypAngerechneteReise()
    ).reduce((sum, p) => sum + p.anzahlStunden, 0);
  }

  private datenZuruecksetzenBeiWechselMitarbeiter(): void {
    this.abrechnungsmonatAuswahl = [];
  }

  private datenZuruecksetzenBeiWechselArbeitsnachweis(): void {
    this.aktuellerArbeitsnachweis = undefined;

    this.projektstundenFormGroup.reset();
    this.belegeFormGroup.reset();
    this.smartphoneAuswahlFormGroup.reset();
    this.abwesenheitenFormGroup.reset();
    this.sonderzeitenFormGroup.reset();
    this.abrechnungAuszahlungFormGroup.reset();

    this.projektstundenListen = new StatusListen([]);
    this.belegeListen = new StatusListen([]);
    this.abwesenheitsListen = new StatusListen([]);
    this.angerechneteReisezeit = 0;
    this.lohnartZuordnungen = [];
    this.lohnartberechnungLog = [];
    this.dreiMonatsRegeln = [];
  }

  private erstelleDummyArbeitsnachweis(abrechnungsmonat: MitarbeiterAbrechnungsmonat): Arbeitsnachweis {
    const mitarbeiterAusgewaehlt = this.anwAuswahlFormGroup.getRawValue().mitarbeiter;

    return {
      jahr: abrechnungsmonat.jahr,
      monat: abrechnungsmonat.monat,
      mitarbeiterId: mitarbeiterAusgewaehlt.id,
      smartphoneEigen: null,
      sollstunden: 0,
      auszahlung: 0,
    } as Arbeitsnachweis;
  }

  private aktualisiereArbeitsnachweisAbrechnung() {
    this.ladeAbrechnungSpinner.open();
    this.arbeitsnachweisService
      .erstelleArbeitsnachweisAbrechnung(
        this.aktuellerArbeitsnachweis,
        this.projektstundenListen.getAnzeigeListe() as Projektstunde[],
        this.abwesenheitsListen.getAnzeigeListe() as Abwesenheit[],
        this.belegeListen.getAnzeigeListe() as Beleg[]
      )
      .subscribe((anwAbrechnung: ArbeitsnachweisAbrechnung) => {
        this.arbeitsnachweisAbrechnung = anwAbrechnung;
        this.abrechnungAuszahlungFormGroup.patchValue({
          sollstunden: anwAbrechnung.sollstunden ? convertUsNummerZuDeString(anwAbrechnung.sollstunden) : '0',
          auszahlung: anwAbrechnung.auszahlung ? convertUsNummerZuDeString(anwAbrechnung.auszahlung) : '0',
        });
        this.ladeAbrechnungSpinner.close();
      });
  }

  private fuehreArbeitsnachweisWechselNachExcelImportDurch(excelImportErgebnis: ExcelImportErgebnis) {
    this.ladeArbeitsnachweisSpinner.open();

    // Masken bereinigen
    this.datenZuruecksetzenBeiWechselArbeitsnachweis();
    const abrechnungsMonatExistiert = this.setzeAbrechnungsmonatInMaskeOhneChangeListening(
      excelImportErgebnis.arbeitsnachweis
    );

    if (!abrechnungsMonatExistiert) {
      this.ladeArbeitsnachweisSpinner.close();
      return;
    }
    this.geladenenArbeitsnachweisVerarbeiten(excelImportErgebnis.arbeitsnachweis);
    this.weitereDatenFuerArbeitsnachweisNachExcelImportSetzen(excelImportErgebnis);
  }

  private weitereDatenFuerArbeitsnachweisNachExcelImportSetzen(excelImportErgebnis: ExcelImportErgebnis) {
    if (excelImportErgebnis.arbeitsnachweis.id) {
      // Fall: der Excel-Import überschreibt einen bestehenden Arbeitsnachweis
      forkJoin([
        this.arbeitsnachweisService.getProjektstundenByArbeitsnachweis(excelImportErgebnis.arbeitsnachweis),
        this.arbeitsnachweisService.getBelegByArbeitsnachweis(excelImportErgebnis.arbeitsnachweis),
        this.arbeitsnachweisService.getAbwesenheitenByArbeitsnachweis(excelImportErgebnis.arbeitsnachweis),
      ]).subscribe(([projektstunden, belege, abwesenheiten]) => {
        this.angerechneteReisezeit = this.berechneAngerechneteReisezeitAusProjektstunden(projektstunden);
        this.setzteStatusListenNachExcelImport(excelImportErgebnis, projektstunden, belege, abwesenheiten);
        this.ladeArbeitsnachweisSpinner.close();
        this.arbeitsnachweisService.getDmsUrlByArbeitsnachweis(excelImportErgebnis.arbeitsnachweis).subscribe((url) => {
          this.dmsBelegUrl = url;
        });
        this.reagiereAufReisezeitAktualisiertEvent();
      });
    } else {
      // Fall: es handelt sich um einen neuen Arbeitsnachweis
      this.setzteStatusListenNachExcelImport(excelImportErgebnis, [], [], []);
      this.aktualisiereArbeitsnachweisAbrechnung();
      this.ladeArbeitsnachweisSpinner.close();
      this.reagiereAufReisezeitAktualisiertEvent();
    }
  }

  private setzeAbrechnungsmonatInMaskeOhneChangeListening(arbeitsnachweis: Arbeitsnachweis): boolean {
    const ausgewaehlterAbrechnungsMonat: MitarbeiterAbrechnungsmonat | undefined = this.abrechnungsmonatAuswahl.find(
      (abrechnungsmonat) =>
        abrechnungsmonat.monat === arbeitsnachweis.monat && abrechnungsmonat.jahr === arbeitsnachweis.jahr
    );

    if (ausgewaehlterAbrechnungsMonat) {
      this.anwAuswahlFormGroup.get('abrechnungsmonat')?.setValue(ausgewaehlterAbrechnungsMonat, { emitEvent: false });
      this.anwAuswahlFormGroup.get('vorherigerAbrechnungsmonat')?.setValue(ausgewaehlterAbrechnungsMonat);
      return true;
    } else {
      this.benachrichtigungService.erstelleErrorMessage(
        'Es ist ein technischer Fehler aufgetreten. Der ausgewählte Abrechnungsmonat ist für den Mitarbeiter unbekannt.'
      );
      return false;
    }
  }

  private setzteStatusListenNachExcelImport(
    excelImportErgebnis: ExcelImportErgebnis,
    projektstundenZuLoeschen: Projektstunde[],
    belegeZuLoeschen: Beleg[],
    abwesenheitenZuLoeschen: Abwesenheit[]
  ) {
    let projektstunden: Projektstunde[] = [];
    projektstunden = projektstunden.concat(excelImportErgebnis.importierteProjektstunden);
    projektstunden = projektstunden.concat(excelImportErgebnis.importierteReisezeiten);
    projektstunden = projektstunden.concat(excelImportErgebnis.importierteRufbereitschaft);
    projektstunden = projektstunden.concat(excelImportErgebnis.importierteSonderarbeitszeiten);

    this.projektstundenListen = new StatusListen([], [], projektstunden, projektstundenZuLoeschen);
    this.belegeListen = new StatusListen([], [], excelImportErgebnis.importierteBelege, belegeZuLoeschen);
    this.abwesenheitsListen = new StatusListen(
      [],
      [],
      excelImportErgebnis.importierteAbwesenheiten,
      abwesenheitenZuLoeschen
    );
  }

  private aktuellerAbrechnungsmonatUngleichAnwAbrechnungsmonat(arbeitsnachweis: Arbeitsnachweis) {
    const aktuellerAbrechnungsmonat: MitarbeiterAbrechnungsmonat =
      this.anwAuswahlFormGroup.getRawValue().abrechnungsmonat;
    return (
      aktuellerAbrechnungsmonat.jahr !== arbeitsnachweis.jahr ||
      aktuellerAbrechnungsmonat.monat !== arbeitsnachweis.monat
    );
  }

  private pruefeObFehlerExistiertUndZeigeWarnungen(fehlerlog: Fehlerlog[], spinnerOverlay: SpinnerOverlayRef): boolean {
    const fehler: Fehlerlog | undefined = fehlerlog.find(
      (fehlerlog) => fehlerlog.fehlerklasse === ImportFehlerklassenEnum.KRITISCH
    );

    if (fehler) {
      spinnerOverlay.close();
      this.benachrichtigungService.erstelleErrorMessage(fehler.fehlertext);
      return true;
    }

    return false;
  }

  private speichereWarnungenInArbeitsnachweisUndZeigeAn(excelImportErgebnis: ExcelImportErgebnis) {
    excelImportErgebnis.arbeitsnachweis.fehlerlog = excelImportErgebnis.fehlerlog;
    this.oeffneFehlerlogDialog(excelImportErgebnis.fehlerlog);
  }

  private emailFuerInterneMitarbeiterSchicken(arbeitsnachweis: Arbeitsnachweis) {
    this.berichteService.getEmailDatenFuerArbeitsnachweisAbrechnung(arbeitsnachweis).subscribe((emailDaten) => {
      if (!emailDaten.emailAdressen || !emailDaten.emailAdressen[0]) {
        this.benachrichtigungService.erstelleWarnung('Keine Email-Adresse für den Mitarbeiter vorhanden.');
      }
      const data: EmailDialogData = {
        empfanger: emailDaten.emailAdressen[0],
        empfangerBearbeitbar: false,
        mitarbeiterEmails: [],
        betreff: emailDaten.betreff,
        nachricht: emailDaten.nachricht,
        dateiname: emailDaten.dateiname,
        sumbmitButtonText: 'Rückmeldung senden',
        abbruchButtonText: 'Ohne Rückmeldung fortfahren',
      };
      const emailDialogRef = this.dialog.open(EmailDialogComponent, {
        data,
        panelClass: 'dialog-container-default',
        disableClose: true,
      });
      emailDialogRef.componentInstance.sendeEmailEvent.subscribe((emailDaten: MitarbeiterQuittungEmailDaten) => {
        this.berichteService.sendeEmailFuerArbeitsnachweisAbrechnung(emailDaten, arbeitsnachweis).subscribe(() => {
          this.benachrichtigungService.erstelleBenachrichtigung('Email erfolgreich versendet');
        });
      });
      emailDialogRef.componentInstance.oeffnePdfAnhangEvent.subscribe(() => {
        this.berichteService.getB004FuerArbeitsnachweisPdfAnhang(arbeitsnachweis).subscribe((pdf: Blob) => {
          const fileURL = URL.createObjectURL(pdf);
          window.open(fileURL, '_blank');
        });
      });
    });
  }
}
