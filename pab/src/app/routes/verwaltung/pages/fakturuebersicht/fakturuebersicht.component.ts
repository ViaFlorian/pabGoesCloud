import { AfterViewChecked, AfterViewInit, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { SpeichernPostAktionEnum } from '../../../../shared/enum/speichern-post-aktion.enum';
import { BenachrichtigungService } from '../../../../shared/service/benachrichtigung.service';
import { SpinnerOverlayService } from '../../../../shared/service/spinner-overlay.service';
import { NavigationService } from '../../../../shared/service/navigation.service';
import { StatusListen } from '../../../../shared/model/sonstiges/status-listen';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { filter, forkJoin } from 'rxjs';
import { ProjektService } from '../../../../shared/service/projekt.service';
import { ActivatedRoute } from '@angular/router';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';
import { Abrechnungsmonat } from '../../../../shared/model/sonstiges/abrechnungsmonat';
import { Kunde } from '../../../../shared/model/kunde/kunde';
import { KundeService } from '../../../../shared/service/kunde.service';
import { VerwaltungService } from '../../../../shared/service/verwaltung.service';
import { Faktur } from '../../../../shared/model/verwaltung/faktur';
import { convertUsNummerZuDeStringMitGenauZweiNachkommastellen } from '../../../../shared/util/nummer-converter.util';
import { ArbeitsnachweisService } from '../../../../shared/service/arbeitsnachweis.service';
import { istProjektKunde } from '../../../../shared/util/compare.util';
import { AbrechnungsmonatService } from '../../../../shared/service/abrechnungsmonat.service';

@Component({
  selector: 'pab-fakturuebersicht',
  templateUrl: './fakturuebersicht.component.html',
  styleUrls: ['./fakturuebersicht.component.scss'],
})
export class FakturuebersichtComponent implements OnInit, AfterViewInit, AfterViewChecked {
  ladeFakturSpinner: SpinnerRef = new SpinnerRef();

  hinweisText: string = 'Es können nur Fakturen für Kundenprojekte erfasst werden.';

  kundeAlle: Kunde[] = [];
  projektAuswahl: Projekt[] = [];
  abrechnungsmonateAuswahl: Abrechnungsmonat[] = [];
  fakturAuswahlFormGroup: FormGroup;

  fakturFormGroup: FormGroup;

  fakturenListe: StatusListen;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private changeDetectorRef: ChangeDetectorRef,
    private projektService: ProjektService,
    private kundeService: KundeService,
    private verwaltungService: VerwaltungService,
    private arbeitsnachweisService: ArbeitsnachweisService,
    private abrechnungsmonatService: AbrechnungsmonatService,
    private benachrichtigungService: BenachrichtigungService,
    private spinnerOverlayService: SpinnerOverlayService,
    private navigationService: NavigationService
  ) {
    this.fakturAuswahlFormGroup = this.fb.nonNullable.group({
      projekt: [{} as Projekt],
      rechnungsempfaenger: [{ value: '', disabled: true }],
      debitorennummer: [{ value: '', disabled: true }],

      gesamt: [{ value: '0,00', disabled: true }],
      laufendesJahr: [{ value: '0,00', disabled: true }],
    });

    this.fakturFormGroup = this.fb.nonNullable.group({
      id: [''],
      rechnungsdatum: ['', Validators.required],
      rechnungsnummer: ['', Validators.required],
      referenzmonat: ['', Validators.required],
      betragNetto: ['', Validators.required],
      nichtBudgetRelevant: [''],
      umsatzsteuer: ['19,00', Validators.required],
      abwRechnungsempfaenger: [{} as Kunde],
      debitorennummer: [''],
      bemerkung: [''],
    });

    this.fakturenListe = new StatusListen([]);
  }

  ngOnInit(): void {
    this.reagiereAufAenderungProjekt();
  }

  ngAfterViewInit(): void {
    this.rufeInitialeDatenAb();
  }

  ngAfterViewChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  istProjektAusgewaehlt(): boolean {
    const projekt: Projekt = this.fakturAuswahlFormGroup.get('projekt')?.value;
    return !!(projekt && projekt.id);
  }

  oeffneHinweisDialog() {
    this.benachrichtigungService.erstelleInfoMessage(this.hinweisText);
  }

  reagiereAufSpeichernEvent(speichernPostAktion: SpeichernPostAktionEnum) {
    const dataSpinner = {
      title: 'Änderungen werden gespeichert...',
    };
    const spinnerOverlay = this.spinnerOverlayService.open(dataSpinner);
    this.verwaltungService.speicherFakturen(this.fakturenListe).subscribe(() => {
      spinnerOverlay.close();
      this.benachrichtigungService.erstelleBenachrichtigung('Faktur wurde erfolgreich gespeichert.');
      this.aktualisiereFakturen(this.fakturAuswahlFormGroup.getRawValue().projekt);
      this.fuehreAktionNachSpeichernAus(speichernPostAktion);
    });
  }

  reagiereAufRechnungssummeAktualisierenEvent() {
    this.aktualisiereRechnungssumme();
  }

  private reagiereAufAenderungProjekt(): void {
    this.fakturAuswahlFormGroup
      .get('projekt')
      ?.valueChanges.pipe(filter((projekt) => projekt === '' || typeof projekt !== 'string'))
      .subscribe((projekt: Projekt) => {
        this.aktualisiereFakturen(projekt);
      });
  }

  private aktualisiereFakturen(projekt: Projekt) {
    this.ladeFakturSpinner.open();

    // Relevante Daten zurücksetzen
    this.fakturenListe = new StatusListen([]);
    this.fakturFormGroup.reset();

    // Ggf. neue Daten laden
    if (projekt && projekt.id) {
      this.verwaltungService.getFakturenByProjektId(projekt.id).subscribe((fakturen: Faktur[]) => {
        this.fakturenListe = new StatusListen(fakturen);
        this.aktualisiereRechnungssumme();
        this.ladeFakturSpinner.close();
      });
    } else {
      this.ladeFakturSpinner.close();
    }
  }

  private aktualisiereRechnungssumme(): void {
    const fakturen: Faktur[] = this.fakturenListe.getAnzeigeListe() as Faktur[];

    if (fakturen.length === 0) {
      this.fakturAuswahlFormGroup.get('laufendesJahr')?.reset();
      this.fakturAuswahlFormGroup.get('gesamt')?.reset();
    }

    const aktuellesJahr: number = new Date().getFullYear();
    let gesamt: number = 0;
    let laufendesJahr: number = 0;
    fakturen.forEach((faktur) => {
      gesamt += faktur.betragNetto;
      if (faktur.referenzJahr === aktuellesJahr) {
        laufendesJahr += faktur.betragNetto;
      }
    });
    this.fakturAuswahlFormGroup.patchValue({
      laufendesJahr: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(laufendesJahr),
      gesamt: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(gesamt),
    });
  }

  private rufeInitialeDatenAb(): void {
    forkJoin([
      this.kundeService.getAlleKunden(),
      this.projektService.getAlleProjekte(),
      this.arbeitsnachweisService.getAlleAbrechnungsmonate(),
    ]).subscribe(([kunden, projekte, abrechnungsmonate]) => {
      this.kundeAlle = kunden.slice();
      this.projektAuswahl = projekte.slice().filter((projekt) => istProjektKunde(projekt));

      this.abrechnungsmonatService.sortiereAbrechnungsmonate(abrechnungsmonate);
      this.abrechnungsmonateAuswahl = abrechnungsmonate.slice();

      this.verarbeiteQueryParameter();
    });
  }

  private verarbeiteQueryParameter(): void {
    this.route.queryParams.subscribe((params) => {
      if (!params['projektId']) {
        return;
      }

      const projektId: string = params['projektId'];
      const projekt: Projekt = this.projektAuswahl.find((projekt) => projekt.id === projektId)!;
      this.fakturAuswahlFormGroup.get('projekt')?.setValue(projekt);
    });
  }

  private fuehreAktionNachSpeichernAus(speichernPostAktion: SpeichernPostAktionEnum) {
    switch (speichernPostAktion) {
      case SpeichernPostAktionEnum.KEINE:
        break;
      case SpeichernPostAktionEnum.SCHLIESSEN:
        this.navigationService.geheZurueck();
        break;
    }
  }
}
