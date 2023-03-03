import { AfterViewChecked, AfterViewInit, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { StatusListen } from '../../../../shared/model/sonstiges/status-listen';
import { SpeichernPostAktionEnum } from '../../../../shared/enum/speichern-post-aktion.enum';
import { SpinnerOverlayService } from '../../../../shared/service/spinner-overlay.service';
import { NavigationService } from '../../../../shared/service/navigation.service';
import { BenachrichtigungService } from '../../../../shared/service/benachrichtigung.service';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProjektService } from '../../../../shared/service/projekt.service';
import { OrganisationseinheitService } from '../../../../shared/service/organisationseinheit.service';
import { VerwaltungService } from '../../../../shared/service/verwaltung.service';
import { ArbeitsnachweisService } from '../../../../shared/service/arbeitsnachweis.service';
import { MitarbeiterService } from '../../../../shared/service/mitarbeiter.service';
import { filter, forkJoin } from 'rxjs';
import { Kunde } from '../../../../shared/model/kunde/kunde';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { Abrechnungsmonat } from '../../../../shared/model/sonstiges/abrechnungsmonat';
import { Organisationseinheit } from '../../../../shared/model/organisationseinheit/organisationseinheit';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { KundeService } from '../../../../shared/service/kunde.service';
import { AbrechnungsmonatService } from '../../../../shared/service/abrechnungsmonat.service';
import { Skonto } from '../../../../shared/model/verwaltung/skonto';
import { convertUsNummerZuDeStringMitGenauZweiNachkommastellen } from '../../../../shared/util/nummer-converter.util';
import { Faktur } from '../../../../shared/model/verwaltung/faktur';

@Component({
  selector: 'pab-skonto-buchung',
  templateUrl: './skonto-buchung.component.html',
  styleUrls: ['./skonto-buchung.component.scss'],
})
export class SkontoBuchungComponent implements OnInit, AfterViewInit, AfterViewChecked {
  ladeSkontosSpinner: SpinnerRef = new SpinnerRef();
  hinweisText: string = 'Es können nur Skonto-Buchunen für Kundenprojekte erfasst werden.';

  mitarbeiterAlle: Mitarbeiter[] = [];
  kundeAlle: Kunde[] = [];
  organisationseinheitAlle: Organisationseinheit[] = [];
  projektAuswahl: Projekt[] = [];
  abrechnungsmonateAuswahl: Abrechnungsmonat[] = [];

  skontoBuchungAuswahlFormGroup: FormGroup;
  skontoBuchungFormGroup: FormGroup;

  skontoBuchungenListe: StatusListen;

  constructor(
    private fb: FormBuilder,
    private changeDetectorRef: ChangeDetectorRef,
    private mitarbeiterService: MitarbeiterService,
    private projektService: ProjektService,
    private kundeService: KundeService,
    private organistationseinheitService: OrganisationseinheitService,
    private verwaltungService: VerwaltungService,
    private arbeitsnachweisService: ArbeitsnachweisService,
    private abrechnungsmonatService: AbrechnungsmonatService,
    private benachrichtigungService: BenachrichtigungService,
    private spinnerOverlayService: SpinnerOverlayService,
    private navigationService: NavigationService
  ) {
    this.skontoBuchungAuswahlFormGroup = this.fb.nonNullable.group({
      projekt: [{} as Projekt],
      kunde: [{ value: '', disabled: true }],
      organisationseinheit: [{ value: '', disabled: true }],
      projekttyp: [{ value: '', disabled: true }],
      projektverantwortung: [{ value: '', disabled: true }],

      skontoNettoSumme: [{ value: '', disabled: true }],
      skontoBruttoSumme: [{ value: '', disabled: true }],
      gesamtRechnungNetto: [{ value: '', disabled: true }],
    });

    this.skontoBuchungFormGroup = this.fb.nonNullable.group({
      id: [''],
      wertstellung: ['', Validators.required],
      referenzmonat: ['', Validators.required],
      bemerkung: [''],
      skontoNettoBetrag: [''],
      umsatzsteuer: ['19,00', Validators.required],
      skontoBruttoBetrag: [''],
    });

    this.skontoBuchungenListe = new StatusListen([]);
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
    const projekt: Projekt = this.skontoBuchungAuswahlFormGroup.get('projekt')?.value;
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
    spinnerOverlay.close();
    this.benachrichtigungService.erstelleBenachrichtigung('Skonto-Buchungen wurde erfolgreich gespeichert.');
    this.fuehreAktionNachSpeichernAus(speichernPostAktion);
  }

