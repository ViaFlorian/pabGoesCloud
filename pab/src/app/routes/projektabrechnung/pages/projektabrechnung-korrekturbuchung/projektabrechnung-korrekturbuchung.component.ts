import { AfterViewChecked, AfterViewInit, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Abrechnungsmonat } from '../../../../shared/model/sonstiges/abrechnungsmonat';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { Kostenart } from '../../../../shared/model/konstanten/kostenart';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { Kunde } from '../../../../shared/model/kunde/kunde';
import { Organisationseinheit } from '../../../../shared/model/organisationseinheit/organisationseinheit';
import { ProjektService } from '../../../../shared/service/projekt.service';
import { KundeService } from '../../../../shared/service/kunde.service';
import { OrganisationseinheitService } from '../../../../shared/service/organisationseinheit.service';
import { StatusListen } from '../../../../shared/model/sonstiges/status-listen';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';
import { MitarbeiterService } from '../../../../shared/service/mitarbeiter.service';
import { KonstantenService } from '../../../../shared/service/konstanten.service';
import { SpinnerOverlayService } from '../../../../shared/service/spinner-overlay.service';
import { ArbeitsnachweisService } from '../../../../shared/service/arbeitsnachweis.service';
import { BehaviorSubject, catchError, filter, forkJoin } from 'rxjs';
import { AbrechnungsmonatService } from '../../../../shared/service/abrechnungsmonat.service';
import { ProjektabrechnungService } from '../../../../shared/service/projektabrechnung.service';
import { ProjektabrechnungKorrekturbuchung } from '../../../../shared/model/projektabrechnung/projektabrechnung-korrekturbuchung';
import { IdGeneratorService } from '../../../../shared/service/id-generator.service';
import { ProjektabrechnungKorrekturbuchungVorgang } from '../../../../shared/model/projektabrechnung/projektabrechnung-korrekturbuchung-vorgang';
import { BenachrichtigungService } from '../../../../shared/service/benachrichtigung.service';
import { ValdiatorError } from '../../../../shared/model/error/valdiator-error';
import { ErrorService } from '../../../../shared/service/error.service';

@Component({
  selector: 'pab-projektabrechnung-korrekturbuchung',
  templateUrl: './projektabrechnung-korrekturbuchung.component.html',
  styleUrls: ['./projektabrechnung-korrekturbuchung.component.scss'],
})
export class ProjektabrechnungKorrekturbuchungComponent implements OnInit, AfterViewInit, AfterViewChecked {
  ueberschrift = 'Korrektur/Umbuchung Kosten/Leistung';

  korrekturbuchungFilterGroup: FormGroup;

  projektAuswahl: Projekt[] = [];
  kundeAuswahl: Kunde[] = [];
  organisationseinheitAuswahl: Organisationseinheit[] = [];

  mitarbeiterAuswahl: Mitarbeiter[] = [];
  kostenartAuswahl: Kostenart[] = [];

  abrechnungsmonateAuswahl: Abrechnungsmonat[] = [];

  projektabrechnungKorrekturbuchungsListe: StatusListen;
  projektabrechnungKorrekturbuchungTabellenAnzeige: ProjektabrechnungKorrekturbuchung[] = [];

  formKorrekturbuchungsVorgangObs: BehaviorSubject<ProjektabrechnungKorrekturbuchungVorgang | undefined> =
    new BehaviorSubject<ProjektabrechnungKorrekturbuchungVorgang | undefined>(undefined);

  projektabrechnungKorrekturbuchungSpinner: SpinnerRef = new SpinnerRef();

