import { AfterViewChecked, AfterViewInit, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { FormBuilder, FormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { MitarbeiterService } from '../../../../shared/service/mitarbeiter.service';
import { StatusListen } from '../../../../shared/model/sonstiges/status-listen';
import { KonstantenService } from '../../../../shared/service/konstanten.service';
import { BuchungstypUrlaub } from '../../../../shared/model/konstanten/buchungstyp-urlaub';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { entferneUnerlaubteZeichenAusFormGroupKomponente } from '../../../../shared/util/regex-control.util';
import { MitarbeiterUrlaubskonto } from '../../../../shared/model/mitarbeiter/mitarbeiter-urlaubskonto';
import { IdGeneratorService } from '../../../../shared/service/id-generator.service';
import { ArbeitsnachweisService } from '../../../../shared/service/arbeitsnachweis.service';
import { BuchungstypUrlaubArtenEnum } from '../../../../shared/enum/buchungstyp-urlaub-arten.enum';
import { BenachrichtigungService } from '../../../../shared/service/benachrichtigung.service';
import { BenutzerService } from '../../../../shared/service/benutzer.service';
import { AbrechnungsmonatService } from '../../../../shared/service/abrechnungsmonat.service';
import { convertDeStringZuUsNummer } from '../../../../shared/util/nummer-converter.util';
import { SpeichernPostAktionEnum } from '../../../../shared/enum/speichern-post-aktion.enum';
import { NavigationService } from '../../../../shared/service/navigation.service';
import { filter } from 'rxjs';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';

@Component({
  selector: 'pab-urlaubskonto',
  templateUrl: './urlaubskonto.component.html',
  styleUrls: ['./urlaubskonto.component.scss'],
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
export class UrlaubskontoComponent implements OnInit, AfterViewInit, AfterViewChecked {
  ladeUrlaubskontoSpinner: SpinnerRef = new SpinnerRef();

  mitarbeiterAuswahlFormGroup: FormGroup;
  urlaubskontoFormGroup: FormGroup;

  mitarbeiterAuswahl: Mitarbeiter[] = [];
  buchungstypUrlaub: BuchungstypUrlaub[] = [];
  urlaubskontoListe: StatusListen;
  aktuellerAbrechnungsmonat: Date;

  tabelle: TableData<MitarbeiterUrlaubskonto>;
  spalten = [
    { id: 'wertstellung', name: 'Wertstellung' },
    { id: 'buchungstyp', name: 'Buchungstyp' },
    { id: 'bemerkung', name: 'Bemerkung' },
    { id: 'anzahlTage', name: 'Anzahl Tage' },
    { id: 'saldo', name: 'Saldo' },
  ];

  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  @ViewChild(FormGroupDirective) formRef!: FormGroupDirective;

  constructor(
    private fb: FormBuilder,
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
    this.urlaubskontoListe = new StatusListen([]);

    this.aktuellerAbrechnungsmonat = this.abrechnungsmonatService.getAktuellenAbrechnungsmonatAsDate();

    this.mitarbeiterAuswahlFormGroup = this.fb.nonNullable.group({
      mitarbeiter: [{} as Mitarbeiter],
    });

    this.urlaubskontoFormGroup = this.fb.nonNullable.group({
      wertstellung: ['', [Validators.required, Validators.min(this.aktuellerAbrechnungsmonat.getDate())]],
      anzahlTage: ['', Validators.required],
      buchungstyp: [{} as BuchungstypUrlaub, Validators.required],
      bemerkung: ['', Validators.required],
    });
    this.aendereAktivierungsstatusUrlaubskontoFormGroup(false);

    this.tabelle = new TableData<MitarbeiterUrlaubskonto>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<MitarbeiterUrlaubskonto>(new MatMultiSort(), true);
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
    if (this.urlaubskontoFormGroup.invalid) {
      this.urlaubskontoFormGroup.markAllAsTouched();
      return;
    }

    const urlaubskonto: MitarbeiterUrlaubskonto = this.erstelleUrlaubsKontoAusForm();
    this.urlaubskontoListe.fuegeNeuesElementHinzu(urlaubskonto);
    this.leereForm();
    const neueUrlaubskontoEintraege = this.urlaubskontoListe.getNeuListe() as MitarbeiterUrlaubskonto[];
    this.berechneNeuenSaldoUndAktualisiereUrlaubskontoListe(neueUrlaubskontoEintraege);
  }

  leereForm(): void {
    this.urlaubskontoFormGroup.reset();
    setTimeout(() => this.formRef.resetForm(), 100);
  }

  entferneUnerlaubteZeichen(komponentenName: string, erlaubteExp: string): void {
    entferneUnerlaubteZeichenAusFormGroupKomponente(this.urlaubskontoFormGroup, komponentenName, erlaubteExp);
  }

  reagiereAufSpeichernEvent(speichernPostAktion: SpeichernPostAktionEnum): void {
    this.mitarbeiterService
      .speichernUrlaubskontoVonMitarbeiter(
        this.mitarbeiterAuswahlFormGroup.getRawValue().mitarbeiter.id,
        this.urlaubskontoListe.getNeuListe() as MitarbeiterUrlaubskonto[]
      )
      .subscribe(() => {
        this.benachrichtungService.erstelleBenachrichtigung('Urlaubskonto wurde erfolgreich gespeichert.');
        this.aktualisiereUrlaubskontoListe(this.mitarbeiterAuswahlFormGroup.getRawValue().mitarbeiter);
        this.fuehreAktionNachSpeichernAus(speichernPostAktion);
      });
  }

  private reagiereAufAenderungMitarbeiter(): void {
    this.mitarbeiterAuswahlFormGroup
      .get('mitarbeiter')
      ?.valueChanges.pipe(filter((mitarbeiter) => mitarbeiter === '' || typeof mitarbeiter !== 'string'))
      .subscribe((mitarbeiter: Mitarbeiter) => {
        this.aktualisiereUrlaubskontoListe(mitarbeiter);
      });
  }

  private rufeInitialeDatenAb(): void {
    this.mitarbeiterService.getMitarbeiterAuswahlFuerMitarbeiterKonten().subscribe((mitarbeiter) => {
      this.mitarbeiterAuswahl = mitarbeiter.slice();
    });

    this.konstantenService.getBuchungstypUrlaubAll().subscribe((buchungstypUrlaub) => {
      this.buchungstypUrlaub = buchungstypUrlaub.slice();
    });

    this.aktuellerAbrechnungsmonat = this.abrechnungsmonatService.getAktuellenAbrechnungsmonatAsDate();
  }

  private aktualisiereUrlaubskontoListe(mitarbeiter: Mitarbeiter): void {
    this.ladeUrlaubskontoSpinner.open();

    // Relevante Daten zurücksetzen
    this.urlaubskontoListe = new StatusListen([]);
    this.urlaubskontoFormGroup.reset();

    if (!mitarbeiter || !mitarbeiter.id) {
      this.aendereAktivierungsstatusUrlaubskontoFormGroup(false);
      this.ladeUrlaubskontoSpinner.close();
      return;
    }

    this.mitarbeiterService.getUrlaubskontoVonMitarbeiter(mitarbeiter.id).subscribe((urlaubskonto) => {
      this.urlaubskontoListe = new StatusListen(urlaubskonto);
      this.tabelle.data = this.urlaubskontoListe.getAnzeigeListe() as MitarbeiterUrlaubskonto[];
      this.aendereAktivierungsstatusUrlaubskontoFormGroup(true);
      this.ladeUrlaubskontoSpinner.close();
    });
  }

  private aendereAktivierungsstatusUrlaubskontoFormGroup(mitarbeiterAusgewaelt: boolean): void {
    if (mitarbeiterAusgewaelt) {
      this.urlaubskontoFormGroup.enable();
    } else {
      this.urlaubskontoFormGroup.disable();
    }
  }

  private initialisiereTabellenDaten(): void {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<MitarbeiterUrlaubskonto>(this.sort, true);
    this.tabelle.data = this.urlaubskontoListe.getAnzeigeListe() as MitarbeiterUrlaubskonto[];
  }

  private initialisiereTabellenSortierung(): void {
    this.tabelle.sortParams = ['wertstellung', 'buchungsdatum'];
    this.tabelle.sortDirs = ['desc'];

    this.tabelle.updateSortHeaders();
  }

  private erstelleUrlaubsKontoAusForm(): MitarbeiterUrlaubskonto {
    return {
      id: this.idGeneratorService.generiereId(),
      mitarbeiterId: this.mitarbeiterAuswahlFormGroup.getRawValue().mitarbeiter.id,
      wertstellung: this.urlaubskontoFormGroup.getRawValue().wertstellung,
      buchungsdatum: new Date(),
      anzahlTage: this.negiereAnzahlTagAbheangigVonBuchungsTyp(
        convertDeStringZuUsNummer(this.urlaubskontoFormGroup.getRawValue().anzahlTage),
        this.urlaubskontoFormGroup.getRawValue().buchungstyp
      ),
      lfdSaldo: 0,
      buchungstypUrlaubId: this.urlaubskontoFormGroup.getRawValue().buchungstyp.id,
      automatisch: false,
      endgueltig: false,
      zuletztGeaendertVon: this.benutzerService.getBenutzerNamen(),
      bemerkung: this.urlaubskontoFormGroup.getRawValue().bemerkung,
      geaendert: false,
    };
  }

  private negiereAnzahlTagAbheangigVonBuchungsTyp(anzahlTag: number, buchungstypUrlaub: BuchungstypUrlaub): number {
    if (
      buchungstypUrlaub.bezeichnung === BuchungstypUrlaubArtenEnum.WERTKONTO ||
      buchungstypUrlaub.bezeichnung === BuchungstypUrlaubArtenEnum.GENOMMEN
    ) {
      return -1 * anzahlTag;
    } else {
      return anzahlTag;
    }
  }

  private berechneNeuenSaldoUndAktualisiereUrlaubskontoListe(neuUrlaubskontoListe: MitarbeiterUrlaubskonto[]): void {
    this.mitarbeiterService
      .getMitarbeiterUrlaubskontoMitNeuemSaldo(
        this.mitarbeiterAuswahlFormGroup.getRawValue().mitarbeiter.id,
        neuUrlaubskontoListe
      )
      .subscribe((urlaubskontoListe) => {
        const urlaubskontoListeUnveraendert = urlaubskontoListe.filter((urlaubskonto) => !urlaubskonto.geaendert);
        const urlaubskontoListeGeandert = urlaubskontoListe.filter(
          (urlaubskonto) => urlaubskonto.geaendert && Number(urlaubskonto.id) >= 0
        );
        const urlaubskontoListeNeu = urlaubskontoListe.filter(
          (urlaubskonto) => urlaubskonto.geaendert && Number(urlaubskonto.id) < 0
        );

        this.urlaubskontoListe = new StatusListen(
          urlaubskontoListeUnveraendert,
          urlaubskontoListeGeandert,
          urlaubskontoListeNeu
        );
        this.tabelle.data = this.urlaubskontoListe.getAnzeigeListe() as MitarbeiterUrlaubskonto[];
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
