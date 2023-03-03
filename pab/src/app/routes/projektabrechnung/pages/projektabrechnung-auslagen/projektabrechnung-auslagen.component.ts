import { AfterViewChecked, AfterViewInit, ChangeDetectorRef, Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { Belegart } from '../../../../shared/model/konstanten/belegart';
import { ViadeeAuslagenKostenart } from '../../../../shared/model/konstanten/viadee-auslagen-kostenart';
import { ProjektService } from '../../../../shared/service/projekt.service';
import { StatusListen } from '../../../../shared/model/sonstiges/status-listen';
import { MitarbeiterService } from '../../../../shared/service/mitarbeiter.service';
import { ArbeitsnachweisService } from '../../../../shared/service/arbeitsnachweis.service';
import { Abrechnungsmonat } from '../../../../shared/model/sonstiges/abrechnungsmonat';
import { forkJoin } from 'rxjs';
import { KonstantenService } from '../../../../shared/service/konstanten.service';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';
import { ProjektabrechnungService } from '../../../../shared/service/projektabrechnung.service';
import { BenachrichtigungService } from '../../../../shared/service/benachrichtigung.service';
import { SonstigeProjektkosten } from '../../../../shared/model/projektabrechnung/sonstige-projektkosten';
import { SpinnerOverlayService } from '../../../../shared/service/spinner-overlay.service';
import { AbrechnungsmonatService } from '../../../../shared/service/abrechnungsmonat.service';
import { CustomValidatorProjektabrechnung } from '../../validation/custom-validator-projektabrechnung';
import { CustomValidator } from '../../../../shared/validation/custom-validator';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { ProjektabrechnungAuslagenQueryParams } from '../../../../shared/model/query-params/projektabrechnung-auslagen-query-params';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'pab-projektabrechnung-auslagen',
  templateUrl: './projektabrechnung-auslagen.component.html',
  styleUrls: ['./projektabrechnung-auslagen.component.scss'],
})
export class ProjektabrechnungAuslagenComponent implements AfterViewInit, AfterViewChecked {
  ueberschrift: string = 'viadee Auslagen';
  projektabrechnungAuslagenSpinner = new SpinnerRef();

  auslagenFilterFormGroup: FormGroup;
  auslagenDetailsFormGroup: FormGroup;

  alleMitarbeiter: Mitarbeiter[] = [];
  mitarbeiterAuswahl: Mitarbeiter[] = [];
  projektAuswahl: Projekt[] = [];
  abrechnungsmonatAuswahl: Abrechnungsmonat[] = [];
  belegartAuswahl: Belegart[] = [];
  viadeeAuslagenKostenartAuswahl: ViadeeAuslagenKostenart[] = [];

  sonstigeProjektkostenListe: StatusListen;

  constructor(
    private fb: FormBuilder,
    private changeDetectorRef: ChangeDetectorRef,
    private route: ActivatedRoute,
    private projektService: ProjektService,
    private arbeitsnachweisService: ArbeitsnachweisService,
    private mitarbeiterService: MitarbeiterService,
    private konstantenService: KonstantenService,
    private projektabrechnungService: ProjektabrechnungService,
    private benachrichtungService: BenachrichtigungService,
    private abrechnungsmonatService: AbrechnungsmonatService,
    private spinnerOverlayService: SpinnerOverlayService
  ) {
    this.sonstigeProjektkostenListe = new StatusListen([]);

    this.auslagenFilterFormGroup = this.fb.nonNullable.group({
      abrechnungsmonat: [{} as Abrechnungsmonat],
      projekt: [{} as Projekt, [CustomValidator.valueIstObjektOderLeererString()]],
      mitarbeiter: [{} as Mitarbeiter, [CustomValidator.valueIstObjektOderLeererString()]],
    });

    this.auslagenDetailsFormGroup = this.fb.nonNullable.group({
      id: [''],
      abrechnungsmonat: [
        {} as Abrechnungsmonat,
        [
          Validators.required,
          CustomValidatorProjektabrechnung.abrechnungsmonatIstNichtAbgeschlossen(),
          CustomValidatorProjektabrechnung.abrechnungsmonatIstNichtLeer(),
        ],
      ],
      projekt: [{} as Projekt, [Validators.required, CustomValidator.objektIstNichtLeer()]],
      mitarbeiter: [{} as Mitarbeiter],
      viadeeAuslagenKostenart: [
        {} as ViadeeAuslagenKostenart,
        [Validators.required, CustomValidator.objektIstNichtLeer()],
      ],
      belegart: [{} as Belegart, [Validators.required, CustomValidator.objektIstNichtLeer()]],
      betrag: ['', Validators.required],
      bemerkung: [''],
    });
  }