  constructor(
    private fb: FormBuilder,
    private changeDetectorRef: ChangeDetectorRef,
    private projektService: ProjektService,
    private kundeService: KundeService,
    private organistationseinheitService: OrganisationseinheitService,
    private mitarbeiterService: MitarbeiterService,
    private konstantenService: KonstantenService,
    private arbeitsnachweisService: ArbeitsnachweisService,
    private abrechnungsmonatService: AbrechnungsmonatService,
    private projektabrechnungService: ProjektabrechnungService,
    private spinnerOverlayService: SpinnerOverlayService,
    private idGeneratorService: IdGeneratorService,
    private benachrichtigungService: BenachrichtigungService,
    private errorService: ErrorService
  ) {
    this.korrekturbuchungFilterGroup = this.fb.nonNullable.group({
      projekt: [{} as Projekt],
      vorherigesProjekt: [{} as Projekt],
      kunde: [{ value: '', disabled: true }],
      organisationseinheit: [{ value: '', disabled: true }],
    });

    this.projektabrechnungKorrekturbuchungsListe = new StatusListen([]);
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

  reagiereAufBuchungsAuswahlInTabelle(ausgewaehlteKorrekturbuchung: ProjektabrechnungKorrekturbuchung) {
    if (!ausgewaehlteKorrekturbuchung.istKorrekturbuchung) {
      this.passeAbrechnungsmonatUndReferenzmonatAn(ausgewaehlteKorrekturbuchung);
    }
    if (ausgewaehlteKorrekturbuchung.gegenbuchungID === null) {
      this.formKorrekturbuchungsVorgangObs.next({
        korrekturbuchung: ausgewaehlteKorrekturbuchung,
        gegenbuchung: undefined,
      });
      return;
    }

    const gegenbuchung: ProjektabrechnungKorrekturbuchung | undefined = (
      this.projektabrechnungKorrekturbuchungsListe.getNeuListe() as ProjektabrechnungKorrekturbuchung[]
    ).find(
      (korrekturbuchungen: ProjektabrechnungKorrekturbuchung) =>
        korrekturbuchungen.gegenbuchungID === ausgewaehlteKorrekturbuchung.gegenbuchungID &&
        korrekturbuchungen.id !== ausgewaehlteKorrekturbuchung.id
    );
    if (gegenbuchung) {
      this.formKorrekturbuchungsVorgangObs.next({
        korrekturbuchung: ausgewaehlteKorrekturbuchung,
        gegenbuchung: gegenbuchung,
      });
    } else {
      this.projektabrechnungService
        .getProjektabrechnungKorrekturbuchungGegenbuchung(
          ausgewaehlteKorrekturbuchung.id,
          ausgewaehlteKorrekturbuchung.gegenbuchungID
        )
        .subscribe((gegenbuchung) => {
          this.formKorrekturbuchungsVorgangObs.next({
            korrekturbuchung: ausgewaehlteKorrekturbuchung,
            gegenbuchung: gegenbuchung,
          });
        });
    }
  }

  reagiereAufProjektabrechnungKorrekturbuchungHinzufuegen(
    projektabrechnungKorrekturbuchungsVorgang: ProjektabrechnungKorrekturbuchungVorgang
  ) {
    this.projektabrechnungService
      .erstelleNeueProjektabrechnungKorrekturbuchung(projektabrechnungKorrekturbuchungsVorgang)
      .pipe(
        catchError((error: any) => {
          if (error instanceof ValdiatorError) {
            this.errorService.zeigeValidatorFehlerAn(error);
            this.formKorrekturbuchungsVorgangObs.next(projektabrechnungKorrekturbuchungsVorgang);
          }
          throw error;
        })
      )
      .subscribe((response: ProjektabrechnungKorrekturbuchungVorgang) => {
        response.korrekturbuchung.id = this.idGeneratorService.generiereId();
        this.projektabrechnungKorrekturbuchungsListe.fuegeNeuesElementHinzu(response.korrekturbuchung);
        if (response.gegenbuchung?.projektId) {
          response.gegenbuchung!.id = this.idGeneratorService.generiereId();
          this.projektabrechnungKorrekturbuchungsListe.fuegeNeuesElementHinzu(response.gegenbuchung!);
        }

        this.ermittleUndSetzeKorrekturbuchungenZurAnzeige();
      });
  }

  speichern() {
    const data = {
      title: 'Änderungen werden gespeichert...',
    };
    const spinnerOverlay = this.spinnerOverlayService.open(data);
    this.projektabrechnungService
      .speichereKorrekturbuchungen(
        this.projektabrechnungKorrekturbuchungsListe.getNeuListe() as ProjektabrechnungKorrekturbuchung[]
      )
      .pipe(
        catchError((error: any) => {
          if (error instanceof ValdiatorError) {
            spinnerOverlay.close();
            error.message = `${error.message} Die Änderungen werden verworfen.`;
            this.errorService.zeigeValidatorFehlerAn(error);
            this.ladeOderLeereProjektabrechnungKorrekturDaten(this.korrekturbuchungFilterGroup.getRawValue().projekt);
          }

          throw error;
        })
      )
      .subscribe((meldungen: string[]) => {
        spinnerOverlay.close();

        this.benachrichtigungService.erstelleBenachrichtigung(...meldungen);
        this.ladeOderLeereProjektabrechnungKorrekturDaten(this.korrekturbuchungFilterGroup.getRawValue().projekt);
      });
  }

  istAenderungVorhanden() {
    return this.projektabrechnungKorrekturbuchungsListe.istVeraendert();
  }

  ermittleUndSetzeKorrekturbuchungenZurAnzeige() {
    const projekt: Projekt = this.korrekturbuchungFilterGroup.getRawValue().projekt;
    if (!projekt || !projekt.id) {
      this.projektabrechnungKorrekturbuchungTabellenAnzeige = [];
    }
    this.projektabrechnungKorrekturbuchungTabellenAnzeige = (
      this.projektabrechnungKorrekturbuchungsListe.getAnzeigeListe() as ProjektabrechnungKorrekturbuchung[]
    ).filter((korrekturbuchung) => korrekturbuchung.projektId === projekt.id);
  }

  private reagiereAufAenderungProjekt() {
    this.korrekturbuchungFilterGroup
      .get('projekt')
      ?.valueChanges.pipe(filter((projekt) => projekt === '' || typeof projekt !== 'string'))
      .subscribe((projekt) => {
        if (projekt === this.korrekturbuchungFilterGroup.getRawValue().vorherigesProjekt) {
          return;
        }
        if (this.projektabrechnungKorrekturbuchungsListe.istVeraendert()) {
          this.wechselNurBeiNutzerZustimmung(projekt);
          return;
        }
        // Speicher aktuelles Projekt als vorheriges Projekt für nächste Änderung
        this.korrekturbuchungFilterGroup.get('vorherigesProjekt')?.setValue(projekt);
        this.ladeOderLeereProjektabrechnungKorrekturDaten(projekt);
      });
  }

  private ladeOderLeereProjektabrechnungKorrekturDaten(projekt: Projekt) {
    this.setzeDatenZurueck();

    if (!projekt || !projekt.id) {
      return;
    }

    this.projektabrechnungKorrekturbuchungSpinner.open();

    this.projektabrechnungService
      .getProjektabrechnungKorrekturbuchungByProjektId(projekt)
      .subscribe((projektabrechnungKorrekturbuchungen) => {
        this.projektabrechnungKorrekturbuchungsListe = new StatusListen(projektabrechnungKorrekturbuchungen);
        this.ermittleUndSetzeKorrekturbuchungenZurAnzeige();

        //Erstelle und übergebe korrekturbuchungsvorgang an Form componente um Form zu füllen
        const aktuellerAbrechnungsmonat = this.abrechnungsmonatService.getAktuellenAbrechnungsmonat();
        const projektabrechnungKorrekturbuchungVorgang: ProjektabrechnungKorrekturbuchungVorgang = {
          korrekturbuchung: {
            projektId: projekt.id,
          } as ProjektabrechnungKorrekturbuchung,
          gegenbuchung: undefined,
        };
        projektabrechnungKorrekturbuchungVorgang.korrekturbuchung.jahr = aktuellerAbrechnungsmonat.jahr;
        projektabrechnungKorrekturbuchungVorgang.korrekturbuchung.monat = aktuellerAbrechnungsmonat.monat;
        this.formKorrekturbuchungsVorgangObs.next(projektabrechnungKorrekturbuchungVorgang);

        this.projektabrechnungKorrekturbuchungSpinner.close();
      });
  }

  private rufeInitialeDatenAb(): void {
    const data = {
      title: 'Daten werden geladen...',
    };
    const spinnerOverlay = this.spinnerOverlayService.open(data);
    forkJoin([
      this.projektService.getAlleProjekte(),
      this.kundeService.getAlleKunden(),
      this.organistationseinheitService.getAlleOrganisationseinheiten(),
      this.mitarbeiterService.getMitarbeiterAuswahlFuerArbeitsnachweisBearbeiten(),
      this.konstantenService.getKostenartAll(),
      this.arbeitsnachweisService.getAlleAbrechnungsmonate(),
    ]).subscribe(([projekte, kunden, organisationseinheiten, mitarbeiter, kostenart, abrechnungsmonate]) => {
      this.projektAuswahl = projekte.slice();
      this.kundeAuswahl = kunden.slice();
      this.organisationseinheitAuswahl = organisationseinheiten.slice();
      this.mitarbeiterAuswahl = mitarbeiter.slice();
      this.kostenartAuswahl = kostenart.slice();
      this.abrechnungsmonatService.sortiereAbrechnungsmonate(abrechnungsmonate);
      this.abrechnungsmonateAuswahl = abrechnungsmonate;
      spinnerOverlay.close();
    });
  }

  private passeAbrechnungsmonatUndReferenzmonatAn(ausgewaehlteKorrekturbuchung: ProjektabrechnungKorrekturbuchung) {
    ausgewaehlteKorrekturbuchung.referenzJahr = ausgewaehlteKorrekturbuchung.jahr;
    ausgewaehlteKorrekturbuchung.referenzMonat = ausgewaehlteKorrekturbuchung.monat;

    const aktuellerAbrechnungsmonat: Abrechnungsmonat = this.abrechnungsmonatService.getAktuellenAbrechnungsmonat();
    ausgewaehlteKorrekturbuchung.jahr = aktuellerAbrechnungsmonat.jahr;
    ausgewaehlteKorrekturbuchung.monat = aktuellerAbrechnungsmonat.monat;
  }

  private wechselNurBeiNutzerZustimmung(projekt: Projekt) {
    const titel: string = 'Änderungen verwerfen?';
    const message: string = 'Sollen die ungesicherten Änderungen verworfen werden?';
    const dialogRef = this.benachrichtigungService.erstelleBestaetigungMessage(message, titel);
    dialogRef.afterClosed().subscribe((result) => {
      if (!result) {
        // Setze alte Werte wieder in Form und breche ab
        this.korrekturbuchungFilterGroup.patchValue({
          projekt: this.korrekturbuchungFilterGroup.getRawValue().vorherigesProjekt,
        });
      } else {
        // Speicher aktuelles Projekt als vorheriges Projekt für nächsten Change
        this.korrekturbuchungFilterGroup.get('vorherigesProjekt')?.setValue(projekt);
        this.ladeOderLeereProjektabrechnungKorrekturDaten(projekt);
      }
    });
  }

  private setzeDatenZurueck() {
    this.projektabrechnungKorrekturbuchungsListe = new StatusListen([]);
    this.ermittleUndSetzeKorrekturbuchungenZurAnzeige();
    this.formKorrekturbuchungsVorgangObs.next(undefined);
  }
}