  reagiereAufSummenAktualisierenEvent() {
    this.aktualisiereSummen();
  }

  private reagiereAufAenderungProjekt(): void {
    this.skontoBuchungAuswahlFormGroup
      .get('projekt')
      ?.valueChanges.pipe(filter((projekt) => projekt === '' || typeof projekt !== 'string'))
      .subscribe((projekt: Projekt) => {
        this.ladeSkontosSpinner.open();

        // Relevante Daten zurücksetzen
        this.skontoBuchungenListe = new StatusListen([]);
        this.skontoBuchungFormGroup.reset();

        // Ggf. neue Daten laden
        if (!projekt || !projekt.id) {
          this.aktualisiereSummen();
          this.ladeSkontosSpinner.close();
          return;
        }

        this.verwaltungService.getSkontosByProjektId(projekt.id).subscribe((skontos: Skonto[]) => {
          this.skontoBuchungenListe = new StatusListen(skontos);
          this.aktualisiereSummen();
          this.ladeSkontosSpinner.close();
        });
      });
  }

  private aktualisiereSummen(): void {
    // Gesamtrechnung benötigt Faktur-Daten
    if (this.istProjektAusgewaehlt()) {
      this.verwaltungService
        .getFakturenByProjektId(this.skontoBuchungAuswahlFormGroup.getRawValue().projekt.id)
        .subscribe((fakturen: Faktur[]) => {
          const gesamtRechnungNetto: number = fakturen.reduce((sum, faktur) => (sum += faktur.betragNetto), 0);
          this.skontoBuchungAuswahlFormGroup.patchValue({
            gesamtRechnungNetto: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(gesamtRechnungNetto),
          });
        });
    } else {
      this.skontoBuchungAuswahlFormGroup.get('gesamtRechnungNetto')?.reset();
    }

    // Skonto Netto&rutto summieren
    const skontos: Skonto[] = this.skontoBuchungenListe.getAnzeigeListe() as Skonto[];

    if (skontos.length === 0) {
      this.skontoBuchungAuswahlFormGroup.get('skontoNettoSumme')?.reset();
      this.skontoBuchungAuswahlFormGroup.get('skontoBruttoSumme')?.reset();
      return;
    }

    const { skontoNettoSumme, skontoBruttoSumme } = skontos.reduce(
      (summe, skonto) => {
        summe.skontoNettoSumme += skonto.skontoNettoBetrag;
        summe.skontoBruttoSumme += skonto.skontoNettoBetrag * (1 + skonto.umsatzsteuer / 100);
        return summe;
      },
      { skontoNettoSumme: 0, skontoBruttoSumme: 0 }
    );

    this.skontoBuchungAuswahlFormGroup.patchValue({
      skontoNettoSumme: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(skontoNettoSumme),
      skontoBruttoSumme: convertUsNummerZuDeStringMitGenauZweiNachkommastellen(skontoBruttoSumme),
    });
  }

  private rufeInitialeDatenAb(): void {
    forkJoin([
      this.mitarbeiterService.getAlleMitarbeiter(),
      this.kundeService.getAlleKunden(),
      this.organistationseinheitService.getAlleOrganisationseinheiten(),
      this.arbeitsnachweisService.getAlleAbrechnungsmonate(),
      this.projektService.getAlleProjekte(),
    ]).subscribe(([mitarbeiter, kunden, organisationseinheiten, abrechnungsmonate, projekte]) => {
      this.mitarbeiterAlle = mitarbeiter.slice();
      this.kundeAlle = kunden.slice();
      this.organisationseinheitAlle = organisationseinheiten.slice();

      this.projektAuswahl = projekte.slice();

      this.abrechnungsmonatService.sortiereAbrechnungsmonate(abrechnungsmonate);
      this.abrechnungsmonateAuswahl = abrechnungsmonate.slice();
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