  ngAfterViewInit(): void {
    this.rufeInitialeDatenAb();
  }

  ngAfterViewChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  reagiereAufSpeichernEvent() {
    const data = {
      title: 'Änderungen werden gespeichert...',
    };
    const spinnerOverlay = this.spinnerOverlayService.open(data);
    this.projektabrechnungService
      .speichernViadeeAuslage(
        this.sonstigeProjektkostenListe.getGeloeschtListe() as SonstigeProjektkosten[],
        this.sonstigeProjektkostenListe.getAktualisiertListe() as SonstigeProjektkosten[],
        this.sonstigeProjektkostenListe.getNeuListe() as SonstigeProjektkosten[]
      )
      .subscribe((sonstigeProjektkostenResponse) => {
        spinnerOverlay.close();
        this.benachrichtungService.erstelleBenachrichtigung('viadee Auslagen wurden erfolgreich gespeichert.');

        if (sonstigeProjektkostenResponse) {
          this.benachrichtungService.erstelleWarnung(sonstigeProjektkostenResponse.meldungen);
        }

        this.rufeInitialeDatenAb();
      });
  }

  private rufeInitialeDatenAb(): void {
    this.projektabrechnungAuslagenSpinner.open();
    forkJoin([
      this.mitarbeiterService.getAlleMitarbeiter(),
      this.mitarbeiterService.getMitarbeiterAuswahlFuerMitarbeiterKonten(),
      this.projektService.getAlleProjekte(),
      this.projektService.getAlleSonstigeProjektkosten(),
      this.arbeitsnachweisService.getAlleAbrechnungsmonate(),
      this.konstantenService.getBelegartenAll(),
      this.konstantenService.getViadeeAuslagenKostenartAll(),
    ]).subscribe(
      ([
        alleMitarbeiter,
        mitarbeiterAuswahl,
        projekte,
        sonstigeProjektkosten,
        abrechnungsmonate,
        belegart,
        viadeeAuslagenKostenart,
      ]) => {
        this.alleMitarbeiter = alleMitarbeiter;
        this.mitarbeiterAuswahl = mitarbeiterAuswahl.slice();
        this.projektAuswahl = projekte.slice();
        this.belegartAuswahl = belegart.slice();
        this.viadeeAuslagenKostenartAuswahl = viadeeAuslagenKostenart.slice();
        this.sonstigeProjektkostenListe = new StatusListen(sonstigeProjektkosten);
        this.projektabrechnungAuslagenSpinner.close();

        this.abrechnungsmonatService.sortiereAbrechnungsmonate(abrechnungsmonate);
        this.abrechnungsmonatAuswahl = abrechnungsmonate.slice();

        this.verarbeiteQueryParameter();
      }
    );
  }

  private verarbeiteQueryParameter(): void {
    this.route.queryParams.subscribe((params) => {
      if (!params['jahr']) {
        return;
      }
      const queryParams = {
        jahr: parseInt(params['jahr']),
        monat: parseInt(params['monat']),
        projektId: params['projektId'],
        mitarbeiterId: params['mitarbeiterId'],
      } as ProjektabrechnungAuslagenQueryParams;

      if (queryParams.jahr && queryParams.monat) {
        const abrechnungsmonat: Abrechnungsmonat | undefined = this.abrechnungsmonatAuswahl.find(
          (element) => element.jahr === queryParams.jahr && element.monat === queryParams.monat
        );
        if (abrechnungsmonat) {
          this.auslagenFilterFormGroup.get('abrechnungsmonat')?.setValue(abrechnungsmonat);
        }
      }

      if (queryParams.projektId) {
        const projekt: Projekt = getObjektAusListeDurchId(queryParams.projektId, this.projektAuswahl) as Projekt;
        this.auslagenFilterFormGroup.get('projekt')?.setValue(projekt);
      }

      if (queryParams.mitarbeiterId) {
        const mitarbeiter: Mitarbeiter = getObjektAusListeDurchId(
          queryParams.mitarbeiterId,
          this.mitarbeiterAuswahl
        ) as Mitarbeiter;
        this.auslagenFilterFormGroup.get('mitarbeiter')?.setValue(mitarbeiter);
      }
    });
  }
}
