import { AfterViewChecked, AfterViewInit, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { ProjektService } from '../../../../shared/service/projekt.service';
import { BehaviorSubject, filter, forkJoin } from 'rxjs';
import { ProjektabrechnungService } from '../../../../shared/service/projektabrechnung.service';
import { ArbeitsnachweisService } from '../../../../shared/service/arbeitsnachweis.service';
import { ProjektAbrechnungsmonat } from '../../../../shared/model/projektabrechnung/projekt-abrechnungsmonat';
import { AbrechnungsmonatService } from '../../../../shared/service/abrechnungsmonat.service';
import { Kunde } from '../../../../shared/model/kunde/kunde';
import { Organisationseinheit } from '../../../../shared/model/organisationseinheit/organisationseinheit';
import { MitarbeiterService } from '../../../../shared/service/mitarbeiter.service';
import { KundeService } from '../../../../shared/service/kunde.service';
import { OrganisationseinheitService } from '../../../../shared/service/organisationseinheit.service';
import { Projektabrechnung } from '../../../../shared/model/projektabrechnung/projektabrechnung';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';
import { ProjektabrechnungKostenLeistung } from '../../../../shared/model/projektabrechnung/projektabrechnung-kosten-leistung';
import { ProjektabrechnungProjektzeit } from '../../../../shared/model/projektabrechnung/projektabrechnung-projektzeit';
import { ProjektabrechnungSonstige } from '../../../../shared/model/projektabrechnung/projektabrechnung-sonstige';
import { ProjektabrechnungSonderarbeit } from '../../../../shared/model/projektabrechnung/projektabrechnung-sonderarbeit';
import { ProjektabrechnungReise } from '../../../../shared/model/projektabrechnung/projektabrechnung-reise';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { BenachrichtigungService } from '../../../../shared/service/benachrichtigung.service';
import { MatDialog } from '@angular/material/dialog';
import {
  ProjektabrechnungBearbeitenTabReiseSpesenZuschlaegeDialogComponent,
  ProjektabrechnungBearbeitenTabReiseSpesenZuschlaegeDialogData,
} from '../../components/projektabrechnung-bearbeiten-tab-reise-spesen-zuschlaege-dialog/projektabrechnung-bearbeiten-tab-reise-spesen-zuschlaege-dialog.component';
import { Abwesenheit } from '../../../../shared/model/arbeitsnachweis/abwesenheit';
import { MitarbeiterAbrechnungsmonat } from '../../../../shared/model/mitarbeiter/mitarbeiter-abrechnungsmonat';
import { Belegart } from '../../../../shared/model/konstanten/belegart';
import { KonstantenService } from '../../../../shared/service/konstanten.service';
import { Beleg } from '../../../../shared/model/arbeitsnachweis/beleg';
import {
  ProjektabrechnungBearbeitenTabReiseBelegeDialogComponent,
  ProjektabrechnungBearbeitenTabReiseBelegeDialogData,
} from '../../components/projektabrechnung-bearbeiten-tab-reise-belege-dialog/projektabrechnung-bearbeiten-tab-reise-belege-dialog.component';
import { SonstigeProjektkosten } from '../../../../shared/model/projektabrechnung/sonstige-projektkosten';
import {
  ProjektabrechnungBearbeitenTabAuslagenDialogComponent,
  ProjektabrechnungBearbeitenTabAuslagenDialogData,
} from '../../components/projektabrechnung-bearbeiten-tab-auslagen-dialog/projektabrechnung-bearbeiten-tab-auslagen-dialog.component';
import { ViadeeAuslagenKostenart } from '../../../../shared/model/konstanten/viadee-auslagen-kostenart';
import { CustomValidatorProjektabrechnung } from '../../validation/custom-validator-projektabrechnung';
import {
  convertDeStringZuUsNummer,
  convertUsNummerZuDeStringMitGenauZweiNachkommastellen,
  rundeNummerAufZweiNackommastellen,
} from '../../../../shared/util/nummer-converter.util';
import { SpeichernPostAktionEnum } from '../../../../shared/enum/speichern-post-aktion.enum';
import { SpinnerOverlayService } from '../../../../shared/service/spinner-overlay.service';
import { NavigationService } from '../../../../shared/service/navigation.service';
import { ProjekttypEnum } from '../../../../shared/enum/projekttyp.enum';
import { StatusListen } from '../../../../shared/model/sonstiges/status-listen';
import { ProjektabrechnungBerechneteLeistung } from '../../../../shared/model/projektabrechnung/projektabrechnung-berechnete-leistung';
import { ObjektMitId } from '../../../../shared/model/sonstiges/objekt-mit-id';
import { ArbeitsnachweisBearbeitenQueryParams } from '../../../../shared/model/query-params/arbeitsnachweis-bearbeiten-query-params';
import { ProjektabrechnungBearbeitenQueryParams } from '../../../../shared/model/query-params/projektabrechnung-bearbeiten-query-params';
import { ActivatedRoute } from '@angular/router';
import {
  ProjektabrechnungBearbeitenFertigstellungDialogComponent,
  ProjektabrechnungBearbeitenFertigstellungDialogData,
} from '../../components/projektabrechnung-bearbeiten-fertigstellung-dialog/projektabrechnung-bearbeiten-fertigstellung-dialog.component';
import { CustomValidator } from '../../../../shared/validation/custom-validator';
import { ProjektabrechnungFertigstellungInitialDaten } from '../../../../shared/model/projektabrechnung/projektabrechnung-fertigstellung-initial-daten';
import { VerwaltungFakturuebersichtQueryParams } from '../../../../shared/model/query-params/verwaltung-fakturuebersicht-query-params';
import { istProjektFestpreis, istProjektKunde } from '../../../../shared/util/compare.util';

@Component({
  selector: 'pab-projektabrechnung-bearbeiten',
  templateUrl: './projektabrechnung-bearbeiten.component.html',
  styleUrls: ['./projektabrechnung-bearbeiten.component.scss'],
})
export class ProjektabrechnungBearbeitenComponent implements OnInit, AfterViewInit, AfterViewChecked {
  // Initial sind alle Felder gesperrt, bis ein entsprechende Projektabrechnung geladen ist
  ladeProjektabrechnungSpinner: SpinnerRef = new SpinnerRef();
  ladeFertigstellungDialogDatenSpinner: SpinnerRef = new SpinnerRef();
  ausgewaehlterTab: number = 3;
  hatMitarbeiterMehrAlsBerechneteLeistung: boolean = false;

  aktuelleProjektabrechnung: Projektabrechnung | undefined = undefined;

  mitarbeiterAlle: Mitarbeiter[] = [];
  sachbearbeiterAlle: Mitarbeiter[] = [];
  kundeAlle: Kunde[] = [];
  organisationseinheitAlle: Organisationseinheit[] = [];
  belegartenAlle: Belegart[] = [];
  viadeeAuslagenKostenartenAlle: ViadeeAuslagenKostenart[] = [];

  projektAuswahl: Projekt[] = [];
  abrechnungsmonatAuswahl: ProjektAbrechnungsmonat[] = [];
  projektabrechnungKostenLeistungAuswahl: ProjektabrechnungKostenLeistung[] = [];

  pabAuswahlFormGroup: FormGroup;
  pabProjektzeitFormGroup: FormGroup;
  pabProjektzeitAlternativeStundensaetzeFormArray: FormArray;
  pabReiseFormGroup: FormGroup;
  pabSonderarbeitFormGroup: FormGroup;
  pabSonstigeFormGroup: FormGroup;
  pabFakturfaehigeLeistungFormGroup: FormGroup;
  pabFertigstellungFormGroup: FormGroup;
  berechneteLeistungen: StatusListen | undefined = undefined;

  private queryParams: ProjektabrechnungBearbeitenQueryParams | undefined = undefined;

  constructor(
    private fb: FormBuilder,
    private changeDetectorRef: ChangeDetectorRef,
    private route: ActivatedRoute,
    private mitarbeiterService: MitarbeiterService,
    private projektService: ProjektService,
    private kundeService: KundeService,
    private organistationseinheitService: OrganisationseinheitService,
    private projektabrechnungService: ProjektabrechnungService,
    private arbeitsnachweisService: ArbeitsnachweisService,
    private abrechnungsmonatService: AbrechnungsmonatService,
    private konstantenService: KonstantenService,
    private benachrichtigungService: BenachrichtigungService,
    private spinnerOverlayService: SpinnerOverlayService,
    private navigationService: NavigationService,
    public dialog: MatDialog
  ) {
    this.pabAuswahlFormGroup = this.fb.nonNullable.group({
      projekt: [{} as Projekt],
      abrechnungsmonat: [{} as ProjektAbrechnungsmonat],
      mitarbeiter: [{} as ProjektabrechnungKostenLeistung],
      kunde: [{ value: '', disabled: true }],
      projekttyp: [{ value: '', disabled: true }],
      sachbearbeiter: [{ value: '', disabled: true }],
      organisationseinheit: [{ value: '', disabled: true }],
      projektverantwortung: [{ value: '', disabled: true }],
      geschaeftsstelle: [{ value: '', disabled: true }],

      kosten: [{ value: '0,00', disabled: true }],
      leistung: [{ value: '0,00', disabled: true }],
    });

    this.pabProjektzeitAlternativeStundensaetzeFormArray = this.fb.array([]);
    this.pabProjektzeitFormGroup = this.fb.nonNullable.group(
      {
        kostenStundenGesamt: ['0,00'],

        kostenStunden: ['0,00'],
        kostenKostensatz: ['0,00'],
        kostenKostensatzLetzter: ['0,00'],
        kostenKostensatzVertrag: ['0,00'],
        kostenBetrag: ['0,00'],

        leistungStundensatz: ['0,00'],
        leistungStundensatzLetzter: ['0,00'],
        leistungStundensatzVertrag: ['0,00'],
        leistungBetrag: ['0,00'],

        alternativeStundensaetze: this.pabProjektzeitAlternativeStundensaetzeFormArray,

        kosten: ['0,00'],
        leistung: ['0,00'],
      },
      { validators: CustomValidatorProjektabrechnung.bearbeitenProjektzeitGesamtstundenUebertschritten() }
    );

    this.pabReiseFormGroup = this.fb.nonNullable.group({
      kostenReisezeitenAngerechneteReisezeit: ['0,00'],
      kostenReisezeitenKostensatz: ['0,00'],
      kostenReisezeitenBetrag: ['0,00'],

      leistungReisezeitenFakturierteReisezeit: ['0,00'],
      leistungReisezeitenTatsaechlicheReisezeit: ['0,00'],
      leistungReisezeitenStundensatz: ['0,00'],
      leistungReisezeitenBetrag: ['0,00'],

      kostenBelegeUndAuslagenBelege: ['0,00'],
      kostenBelegeUndAuslagenAuslagen: ['0,00'],
      kostenBelegeUndAuslagenSpesen: ['0,00'],
      kostenBelegeUndAuslagenZuschlaege: ['0,00'],
      kostenBelegeUndAuslagenBetrag: ['0,00'],

      leistungBelegeUndAuslagenBelege: ['0,00'],
      leistungBelegeUndAuslagenAuslagen: ['0,00'],
      leistungBelegeUndAuslagenSpesen: ['0,00'],
      leistungBelegeUndAuslagenZuschlaege: ['0,00'],
      leistungBelegeUndAuslagenBetrag: ['0,00'],

      leistungPauschaleAnzahl: ['0,00'],
      leistungPauschaleProTag: ['0,00'],
      leistungPauschaleBetrag: ['0,00'],

      kosten: ['0,00'],
      leistung: ['0,00'],
    });

    this.pabSonderarbeitFormGroup = this.fb.nonNullable.group({
      kostenRufbereitschaftenAnzahlStunden: ['0,00'],
      kostenRufbereitschaftenKostensatz: ['0,00'],
      kostenRufbereitschaftenBetrag: ['0,00'],

      leistungRufbereitschaftenAnzahlStunden: ['0,00'],
      leistungRufbereitschaftenStundensatz: ['0,00'],
      leistungRufbereitschaftenPauschale: ['0,00'],
      leistungRufbereitschaftenBetrag: ['0,00'],

      kostenSonderarbeitszeitenAnzahlStunden50: ['0,00'],
      kostenSonderarbeitszeitenAnzahlStunden100: ['0,00'],
      kostenSonderarbeitszeitenKostensatz: ['0,00'],
      kostenSonderarbeitszeitenPauschale: ['0,00'],
      kostenSonderarbeitszeitenBetrag: ['0,00'],

      leistungSonderarbeitszeitenPauschale: ['0,00'],
      leistungSonderarbeitszeitenBetrag: ['0,00'],

      kosten: ['0,00'],
      leistung: ['0,00'],
    });

    this.pabSonstigeFormGroup = this.fb.nonNullable.group({
      kostenSonstigeAuslagen: ['0,00'],
      kostenSonstigePauschale: ['0,00'],
      kostenSonstigeBetrag: ['0,00'],

      leistungSonstigePauschale: ['0,00'],
      leistungSonstigeBetrag: ['0,00'],

      bemerkung: [''],

      kosten: ['0,00'],
      leistung: ['0,00'],
    });

    this.pabFakturfaehigeLeistungFormGroup = this.fb.nonNullable.group({
      fakturierfaehigeLeistung: ['0,00'],
    });

    this.pabFertigstellungFormGroup = this.fb.nonNullable.group(
      {
        bisherFertigstellung: ['0,00'],
        bisherProjektbudget: ['0,00'],
        bisherLeistungFaktuierfaehigKumuliert: ['0,00'],
        bisherLeistungRechnerisch: ['0,00'],
        monatFertigstellung: [
          '0,00',
          [
            CustomValidator.minFuerString(0),
            CustomValidator.maxFuerString(100),
            Validators.pattern('^\\d{0,3}(,\\d{0,6})?$'),
            Validators.required,
          ],
        ],
        monatProjektbudget: ['0,00'],
        monatLeistungFaktuierfaehigKumuliert: ['0,00'],
        monatLeistungFaktuierfaehig: ['0,00'],
        monatLeistungRechnerisch: ['0,00'],
        monatErrechneteFertigstellung: ['0,00'],
      },
      { validators: CustomValidatorProjektabrechnung.bearbeitenFertigstellungErrechneteLeistungZuKlein() }
    );
  }

  ngOnInit() {
    this.reagiereAufAenderungProjekt();
    this.reagiereAufAenderungAbrechnungsmonat();
    this.reagiereAufAenderungMitarbeiter();
  }

  ngAfterViewInit(): void {
    this.rufeInitialeDatenAb();
  }

  ngAfterViewChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  reagiereAufErgaenzeAlternativenStundensatzEvent(): FormGroup {
    const alternativerStundensatz = this.fb.nonNullable.group({
      kostenStunden: ['0,00'],
      kostenKostensatz: ['0,00'],
      kostenBetrag: ['0,00'],

      leistungStundensatz: ['0,00'],
      leistungBetrag: ['0,00'],
    });
    (this.pabProjektzeitAlternativeStundensaetzeFormArray as FormArray).push(alternativerStundensatz);

    alternativerStundensatz.disable();
    if (!this.istAktuelleProjektabrechnungOffen()) {
      return alternativerStundensatz;
    }

    alternativerStundensatz.get('kostenStunden')?.enable();

    const projektabrechnungKostenLeistung: ProjektabrechnungKostenLeistung =
      this.pabAuswahlFormGroup.getRawValue().mitarbeiter;
    if (projektabrechnungKostenLeistung.ohneMitarbeiterBezug) {
      return alternativerStundensatz;
    }

    const mitarbeiter: Mitarbeiter = getObjektAusListeDurchId(
      projektabrechnungKostenLeistung.mitarbeiterId,
      this.mitarbeiterAlle
    ) as Mitarbeiter;
    if (!mitarbeiter.intern) {
      alternativerStundensatz.get('kostenKostensatz')?.enable();
    }

    const projekt: Projekt = this.pabAuswahlFormGroup.getRawValue().projekt;
    const istProjektIntern: boolean = !!(projekt && projekt.id && projekt.projekttyp === ProjekttypEnum.INTERN);
    if (!istProjektIntern) {
      alternativerStundensatz.get('leistungStundensatz')?.enable();
    }

    return alternativerStundensatz;
  }

  reagiereAufOeffneBelegeDialogEvent() {
    this.arbeitsnachweisService
      .getAbrechnungsmonateByMitarbeiterId(this.pabAuswahlFormGroup.getRawValue().mitarbeiter.mitarbeiterId)
      .subscribe((abrechnungsmonate: MitarbeiterAbrechnungsmonat[]) => {
        const aktuellerAbrechnungsmonat = abrechnungsmonate.find((abrechnungsmonat) => {
          return (
            abrechnungsmonat.monat === this.aktuelleProjektabrechnung?.monat &&
            abrechnungsmonat.jahr === this.aktuelleProjektabrechnung.jahr
          );
        });
        if (!aktuellerAbrechnungsmonat) {
          this.benachrichtigungService.erstelleWarnung(
            'Es ist noch kein Arbeitsnachweis für diesen Mitarbeiter und diesen Monat erfasst'
          );
          return;
        }

        this.arbeitsnachweisService
          .getBelegByArbeitsnachweisId(aktuellerAbrechnungsmonat.arbeitsnachweisId!)
          .subscribe((belege: Beleg[]) => {
            const data: ProjektabrechnungBearbeitenTabReiseBelegeDialogData = {
              belege: belege.filter((beleg) => beleg.projektId === this.pabAuswahlFormGroup.getRawValue().projekt.id),
              projekte: this.projektAuswahl,
              belegarten: this.belegartenAlle,
            };

            this.dialog.open(ProjektabrechnungBearbeitenTabReiseBelegeDialogComponent, {
              data,
              panelClass: 'dialog-container-default',
            });
          });
      });
  }

  reagiereAufReiseOeffneAuslagenDialogEvent() {
    this.projektService.getAlleSonstigeProjektkosten().subscribe((sonstigeProjektkostens: SonstigeProjektkosten[]) => {
      const viadeeAuslagenKostenartReise: ViadeeAuslagenKostenart =
        this.konstantenService.getViadeeAuslagenKostenartReise();
      const sonstigeProjektkostensGefiltert: SonstigeProjektkosten[] = sonstigeProjektkostens.filter(
        (sonstigeProjektkosten) =>
          sonstigeProjektkosten.viadeeAuslagenKostenartId === viadeeAuslagenKostenartReise.id &&
          sonstigeProjektkosten.projektId === this.pabAuswahlFormGroup.getRawValue().projekt.id &&
          sonstigeProjektkosten.monat === this.aktuelleProjektabrechnung?.monat &&
          sonstigeProjektkosten.jahr === this.aktuelleProjektabrechnung.jahr &&
          sonstigeProjektkosten.mitarbeiterId === this.pabAuswahlFormGroup.getRawValue().mitarbeiter.mitarbeiterId
      );

      const data: ProjektabrechnungBearbeitenTabAuslagenDialogData = {
        sonstigeProjektkostens: sonstigeProjektkostensGefiltert,
        belegarten: this.belegartenAlle,
      };

      this.dialog.open(ProjektabrechnungBearbeitenTabAuslagenDialogComponent, {
        data,
        panelClass: 'dialog-container-default',
      });
    });
  }

  reagiereAufSonstigeOeffneAuslagenDialogEvent() {
    this.projektService.getAlleSonstigeProjektkosten().subscribe((sonstigeProjektkostens: SonstigeProjektkosten[]) => {
      const viadeeAuslagenKostenartSonstiges: ViadeeAuslagenKostenart =
        this.konstantenService.getViadeeAuslagenKostenartSonstiges();
      const sonstigeProjektkostensGefiltert: SonstigeProjektkosten[] = sonstigeProjektkostens.filter(
        (sonstigeProjektkosten) =>
          sonstigeProjektkosten.viadeeAuslagenKostenartId === viadeeAuslagenKostenartSonstiges.id &&
          sonstigeProjektkosten.projektId === this.pabAuswahlFormGroup.getRawValue().projekt.id &&
          sonstigeProjektkosten.monat === this.aktuelleProjektabrechnung?.monat &&
          sonstigeProjektkosten.jahr === this.aktuelleProjektabrechnung.jahr &&
          sonstigeProjektkosten.mitarbeiterId === this.pabAuswahlFormGroup.getRawValue().mitarbeiter.mitarbeiterId
      );

      const data: ProjektabrechnungBearbeitenTabAuslagenDialogData = {
        sonstigeProjektkostens: sonstigeProjektkostensGefiltert,
        belegarten: this.belegartenAlle,
      };

      this.dialog.open(ProjektabrechnungBearbeitenTabAuslagenDialogComponent, {
        data,
        panelClass: 'dialog-container-default',
      });
    });
  }

  reagiereAufOeffneSpesenUndZuschlaegeDialogEvent() {
    this.arbeitsnachweisService
      .getAbrechnungsmonateByMitarbeiterId(this.pabAuswahlFormGroup.getRawValue().mitarbeiter.mitarbeiterId)
      .subscribe((abrechnungsmonate: MitarbeiterAbrechnungsmonat[]) => {
        const aktuellerAbrechnungsmonat = abrechnungsmonate.find((abrechnungsmonat) => {
          return (
            abrechnungsmonat.monat === this.aktuelleProjektabrechnung?.monat &&
            abrechnungsmonat.jahr === this.aktuelleProjektabrechnung.jahr
          );
        });
        if (!aktuellerAbrechnungsmonat) {
          this.benachrichtigungService.erstelleWarnung(
            'Es ist noch kein Arbeitsnachweis für diesen Mitarbeiter und diesen Monat erfasst'
          );
          return;
        }
        this.arbeitsnachweisService
          .getAbwesenheitenByArbeitsnachweisId(aktuellerAbrechnungsmonat.arbeitsnachweisId!)
          .subscribe((abwesenheit: Abwesenheit[]) => {
            const data: ProjektabrechnungBearbeitenTabReiseSpesenZuschlaegeDialogData = {
              abwesenheiten: abwesenheit.filter(
                (abwesenheit) => abwesenheit.projektId === this.pabAuswahlFormGroup.getRawValue().projekt.id
              ),
              projekte: this.projektAuswahl,
            };

            this.dialog.open(ProjektabrechnungBearbeitenTabReiseSpesenZuschlaegeDialogComponent, {
              data,
              panelClass: 'dialog-container-default',
            });
          });
      });
  }

  reagiereAufNavigiereZuArbeitsnachweisBearbeitenEvent() {
    this.arbeitsnachweisService
      .getAbrechnungsmonateByMitarbeiterId(this.pabAuswahlFormGroup.getRawValue().mitarbeiter.mitarbeiterId)
      .subscribe((abrechnungsmonate: MitarbeiterAbrechnungsmonat[]) => {
        const aktuellerAbrechnungsmonat = abrechnungsmonate.find((abrechnungsmonat) => {
          return (
            abrechnungsmonat.monat === this.aktuelleProjektabrechnung?.monat &&
            abrechnungsmonat.jahr === this.aktuelleProjektabrechnung.jahr
          );
        });
        if (!aktuellerAbrechnungsmonat) {
          this.benachrichtigungService.erstelleWarnung(
            'Es ist noch kein Arbeitsnachweis für diesen Mitarbeiter und diesen Monat erfasst'
          );
          return;
        }

        const queryParams: ArbeitsnachweisBearbeitenQueryParams = {
          jahr: this.pabAuswahlFormGroup.getRawValue().abrechnungsmonat.jahr,
          monat: this.pabAuswahlFormGroup.getRawValue().abrechnungsmonat.monat,
          mitarbeiterId: this.pabAuswahlFormGroup.getRawValue().mitarbeiter.mitarbeiterId,
          belegeTab: true,
        };
        const url: string = '/arbeitsnachweis/bearbeiten';
        this.navigationService.navigiereZuMitQueryParams(url, false, queryParams);
      });
  }

  reagiereAufAktualisiereGesamtKostenUndLeistungEvent(): void {
    this.berechneUndSetzeGesamtKostenUndLeistung(this.pabAuswahlFormGroup.getRawValue().mitarbeiter);
  }

  reagiereAufSpeichernEvent(speichernPostAktion: SpeichernPostAktionEnum) {
    const dataSpinner = {
      title: 'Änderungen werden gespeichert...',
    };
    const spinnerOverlay = this.spinnerOverlayService.open(dataSpinner);
    spinnerOverlay.close();
    this.benachrichtigungService.erstelleBenachrichtigung('Projektabrechnung wurde erfolgreich gespeichert.');
    this.fuehreAktionNachSpeichernAus(speichernPostAktion);
  }

  reagiereAufOeffneFakturuebersichtEvent() {
    const queryParams: VerwaltungFakturuebersichtQueryParams = {
      projektId: this.pabAuswahlFormGroup.getRawValue().projekt.id,
    };
    const url: string = '/verwaltung/fakturuebersicht';
    this.navigationService.navigiereZuMitQueryParams(url, false, queryParams);
  }

  reagiereAufOeffneFertigstellungDialogEvent() {
    this.ladeFertigstellungDialogDatenSpinner.open();
    const projektabrechnungId: string = this.pabAuswahlFormGroup.getRawValue().abrechnungsmonat.projektabrechnungId!;
    const fertigstellungInitialDatenSub: BehaviorSubject<ProjektabrechnungFertigstellungInitialDaten | undefined> =
      new BehaviorSubject<ProjektabrechnungFertigstellungInitialDaten | undefined>(undefined);

    const data: ProjektabrechnungBearbeitenFertigstellungDialogData = {
      pabFertigstellungFormGroup: this.pabFertigstellungFormGroup,
      ladeFertigstellungDialogDatenSpinner: this.ladeFertigstellungDialogDatenSpinner,
      fertigstellungInitialDatenObs: fertigstellungInitialDatenSub,
    };

    const dialogRef = this.dialog.open(ProjektabrechnungBearbeitenFertigstellungDialogComponent, {
      data,
      panelClass: 'dialog-container-large',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (!result) {
        return;
      }
      const monatFertigstellung: number = convertDeStringZuUsNummer(
        this.pabFertigstellungFormGroup.getRawValue().monatFertigstellung
      );
      this.projektabrechnungService
        .berechneNeueFertigstellung(projektabrechnungId, monatFertigstellung)
        .subscribe((neueBerechneteLeistungen: ProjektabrechnungBerechneteLeistung[]) => {
          // Status-Listen Objekt neu erzeugen, alle Elemente alles gelöscht markieren, bzw. falls mehrfach berrechnet gelöschte Elemente übernehmen
          const vorhandeneElemente = this.berechneteLeistungen!.getUnveraendertListe().concat(
            this.berechneteLeistungen!.getGeloeschtListe()
          );
          this.berechneteLeistungen = new StatusListen([]);
          this.berechneteLeistungen.setGeloescht(vorhandeneElemente);
          neueBerechneteLeistungen.forEach((element) => this.berechneteLeistungen!.fuegeNeuesElementHinzu(element));
          this.aktualisiereBerchneteLeistung(this.pabAuswahlFormGroup.getRawValue().mitarbeiter.mitarbeiterId);

          // Weitere Daten aktualisieren
          this.aktuelleProjektabrechnung!.fertigstellungsgrad = monatFertigstellung;
          this.aktuelleProjektabrechnung!.budgetBetragZurAbrechnung = convertDeStringZuUsNummer(
            this.pabFertigstellungFormGroup.getRawValue().monatProjektbudget
          );
        });
    });

    this.projektabrechnungService
      .getProjektabrechnungFertigstellungInitialeDaten(
        projektabrechnungId,
        this.aktuelleProjektabrechnung!.fertigstellungsgrad
      )
      .subscribe((projektabrechnungFertigstellungInitialDaten: ProjektabrechnungFertigstellungInitialDaten) => {
        if (projektabrechnungFertigstellungInitialDaten.meldungen.length > 0) {
          this.benachrichtigungService.erstelleWarnung(...projektabrechnungFertigstellungInitialDaten.meldungen);
          this.ladeFertigstellungDialogDatenSpinner.close();
          return;
        }
        fertigstellungInitialDatenSub.next(projektabrechnungFertigstellungInitialDaten);
        fertigstellungInitialDatenSub.complete();
        data.ladeFertigstellungDialogDatenSpinner.close();
      });
  }

  reagiereAufProjektabrechnungAbrechnenEvent() {
    console.log('IMPLEMENT ME - reagiereAufProjektabrechnungAbrechnenEvent()');
  }

  istAktuelleProjektabrechnungOffen(): boolean {
    if (!this.aktuelleProjektabrechnung) {
      return false;
    }

    const abrechnungsmonat: ProjektAbrechnungsmonat = this.pabAuswahlFormGroup.getRawValue().abrechnungsmonat;
    return !abrechnungsmonat.abgeschlossen;
  }

  istProjektFestpreis(): boolean {
    const projekt: Projekt | undefined = this.pabAuswahlFormGroup.getRawValue().projekt;
    return istProjektFestpreis(projekt);
  }

  istProjektKunde(): boolean {
    const projekt: Projekt | undefined = this.pabAuswahlFormGroup.getRawValue().projekt;
    return istProjektKunde(projekt);
  }

  istAenderungVorhanden(): boolean {
    return (
      (this.pabProjektzeitFormGroup.touched && this.pabProjektzeitFormGroup.valid) ||
      (this.pabReiseFormGroup.touched && this.pabReiseFormGroup.valid) ||
      (this.pabSonderarbeitFormGroup.touched && this.pabSonderarbeitFormGroup.valid) ||
      (this.pabSonstigeFormGroup.touched && this.pabSonstigeFormGroup.valid) ||
      (!!this.berechneteLeistungen && this.berechneteLeistungen.istVeraendert())
    );
  }

  private reagiereAufAenderungProjekt(): void {
    this.pabAuswahlFormGroup
      .get('projekt')
      ?.valueChanges.pipe(filter((projekt) => projekt === '' || typeof projekt !== 'string'))
      .subscribe((projekt: Projekt) => {
        this.ladeProjektabrechnungSpinner.open();
        this.datenZuruecksetzenBeiWechselProjekt();
        if (!projekt || (projekt && !projekt.id)) {
          this.pabAuswahlFormGroup.get('abrechnungsmonat')?.reset();
          this.ladeProjektabrechnungSpinner.close();
          return;
        }

        this.projektabrechnungService
          .getAbrechnungsmonateByProjekt(projekt)
          .subscribe((abrechnungsmonate: ProjektAbrechnungsmonat[]) => {
            this.abrechnungsmonatService.ergaenzeFehlendenAbrechnungsmonateMitDummy(abrechnungsmonate);
            this.abrechnungsmonatService.sortiereAbrechnungsmonate(abrechnungsmonate);
            this.setzeVorauswahlFuerAbrechnungsmonat(abrechnungsmonate);
            this.abrechnungsmonatAuswahl = abrechnungsmonate.slice();
          });
      });
  }

  private datenZuruecksetzenBeiWechselProjekt(): void {
    this.abrechnungsmonatAuswahl = [];
  }

  private reagiereAufAenderungAbrechnungsmonat(): void {
    this.pabAuswahlFormGroup
      .get('abrechnungsmonat')
      ?.valueChanges.subscribe((abrechnungsmonat: ProjektAbrechnungsmonat) => {
        this.fuehreProjektabrechnungWechselDurch(abrechnungsmonat);
      });
  }

  private fuehreProjektabrechnungWechselDurch(abrechnungsmonat: ProjektAbrechnungsmonat): void {
    // Erstmal alles wegwerfen
    this.datenZuruecksetzenBeiWechselAbrechnungsmonat();

    // Kein Monat ausgewählt
    if (!abrechnungsmonat || !abrechnungsmonat.monat) {
      this.pabAuswahlFormGroup.get('mitarbeiter')?.reset();
      this.ladeProjektabrechnungSpinner.close();
      return;
    }

    if (!abrechnungsmonat.projektabrechnungId) {
      // Dummy-Monat ausgewählt -> Dummy-Projektabrechnung erzeugen und laden
      const dummyProjektabrechnung: Projektabrechnung = this.erstelleDummyProjektabrechnung(abrechnungsmonat);
      this.geladeneProjektabrechnungVerarbeiten(dummyProjektabrechnung);

      const projektabrechnungKostenLeistungs: ProjektabrechnungKostenLeistung[] = [
        this.erstelleDummyProjektabrechnungKostenLeistung(),
      ];
      this.projektabrechnungKostenLeistungAuswahl = projektabrechnungKostenLeistungs;
      this.setzeVorauswahlFuerMitarbeiter(projektabrechnungKostenLeistungs);
      this.berechneUndSetzeGesamtKostenUndLeistung();

      this.ladeProjektabrechnungSpinner.close();
    } else {
      // Neue Projektabrechnung laden
      forkJoin([
        this.projektabrechnungService.getProjektabrechnungById(abrechnungsmonat.projektabrechnungId),
        this.projektabrechnungService.getKostenLeistungJeMitarbeiter(abrechnungsmonat.projektabrechnungId),
      ]).subscribe(([projektabrechnung, projektabrechnungKostenLeistungs]) => {
        this.geladeneProjektabrechnungVerarbeiten(projektabrechnung);

        this.projektabrechnungKostenLeistungAuswahl = this.sortiereProjektabrechnungKostenLeistungen(
          projektabrechnungKostenLeistungs
        );
        this.setzeVorauswahlFuerMitarbeiter(projektabrechnungKostenLeistungs);
        this.berechneUndSetzeGesamtKostenUndLeistung();

        this.ladeProjektabrechnungSpinner.close();
      });
    }
  }

  private sortiereProjektabrechnungKostenLeistungen(
    projektabrechnungKostenLeistungen: ProjektabrechnungKostenLeistung[]
  ): ProjektabrechnungKostenLeistung[] {
    return projektabrechnungKostenLeistungen.sort((a, b): number => {
      if (a.mitarbeiterId === b.mitarbeiterId) {
        return 0;
      }
      if (a.ohneMitarbeiterBezug && !b.ohneMitarbeiterBezug) {
        return 1;
      }
      if (!a.ohneMitarbeiterBezug && b.ohneMitarbeiterBezug) {
        return -1;
      }

      const mitarbeiterA: Mitarbeiter = getObjektAusListeDurchId(a.mitarbeiterId, this.mitarbeiterAlle) as Mitarbeiter;
      const mitarbeiterB: Mitarbeiter = getObjektAusListeDurchId(b.mitarbeiterId, this.mitarbeiterAlle) as Mitarbeiter;

      return mitarbeiterA.nachname.localeCompare(mitarbeiterB.nachname);
    });
  }

  private datenZuruecksetzenBeiWechselAbrechnungsmonat(): void {
    this.projektabrechnungKostenLeistungAuswahl = [];

    this.pabAuswahlFormGroup.get('kosten')?.reset();
    this.pabAuswahlFormGroup.get('leistung')?.reset();

    this.aktuelleProjektabrechnung = undefined;
    this.berechneteLeistungen = undefined;
  }

  private reagiereAufAenderungMitarbeiter(): void {
    this.pabAuswahlFormGroup
      .get('mitarbeiter')
      ?.valueChanges.subscribe((projektabrechnungKostenLeistung: ProjektabrechnungKostenLeistung) => {
        // Erstmal alles wegwerfen
        this.datenZuruecksetzenBeiWechselMitarbeiter();

        // Projektabrechnung und damit auch Mitarbeiter vorhanden?
        if (!this.aktuelleProjektabrechnung) {
          this.ausgewaehlterTab = 3;
          return;
        }
        // Dummy-Projektabrechnung vorhanden oder echte Projektabrechnung?
        if (!this.aktuelleProjektabrechnung.id) {
          return;
        }

        if (projektabrechnungKostenLeistung.ohneMitarbeiterBezug) {
          this.ladeSonstigeOhneMitarbeiterbezug();
          this.ausgewaehlterTab = 3;
        } else {
          this.ladeBerechneteLeistungen(projektabrechnungKostenLeistung.mitarbeiterId);
          this.ladeProjektzeitFurMitarbeiter(projektabrechnungKostenLeistung.mitarbeiterId);
          this.ladeReiseFuerMitarbeiter(projektabrechnungKostenLeistung.mitarbeiterId);
          this.ladeSonderarbeitFuerMitarbeiter(projektabrechnungKostenLeistung.mitarbeiterId);
          this.ladeSonstigeFuerMitarbeiter(projektabrechnungKostenLeistung.mitarbeiterId);
          this.pruefeObMitarbeiterMehrAlsBerechneteLeistungHat(projektabrechnungKostenLeistung.mitarbeiterId);
        }
      });
  }

  private datenZuruecksetzenBeiWechselMitarbeiter(): void {
    this.pabProjektzeitAlternativeStundensaetzeFormArray.clear();
    this.pabProjektzeitFormGroup.reset();
    this.pabReiseFormGroup.reset();
    this.pabSonderarbeitFormGroup.reset();
    this.pabSonstigeFormGroup.reset();
    this.pabFakturfaehigeLeistungFormGroup.reset();

    this.hatMitarbeiterMehrAlsBerechneteLeistung = false;
  }

  private erstelleDummyProjektabrechnung(abrechnungsmonat: ProjektAbrechnungsmonat): Projektabrechnung {
    const projektAusgewaehlt = this.pabAuswahlFormGroup.getRawValue().projekt;

    return {
      jahr: abrechnungsmonat.jahr,
      monat: abrechnungsmonat.monat,
      projektId: projektAusgewaehlt.id,
      korrekturVorhanden: false,
    } as Projektabrechnung;
  }

  private erstelleDummyProjektabrechnungKostenLeistung(): ProjektabrechnungKostenLeistung {
    return {
      projektzeitKosten: 0,
      projektzeitLeistung: 0,
      reiseKosten: 0,
      reiseLeistung: 0,
      sonderzeitKosten: 0,
      sonderzeitLeistung: 0,
      sonstigeKosten: 0,
      sonstigeLeistung: 0,
      fakturierfaehigeLeistung: 0,
      ohneMitarbeiterBezug: true,
    } as ProjektabrechnungKostenLeistung;
  }

  private geladeneProjektabrechnungVerarbeiten(projektabrechnung: Projektabrechnung): void {
    // Projektabrechnung merken
    this.aktuelleProjektabrechnung = projektabrechnung;
  }

  private setzeVorauswahlFuerMitarbeiter(projektabrechnungKostenLeistungs: ProjektabrechnungKostenLeistung[]) {
    this.pabAuswahlFormGroup.get('mitarbeiter')?.setValue(projektabrechnungKostenLeistungs[0]);
  }

  private berechneUndSetzeGesamtKostenUndLeistung(
    aktuelleProjektabrechnungKostenLeistung?: ProjektabrechnungKostenLeistung
  ) {
    let kosten: number = 0;
    let leistung: number = 0;

    this.projektabrechnungKostenLeistungAuswahl.forEach(
      (projektabrechnungKostenLeistung: ProjektabrechnungKostenLeistung) => {
        // Die Werte können abweichen, daher ignorieren wir das aktuell ausgewählte ProjektabrechnungKostenLeistung Objekt und lesen direkt aus den Forms
        if (
          aktuelleProjektabrechnungKostenLeistung &&
          aktuelleProjektabrechnungKostenLeistung.mitarbeiterId === projektabrechnungKostenLeistung.mitarbeiterId
        ) {
          return;
        }

        kosten +=
          projektabrechnungKostenLeistung.projektzeitKosten +
          projektabrechnungKostenLeistung.reiseKosten +
          projektabrechnungKostenLeistung.sonderzeitKosten +
          projektabrechnungKostenLeistung.sonstigeKosten;
        leistung +=
          projektabrechnungKostenLeistung.projektzeitLeistung +
          projektabrechnungKostenLeistung.reiseLeistung +
          projektabrechnungKostenLeistung.sonderzeitLeistung +
          projektabrechnungKostenLeistung.sonstigeLeistung;
      }
    );

    if (aktuelleProjektabrechnungKostenLeistung) {
      kosten += convertDeStringZuUsNummer(this.pabProjektzeitFormGroup.getRawValue().kosten);
      kosten += convertDeStringZuUsNummer(this.pabReiseFormGroup.getRawValue().kosten);
      kosten += convertDeStringZuUsNummer(this.pabSonderarbeitFormGroup.getRawValue().kosten);
      kosten += convertDeStringZuUsNummer(this.pabSonstigeFormGroup.getRawValue().kosten);

      leistung += convertDeStringZuUsNummer(this.pabProjektzeitFormGroup.getRawValue().leistung);
      leistung += convertDeStringZuUsNummer(this.pabReiseFormGroup.getRawValue().leistung);
      leistung += convertDeStringZuUsNummer(this.pabSonderarbeitFormGroup.getRawValue().leistung);
      leistung += convertDeStringZuUsNummer(this.pabSonstigeFormGroup.getRawValue().leistung);
    }

    this.pabAuswahlFormGroup
      .get('kosten')
      ?.setValue(convertUsNummerZuDeStringMitGenauZweiNachkommastellen(rundeNummerAufZweiNackommastellen(kosten)));
    this.pabAuswahlFormGroup
      .get('leistung')
      ?.setValue(convertUsNummerZuDeStringMitGenauZweiNachkommastellen(rundeNummerAufZweiNackommastellen(leistung)));
  }

  private ladeBerechneteLeistungen(mitarbeiterId: string) {
    if (!this.istProjektFestpreis()) {
      return;
    }
    const projektabrechnungId: string = this.pabAuswahlFormGroup.getRawValue().abrechnungsmonat.projektabrechnungId!;
    if (this.berechneteLeistungen) {
      this.aktualisiereBerchneteLeistung(mitarbeiterId);
      return;
    }
    this.projektabrechnungService
      .getProjektabrechnungBerechneteLeistungen(projektabrechnungId)
      .subscribe((projektabrechnungBerechneteLeistungen: ProjektabrechnungBerechneteLeistung[]) => {
        this.berechneteLeistungen = new StatusListen(projektabrechnungBerechneteLeistungen);
        this.aktualisiereBerchneteLeistung(mitarbeiterId);
      });
  }

  private aktualisiereBerchneteLeistung(mitarbeiterId: string) {
    // Pruefe ob Element schon vorhanden
    const projektabrechnungBerchneteLeistung: ObjektMitId | undefined = this.berechneteLeistungen
      ?.getAnzeigeListe()
      .find((element) => {
        return (element as ProjektabrechnungBerechneteLeistung).mitarbeiterId === mitarbeiterId;
      });
    if (projektabrechnungBerchneteLeistung) {
      this.pabFakturfaehigeLeistungFormGroup.patchValue({
        fakturierfaehigeLeistung: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
          rundeNummerAufZweiNackommastellen(
            (projektabrechnungBerchneteLeistung as ProjektabrechnungBerechneteLeistung).leistung
          )
        ),
      });
      return;
    }

    // Element erzeugen
    const neueProjektabrechnungBerechneteLeistung: ProjektabrechnungBerechneteLeistung = {
      projektabrechnungId: this.pabAuswahlFormGroup.getRawValue().abrechnungsmonat.projektabrechnungId!,
      mitarbeiterId: mitarbeiterId,
      leistung: 0,
    } as ProjektabrechnungBerechneteLeistung;
    this.berechneteLeistungen?.fuegeNeuesElementHinzu(neueProjektabrechnungBerechneteLeistung);
    this.pabFakturfaehigeLeistungFormGroup.patchValue({
      fakturierfaehigeLeistung: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
        rundeNummerAufZweiNackommastellen(neueProjektabrechnungBerechneteLeistung.leistung)
      ),
    });
  }

  private ladeProjektzeitFurMitarbeiter(mitarbeiterId: string) {
    const projektAbrechnungsmonat: ProjektAbrechnungsmonat = this.pabAuswahlFormGroup.getRawValue().abrechnungsmonat;
    const projektId: string = this.pabAuswahlFormGroup.getRawValue().projekt.id;
    this.projektabrechnungService
      .getProjektabrechnungProjektzeituProjektabrechnungFuerMitarbeiter(
        projektAbrechnungsmonat.projektabrechnungId!,
        mitarbeiterId,
        projektId,
        projektAbrechnungsmonat.jahr,
        projektAbrechnungsmonat.monat
      )
      .subscribe((projektabrechnungProjektzeits: ProjektabrechnungProjektzeit[]) => {
        projektabrechnungProjektzeits.forEach((projektabrechnungProjektzeit) => {
          if (projektabrechnungProjektzeit.laufendeNummer === 1) {
            this.pabProjektzeitFormGroup.patchValue({
              kostenStundenGesamt: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
                projektabrechnungProjektzeits.reduce((sum, element) => {
                  return sum + element.stundenLautArbeitsnachweis;
                }, 0)
              ),

              kostenStunden: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
                projektabrechnungProjektzeit.stundenLautArbeitsnachweis
              ),
              kostenKostensatz: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
                projektabrechnungProjektzeit.kostensatz
              ),
              kostenKostensatzLetzter: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
                projektabrechnungProjektzeit.kostensatzVormonat
              ),
              kostenKostensatzVertrag: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
                projektabrechnungProjektzeit.kostensatzVertrag
              ),

              leistungStundensatz: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
                projektabrechnungProjektzeit.stundensatz
              ),
              leistungStundensatzLetzter: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
                projektabrechnungProjektzeit.stundensatzVormonat
              ),
              leistungStundensatzVertrag: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
                projektabrechnungProjektzeit.stundensatzVertrag
              ),
            });
            return;
          }

          const alternativerStundensatzFormGroup = this.reagiereAufErgaenzeAlternativenStundensatzEvent();
          alternativerStundensatzFormGroup.patchValue({
            kostenStunden: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
              projektabrechnungProjektzeit.stundenLautArbeitsnachweis
            ),
            kostenKostensatz: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
              projektabrechnungProjektzeit.kostensatz
            ),
            leistungStundensatz: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
              projektabrechnungProjektzeit.stundensatz
            ),
          });
        });
      });
  }

  private ladeReiseFuerMitarbeiter(mitarbeiterId: string) {
    const projektabrechnungId: string = this.pabAuswahlFormGroup.getRawValue().abrechnungsmonat.projektabrechnungId!;
    this.projektabrechnungService
      .getProjektabrechnungReiseZuProjektabrechnungFuerMitarbeiter(projektabrechnungId, mitarbeiterId)
      .subscribe((projektabrechnungReise: ProjektabrechnungReise) => {
        if (!projektabrechnungReise || !projektabrechnungReise.projektabrechnungId) {
          return;
        }

        this.pabReiseFormGroup.patchValue({
          kostenReisezeitenAngerechneteReisezeit: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungReise.angerechneteReisezeit
          ),
          kostenReisezeitenKostensatz: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungReise.kostensatz
          ),
          leistungReisezeitenFakturierteReisezeit: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungReise.tatsaechlicheReisezeit
          ),
          leistungReisezeitenTatsaechlicheReisezeit: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungReise.tatsaechlicheReisezeitInformatorisch
          ),
          leistungReisezeitenStundensatz: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungReise.stundensatz
          ),
          kostenBelegeUndAuslagenBelege: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungReise.belegeLautArbeitsnachweisKosten
          ),
          kostenBelegeUndAuslagenAuslagen: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungReise.belegeViadeeKosten
          ),
          kostenBelegeUndAuslagenSpesen: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungReise.spesenKosten
          ),
          kostenBelegeUndAuslagenZuschlaege: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungReise.zuschlaegeKosten
          ),
          leistungBelegeUndAuslagenBelege: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungReise.belegeLautArbeitsnachweisLeistung
          ),
          leistungBelegeUndAuslagenAuslagen: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungReise.belegeViadeeLeistung
          ),
          leistungBelegeUndAuslagenSpesen: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungReise.spesenLeistung
          ),
          leistungBelegeUndAuslagenZuschlaege: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungReise.zuschlaegeLeistung
          ),
          leistungPauschaleAnzahl: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungReise.pauschaleAnzahl
          ),
          leistungPauschaleProTag: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungReise.pauschaleProTag
          ),
        });
      });
  }

  private ladeSonderarbeitFuerMitarbeiter(mitarbeiterId: string) {
    const projektabrechnungId: string = this.pabAuswahlFormGroup.getRawValue().abrechnungsmonat.projektabrechnungId!;
    this.projektabrechnungService
      .getProjektabrechnungSonderarbeitZuProjektabrechnungFuerMitarbeiter(projektabrechnungId, mitarbeiterId)
      .subscribe((projektabrechnungSonderarbeit: ProjektabrechnungSonderarbeit) => {
        if (!projektabrechnungSonderarbeit || !projektabrechnungSonderarbeit.projektabrechnungId) {
          return;
        }

        this.pabSonderarbeitFormGroup.patchValue({
          kostenRufbereitschaftenAnzahlStunden: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungSonderarbeit.rufbereitschaftKostenAnzahlStunden
          ),
          kostenRufbereitschaftenKostensatz: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungSonderarbeit.rufbereitschaftKostensatz
          ),
          leistungRufbereitschaftenAnzahlStunden: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungSonderarbeit.rufbereitschaftLeistungAnzahlStunden
          ),
          leistungRufbereitschaftenStundensatz: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungSonderarbeit.rufbereitschaftStundensatz
          ),
          leistungRufbereitschaftenPauschale: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungSonderarbeit.rufbereitschaftLeistungPauschale
          ),
          kostenSonderarbeitszeitenAnzahlStunden50: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungSonderarbeit.sonderarbeitAnzahlStunden50
          ),
          kostenSonderarbeitszeitenAnzahlStunden100: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungSonderarbeit.sonderarbeitAnzahlStunden100
          ),
          kostenSonderarbeitszeitenKostensatz: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungSonderarbeit.sonderarbeitKostensatz
          ),
          kostenSonderarbeitszeitenPauschale: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungSonderarbeit.sonderarbeitPauschale
          ),
          leistungSonderarbeitszeitenPauschale: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
            projektabrechnungSonderarbeit.sonderarbeitLeistungPauschale
          ),
        });
      });
  }

  private ladeSonstigeFuerMitarbeiter(mitarbeiterId: string) {
    const projektabrechnungId: string = this.pabAuswahlFormGroup.getRawValue().abrechnungsmonat.projektabrechnungId!;
    this.projektabrechnungService
      .getProjektabrechnungSonstigeZuProjektabrechnungFuerMitarbeiter(projektabrechnungId, mitarbeiterId)
      .subscribe((projektabrechnungSonstige: ProjektabrechnungSonstige) => {
        this.verarbeiteProjektabrechnungSonstige(projektabrechnungSonstige);
      });
  }

  private pruefeObMitarbeiterMehrAlsBerechneteLeistungHat(mitarbeiterId: string) {
    const projektabrechnungId: string = this.pabAuswahlFormGroup.getRawValue().abrechnungsmonat.projektabrechnungId!;
    this.projektabrechnungService
      .getMitarbeiterHatMehrAlsBerechneteLeistung(projektabrechnungId, mitarbeiterId)
      .subscribe((hatMitarbeiterMehrAlsBerechneteLeistung: boolean) => {
        this.hatMitarbeiterMehrAlsBerechneteLeistung = hatMitarbeiterMehrAlsBerechneteLeistung;

        if (!hatMitarbeiterMehrAlsBerechneteLeistung) {
          this.ausgewaehlterTab = 4;
        }
      });
  }

  private ladeSonstigeOhneMitarbeiterbezug() {
    const projektabrechnungId: string = this.pabAuswahlFormGroup.getRawValue().abrechnungsmonat.projektabrechnungId!;
    this.projektabrechnungService
      .getProjektabrechnungSonstigeZuProjektabrechnungOhneMitarbeiterbezug(projektabrechnungId)
      .subscribe((projektabrechnungSonstige: ProjektabrechnungSonstige) => {
        this.verarbeiteProjektabrechnungSonstige(projektabrechnungSonstige);
      });
  }

  private verarbeiteProjektabrechnungSonstige(projektabrechnungSonstige: ProjektabrechnungSonstige) {
    if (!projektabrechnungSonstige || !projektabrechnungSonstige.projektabrechnungId) {
      return;
    }

    this.pabSonstigeFormGroup.patchValue({
      kostenSonstigeAuslagen: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
        projektabrechnungSonstige.viadeeAuslagen
      ),
      kostenSonstigePauschale: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
        projektabrechnungSonstige.pauschaleKosten
      ),
      leistungSonstigePauschale: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
        projektabrechnungSonstige.pauschaleLeistung
      ),
      bemerkung: projektabrechnungSonstige.bemerkung,
    });
  }

  private setzeVorauswahlFuerAbrechnungsmonat(abrechnungsmonate: ProjektAbrechnungsmonat[]): void {
    const abrechnungsmonat: ProjektAbrechnungsmonat | undefined =
      this.abrechnungsmonatService.erhalteVorauswahlFuerAbrechnungsmonat(abrechnungsmonate, this.queryParams);
    if (this.queryParams) {
      this.queryParams = undefined;
    }
    if (!abrechnungsmonat) {
      return;
    }
    this.pabAuswahlFormGroup.get('abrechnungsmonat')?.setValue(abrechnungsmonat as ProjektAbrechnungsmonat);
  }

  private rufeInitialeDatenAb(): void {
    forkJoin([
      this.mitarbeiterService.getAlleMitarbeiter(),
      this.mitarbeiterService.getAlleSachbearbeiter(),
      this.projektService.getAlleProjekte(),
      this.kundeService.getAlleKunden(),
      this.organistationseinheitService.getAlleOrganisationseinheiten(),
      this.konstantenService.getBelegartenAll(),
      this.konstantenService.getViadeeAuslagenKostenartAll(),
    ]).subscribe(
      ([
        mitarbeiter,
        sachbearbeiter,
        projekte,
        kunden,
        organisationseinheiten,
        belegarten,
        viadeeAuslagenKostenarten,
      ]) => {
        this.mitarbeiterAlle = mitarbeiter.slice();
        this.sachbearbeiterAlle = sachbearbeiter.slice();
        this.kundeAlle = kunden.slice();
        this.organisationseinheitAlle = organisationseinheiten.slice();
        this.belegartenAlle = belegarten.slice();
        this.viadeeAuslagenKostenartenAlle = viadeeAuslagenKostenarten.slice();

        this.projektAuswahl = projekte.slice();
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
        projektId: params['projektId'],
      };
      const projekt: Projekt = getObjektAusListeDurchId(this.queryParams.projektId, this.projektAuswahl) as Projekt;
      this.pabAuswahlFormGroup.get('projekt')!.setValue(projekt);
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
        this.navigationService.initialisiereRoute('/projektabrechnung/bearbeiten');
        break;
    }
  }
}
