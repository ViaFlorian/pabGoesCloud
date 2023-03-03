import { AfterViewChecked, AfterViewInit, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { FormBuilder, FormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { MitarbeiterService } from '../../../../shared/service/mitarbeiter.service';
import { StatusListen } from '../../../../shared/model/sonstiges/status-listen';
import { KonstantenService } from '../../../../shared/service/konstanten.service';
import { BuchungstypStunden } from '../../../../shared/model/konstanten/buchungstyp-stunden';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { entferneUnerlaubteZeichenAusFormGroupKomponente } from '../../../../shared/util/regex-control.util';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { MitarbeiterStundenKonto } from '../../../../shared/model/mitarbeiter/mitarbeiter-stunden-konto';
import { BuchungstypStundenArtenEnum } from '../../../../shared/enum/buchungstyp-stunden-arten.enum';
import { IdGeneratorService } from '../../../../shared/service/id-generator.service';
import { ArbeitsnachweisService } from '../../../../shared/service/arbeitsnachweis.service';
import { BenutzerService } from '../../../../shared/service/benutzer.service';
import { BenachrichtigungService } from '../../../../shared/service/benachrichtigung.service';
import { ActivatedRoute } from '@angular/router';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { AbrechnungsmonatService } from '../../../../shared/service/abrechnungsmonat.service';
import { convertDeStringZuUsNummer } from '../../../../shared/util/nummer-converter.util';
import { SpeichernPostAktionEnum } from '../../../../shared/enum/speichern-post-aktion.enum';
import { NavigationService } from '../../../../shared/service/navigation.service';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';
import { filter } from 'rxjs';

@Component({
  selector: 'pab-stundenkonto-uebersicht',
  templateUrl: './stundenkonto.component.html',
  styleUrls: ['./stundenkonto.component.scss'],
  providers: [
    {
      provide: MAT_DATE_FORMATS,
      useValue: {
        parse: {
          dateInput: 'dd.MM.yyyy',
        },
        display: {
          dateInput: 'dd.MM.yyyy',
          monthYearLabel: 'MMM y',
          dateA11yLabel: 'LL',
          monthYearA11yLabel: 'MMMM y',
        },
      },
    },
  ],
})
export class StundenkontoComponent implements OnInit, AfterViewInit, AfterViewChecked {
  ladeStundenkontoSpinner: SpinnerRef = new SpinnerRef();

  mitarbeiterAuswahlFormGroup: FormGroup;
  stundenkontoFormGroup: FormGroup;

  mitarbeiterAuswahl: Mitarbeiter[] = [];
  buchungstypStunden: BuchungstypStunden[] = [];
  stundenkontoListe: StatusListen;
  aktuellerAbrechnungsmonat: Date;

  tabelle: TableData<MitarbeiterStundenKonto>;
  spalten = [
    { id: 'wertstellung', name: 'Wertstellung' },
    { id: 'buchungstyp', name: 'Buchungstyp' },
    { id: 'bemerkung', name: 'Bemerkung' },
    { id: 'anzahlStunden', name: 'Anzahl Stunden' },
    { id: 'saldo', name: 'Saldo' },
  ];

  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  @ViewChild(FormGroupDirective) formRef!: FormGroupDirective;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private changeDetectorRef: ChangeDetectorRef,
    private abrechnungsmonatService: AbrechnungsmonatService,
    private mitarbeiterService: MitarbeiterService,
    private konstantenService: KonstantenService,
    private idGeneratorService: IdGeneratorService,
    private arbeitsnachweisService: ArbeitsnachweisService,
    private benutzerService: BenutzerService,
    private benachrichtungService: BenachrichtigungService,
    private navigationService: NavigationService
  ) {
    this.stundenkontoListe = new StatusListen([]);

    this.aktuellerAbrechnungsmonat = this.abrechnungsmonatService.getAktuellenAbrechnungsmonatAsDate();

    this.mitarbeiterAuswahlFormGroup = this.fb.nonNullable.group({
      mitarbeiter: [{} as Mitarbeiter],
    });

    this.stundenkontoFormGroup = this.fb.nonNullable.group({
      wertstellung: ['', [Validators.required, Validators.min(this.aktuellerAbrechnungsmonat.getDate())]],
      anzahlStunden: ['', Validators.required],
      buchungstyp: [{} as BuchungstypStunden, Validators.required],
      bemerkung: ['', Validators.required],
    });
    this.aendereAktivierungsstatusStundenkontoFormGroup(false);

    this.tabelle = new TableData<MitarbeiterStundenKonto>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<MitarbeiterStundenKonto>(new MatMultiSort(), true);
  }

  ngOnInit(): void {
    this.reagiereAufAenderungMitarbeiter();
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenDaten();
    this.initialisiereTabellenSortierung();
    this.rufeInitialeDatenAb();
  }

  ngAfterViewChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  fuegeNeuesElementHinzu(): void {
    if (this.stundenkontoFormGroup.invalid) {
      this.stundenkontoFormGroup.markAllAsTouched();
      return;
    }

    const stundenkonto: MitarbeiterStundenKonto = this.erstelleStundenKontoAusForm();
    this.stundenkontoListe.fuegeNeuesElementHinzu(stundenkonto);
    this.leereForm();
    const neueListe = this.stundenkontoListe.getNeuListe() as MitarbeiterStundenKonto[];
    this.berechneNeuenSaldoUndAktualisiereStundenkontoListe(neueListe);
  }

  leereForm(): void {
    this.stundenkontoFormGroup.reset();
    setTimeout(() => this.formRef.resetForm(), 100);
  }

  entferneUnerlaubteZeichen(komponentenName: string, erlaubteExp: string): void {
    entferneUnerlaubteZeichenAusFormGroupKomponente(this.stundenkontoFormGroup, komponentenName, erlaubteExp);
  }

  reagiereAufSpeichernEvent(speichernPostAktion: SpeichernPostAktionEnum): void {
    this.mitarbeiterService
      .speichernStundenkontoVonMitarbeiter(
        this.mitarbeiterAuswahlFormGroup.getRawValue().mitarbeiter.id,
        this.stundenkontoListe.getNeuListe() as MitarbeiterStundenKonto[]
      )
      .subscribe(() => {
        this.benachrichtungService.erstelleBenachrichtigung('Stundenkonto wurde erfolgreich gespeichert.');
        this.aktualisiereStundenkontoListe(this.mitarbeiterAuswahlFormGroup.getRawValue().mitarbeiter);
        this.fuehreAktionNachSpeichernAus(speichernPostAktion);
      });
  }

  private reagiereAufAenderungMitarbeiter(): void {
    this.mitarbeiterAuswahlFormGroup
      .get('mitarbeiter')
      ?.valueChanges.pipe(filter((mitarbeiter) => mitarbeiter === '' || typeof mitarbeiter !== 'string'))
      .subscribe((mitarbeiter: Mitarbeiter) => {
        this.aktualisiereStundenkontoListe(mitarbeiter);
      });
  }

  private rufeInitialeDatenAb(): void {
    this.mitarbeiterService.getMitarbeiterAuswahlFuerMitarbeiterKonten().subscribe((mitarbeiter) => {
      this.mitarbeiterAuswahl = mitarbeiter.slice();
      this.verarbeiteQueryParameter();
    });

    this.konstantenService.getBuchungsTypStundenAll().subscribe((buchungstypStunden) => {
      this.buchungstypStunden = buchungstypStunden.slice();
      this.buchungstypStunden = buchungstypStunden.filter((value) => {
        return value.bezeichnung !== BuchungstypStundenArtenEnum.UEBERTRAG;
      });
    });

    this.aktuellerAbrechnungsmonat = this.abrechnungsmonatService.getAktuellenAbrechnungsmonatAsDate();
  }

  private aktualisiereStundenkontoListe(mitarbeiter: Mitarbeiter): void {
    this.ladeStundenkontoSpinner.open();

    // Relevante Daten zurücksetzen
    this.stundenkontoListe = new StatusListen([]);
    this.stundenkontoFormGroup.reset();

    if (!mitarbeiter || !mitarbeiter.id) {
      this.aendereAktivierungsstatusStundenkontoFormGroup(false);
      this.ladeStundenkontoSpinner.close();
      return;
    }

    this.mitarbeiterService.getStundenkontoVonMitarbeiter(mitarbeiter.id).subscribe((stundenkonto) => {
      this.stundenkontoListe = new StatusListen(stundenkonto);
      this.tabelle.data = this.stundenkontoListe.getAnzeigeListe() as MitarbeiterStundenKonto[];
      this.aendereAktivierungsstatusStundenkontoFormGroup(true);
      this.ladeStundenkontoSpinner.close();
    });
  }

  private aendereAktivierungsstatusStundenkontoFormGroup(mitarbeiterAusgewaelt: boolean): void {
    if (mitarbeiterAusgewaelt) {
      this.stundenkontoFormGroup.enable();
    } else {
      this.stundenkontoFormGroup.disable();
    }
  }

  private initialisiereTabellenDaten() {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<MitarbeiterStundenKonto>(this.sort, true);
    this.tabelle.data = this.stundenkontoListe.getAnzeigeListe() as MitarbeiterStundenKonto[];
  }

  private initialisiereTabellenSortierung(): void {
    this.tabelle.sortParams = ['wertstellung', 'buchungsdatum'];
    this.tabelle.sortDirs = ['desc'];

    this.tabelle.updateSortHeaders();
  }

  private erstelleStundenKontoAusForm(): MitarbeiterStundenKonto {
    return {
      id: this.idGeneratorService.generiereId(),
      mitarbeiterId: this.mitarbeiterAuswahlFormGroup.getRawValue().mitarbeiter.id,
      wertstellung: this.stundenkontoFormGroup.getRawValue().wertstellung,
      buchungsdatum: new Date(),
      anzahlStunden: this.negiereAnzahlStundenAbheangigVonBuchungsTyp(
        convertDeStringZuUsNummer(this.stundenkontoFormGroup.getRawValue().anzahlStunden),
        this.stundenkontoFormGroup.getRawValue().buchungstyp
      ),
      lfdSaldo: 0,
      buchungstypStundenId: this.stundenkontoFormGroup.getRawValue().buchungstyp.id,
      automatisch: false,
      endgueltig: false,
      zuletztGeaendertVon: this.benutzerService.getBenutzerNamen(),
      bemerkung: this.stundenkontoFormGroup.getRawValue().bemerkung,
      geaendert: false,
    };
  }

  private negiereAnzahlStundenAbheangigVonBuchungsTyp(
    anzahlStunden: number,
    buchungsTypStunden: BuchungstypStunden
  ): number {
    if (
      buchungsTypStunden.bezeichnung === BuchungstypStundenArtenEnum.SOLLSTUNDEN ||
      buchungsTypStunden.bezeichnung === BuchungstypStundenArtenEnum.AUSZAHLUNG ||
      buchungsTypStunden.bezeichnung === BuchungstypStundenArtenEnum.AUSZAHLUNG_SONDERARBEITSZEIT
    ) {
      return -1 * anzahlStunden;
    } else {
      return anzahlStunden;
    }
  }

  private berechneNeuenSaldoUndAktualisiereStundenkontoListe(neuStundenkontoListe: MitarbeiterStundenKonto[]) {
    this.mitarbeiterService
      .getMitarbeiterStundenkontoMitNeuemSaldo(
        this.mitarbeiterAuswahlFormGroup.getRawValue().mitarbeiter.id,
        neuStundenkontoListe
      )
      .subscribe((stundenkontoSaldoListe) => {
        const stundenKontoListeUnveraendert = stundenkontoSaldoListe.filter((stundenkonto) => !stundenkonto.geaendert);
        const stundenKontoListeGeandert = stundenkontoSaldoListe.filter(
          (stundenkonto) => stundenkonto.geaendert && Number(stundenkonto.id) >= 0
        );
        const stundenKontoListeNeu = stundenkontoSaldoListe.filter(
          (stundenkonto) => stundenkonto.geaendert && Number(stundenkonto.id) < 0
        );

        this.stundenkontoListe = new StatusListen(
          stundenKontoListeUnveraendert,
          stundenKontoListeGeandert,
          stundenKontoListeNeu
        );
        this.tabelle.data = this.stundenkontoListe.getAnzeigeListe() as MitarbeiterStundenKonto[];
      });
  }

  private verarbeiteQueryParameter(): void {
    this.route.queryParams.subscribe((params) => {
      if (!params['mitarbeiterId']) {
        return;
      }

      const mitarbeiterId = params['mitarbeiterId'];
      const mitarbeiter: Mitarbeiter = getObjektAusListeDurchId(mitarbeiterId, this.mitarbeiterAuswahl) as Mitarbeiter;

      this.mitarbeiterAuswahlFormGroup.get('mitarbeiter')!.setValue(mitarbeiter);
    });
  }

  private fuehreAktionNachSpeichernAus(speichernPostAktion: SpeichernPostAktionEnum): void {
    switch (speichernPostAktion) {
      case SpeichernPostAktionEnum.KEINE:
        break;
      case SpeichernPostAktionEnum.SCHLIESSEN:
        this.navigationService.geheZurueck();
        break;
    }
  }
}
