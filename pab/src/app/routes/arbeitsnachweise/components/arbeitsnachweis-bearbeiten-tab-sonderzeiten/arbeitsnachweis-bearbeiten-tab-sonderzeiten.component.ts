import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { FormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { VonBisDatumErrorStateMatcher } from '../../validation/von-bis-datum-error-state-matcher';
import { entferneUnerlaubteZeichenAusFormGroupKomponente } from '../../../../shared/util/regex-control.util';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { ProjektstundeTyp } from '../../../../shared/model/konstanten/projektstunde-typ';
import { ProjektstundeTypArtenEnum } from '../../../../shared/enum/projektstunde-typ-arten.enum';
import { projektstundeTypAbhaengigkeiten } from '../../data/projektstunde-typ-abhaengigkeiten';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { Projektstunde } from '../../../../shared/model/arbeitsnachweis/projektstunde';
import { StatusListen } from '../../../../shared/model/sonstiges/status-listen';
import { ProjektstundeTabellendarstellung } from '../../model/projektstunde-tabellendarstellung';
import { TabellenDarstellungService } from '../../service/tabellen-darstellung.service';
import { convertDeStringZuUsNummer, convertUsNummerZuDeString } from '../../../../shared/util/nummer-converter.util';
import {
  erzeugeDatumMit2400Fall,
  transformiereZu2400FallFuerMinuten,
  transformiereZu2400FallFuerStunde,
} from '../../util/uhrzeit.util';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { IdGeneratorService } from '../../../../shared/service/id-generator.service';
import { ErrorService } from '../../../../shared/service/error.service';
import { MitarbeiterAbrechnungsmonat } from '../../../../shared/model/mitarbeiter/mitarbeiter-abrechnungsmonat';
import { BenachrichtigungService } from '../../../../shared/service/benachrichtigung.service';
import { KonstantenService } from '../../../../shared/service/konstanten.service';
import { konvertiereAbrechnungsmonatToJsDate } from '../../../../shared/util/abrechnungsmonat-to-date.util';

@Component({
  selector: 'pab-arbeitsnachweis-bearbeiten-tab-sonderzeiten',
  templateUrl: './arbeitsnachweis-bearbeiten-tab-sonderzeiten.component.html',
  styleUrls: ['./arbeitsnachweis-bearbeiten-tab-sonderzeiten.component.scss'],
})
export class ArbeitsnachweisBearbeitenTabSonderzeitenComponent implements OnInit, OnChanges, AfterViewInit {
  @Input()
  sonderzeitenFormGroup!: FormGroup;

  @Input()
  arbeitsnachweisOffen!: boolean;

  @Input()
  projektAuswahl!: Projekt[];

  @Input()
  projektstundeTypAuswahl!: ProjektstundeTyp[];

  @Input()
  projektstundenListen!: StatusListen;

  @Input()
  abrechnungsmonat!: MitarbeiterAbrechnungsmonat;

  @ViewChild('reisezeitenSort') reisezeitenSort!: MatMultiSort;
  @ViewChild('rufbereitschaftenSort') rufbereitschaftenSort!: MatMultiSort;
  @ViewChild('sonderarbeitszeitenSort') sonderarbeitszeitenSort!: MatMultiSort;
  @ViewChild(FormGroupDirective) formRef!: FormGroupDirective;

  @Output()
  reisezeitAktualisiertEvent = new EventEmitter();

  @Output()
  sonderzeitAktualisiert = new EventEmitter();

  relevanteProjektstundeTypAuswahl: ProjektstundeTyp[] = [];

  projektstundeTypAbhaengigkeiten = projektstundeTypAbhaengigkeiten;

  vonBisAngabenValidMatcher = new VonBisDatumErrorStateMatcher();

  reisezeitenSpalten = [
    { id: 'tagVon', name: 'Tag' },
    { id: 'projektnummer', name: 'Projektnummer' },
    { id: 'stunden', name: 'Stunden' },
    { id: 'bemerkung', name: 'Bemerkung' },
    { id: 'actions', name: '' },
  ];
  rufbereitschaftenSpalten = [
    { id: 'tagVon', name: 'Tag' },
    { id: 'uhrzeitVon', name: 'Von' },
    { id: 'tagBis', name: 'Tag' },
    { id: 'uhrzeitBis', name: 'Bis' },
    { id: 'projektnummer', name: 'Projektnummer' },
    { id: 'stunden', name: 'Stunden' },
    { id: 'bemerkung', name: 'Bemerkung' },
    { id: 'actions', name: '' },
  ];
  sonderarbeitszeitenSpalten = [
    { id: 'tagVon', name: 'Tag' },
    { id: 'uhrzeitVon', name: 'Von' },
    { id: 'uhrzeitBis', name: 'Bis' },
    { id: 'projektnummer', name: 'Projektnummer' },
    { id: 'stunden', name: 'Stunden' },
    { id: 'bemerkung', name: 'Bemerkung' },
    { id: 'actions', name: '' },
  ];

  reisezeitenTabelle: TableData<ProjektstundeTabellendarstellung>;
  rufbereitschaftenTabelle: TableData<ProjektstundeTabellendarstellung>;
  sonderarbeitszeitenTabelle: TableData<ProjektstundeTabellendarstellung>;

  ausgewaehlterTab = 0;

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private tabellenDarstellungService: TabellenDarstellungService,
    private idGeneratorService: IdGeneratorService,
    private konstantenService: KonstantenService,
    private errorService: ErrorService,
    private benachrichtigungsService: BenachrichtigungService
  ) {
    this.reisezeitenTabelle = new TableData<ProjektstundeTabellendarstellung>(this.reisezeitenSpalten);
    this.reisezeitenTabelle.dataSource = new MatMultiSortTableDataSource<ProjektstundeTabellendarstellung>(
      new MatMultiSort(),
      true
    );

    this.rufbereitschaftenTabelle = new TableData<ProjektstundeTabellendarstellung>(this.rufbereitschaftenSpalten);
    this.rufbereitschaftenTabelle.dataSource = new MatMultiSortTableDataSource<ProjektstundeTabellendarstellung>(
      new MatMultiSort(),
      true
    );

    this.sonderarbeitszeitenTabelle = new TableData<ProjektstundeTabellendarstellung>(this.sonderarbeitszeitenSpalten);
    this.sonderarbeitszeitenTabelle.dataSource = new MatMultiSortTableDataSource<ProjektstundeTabellendarstellung>(
      new MatMultiSort(),
      true
    );
  }

  ngOnInit(): void {
    this.sonderzeitenFormGroup.get('projektstundeTyp')?.valueChanges.subscribe((projektstundeTyp: ProjektstundeTyp) => {
      if (projektstundeTyp) {
        this.passeBedingteValidierungAn(projektstundeTyp);
        this.zeigeTabelleFuerProjekstundenTypAn(projektstundeTyp.id);
      }
    });

    this.setDatumsEventListener();
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenDaten();
    this.initialisiereTabellenSortierung();
    this.changeDetectorRef.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['projektstundenListen']) {
      if (this.projektstundenListen.getAnzeigeListe().length > 0) {
        this.aktualisiereTabellenDaten();
      }
    }

    if (changes['projektstundeTypAuswahl']) {
      this.relevanteProjektstundeTypAuswahl = changes['projektstundeTypAuswahl'].currentValue.filter(
        (projektstundeTyp: ProjektstundeTyp) => {
          return (
            projektstundeTyp.textKurz === ProjektstundeTypArtenEnum.RUFBEREITSCHAFT ||
            projektstundeTyp.textKurz === ProjektstundeTypArtenEnum.SONDER ||
            projektstundeTyp.textKurz === ProjektstundeTypArtenEnum.TATSAECHLICHE_REISEZEIT
          );
        }
      );
    }
    if (changes['arbeitsnachweisOffen']) {
      this.aendereAktivierungsstatusSonderzeitenFormGroup(changes['arbeitsnachweisOffen'].currentValue);
    }
  }

  fuegeOderAktualisiereSonderzeit() {
    if (this.sonderzeitenFormGroup.invalid) {
      this.sonderzeitenFormGroup.markAllAsTouched();
      return;
    }

    const projektstunde: Projektstunde = this.erstelleProjektstundenAusForm();

    if (this.projektstundeKollidiertMitStundenGleicherArt(projektstunde)) {
      this.benachrichtigungsService.erstelleWarnung(
        'Die Eingabe führt zu zeitlichen Überschneidungen. Die Änderung kann nicht durchgeführt werden '
      );
      return;
    }

    // Projektstunden aktualisieren
    if (projektstunde.id) {
      this.projektstundenListen?.aktualisiereElement(projektstunde);
    } else {
      projektstunde.id = this.idGeneratorService.generiereId();
      this.projektstundenListen?.fuegeNeuesElementHinzu(projektstunde);
    }

    this.aktualisiereTabellenDaten();

    this.zeigeTabelleFuerProjekstundenTypAn(projektstunde.projektstundeTypId);

    this.sonderzeitenFormGroup.reset();
    setTimeout(() => {
      this.formRef.resetForm();
      this.deaktiviereAbhaengigeFelder();
      // Setze bisher ausgewähltes Projekt wieder im multiselect
      this.sonderzeitenFormGroup.patchValue({
        projekt: getObjektAusListeDurchId(projektstunde.projektId, this.projektAuswahl) as Projekt,
        projektstundeTyp: getObjektAusListeDurchId(
          projektstunde.projektstundeTypId,
          this.projektstundeTypAuswahl
        ) as ProjektstundeTyp,
      });
    }, 100);

    this.pruefeUndMeldeBestimmteAktualisierung(projektstunde);
  }

  entferneUnerlaubteZeichen(komponentenName: string, erlaubteExp: string) {
    entferneUnerlaubteZeichenAusFormGroupKomponente(this.sonderzeitenFormGroup, komponentenName, erlaubteExp);
  }

  leereForm() {
    this.sonderzeitenFormGroup.reset();
    setTimeout(() => {
      this.formRef.resetForm();
      this.deaktiviereAbhaengigeFelder();
    }, 100);
  }

  loescheProjektstunde(projektstundeTabellenDarstellung: ProjektstundeTabellendarstellung) {
    const projektstunde: Projektstunde = getObjektAusListeDurchId(
      projektstundeTabellenDarstellung.id,
      this.projektstundenListen.getAnzeigeListe()
    ) as Projektstunde;
    this.projektstundenListen.loescheElement(projektstunde);

    this.aktualisiereTabellenDaten();

    if (projektstunde.id === this.sonderzeitenFormGroup.getRawValue().id) {
      this.leereForm();
    }

    this.pruefeUndMeldeBestimmteAktualisierung(projektstunde);
  }

  ladeProjektstundenInForm(projektstundeTabellenDarstellung: ProjektstundeTabellendarstellung): void {
    const projektstunde: Projektstunde = getObjektAusListeDurchId(
      projektstundeTabellenDarstellung.id,
      this.projektstundenListen.getAnzeigeListe()
    ) as Projektstunde;

    this.sonderzeitenFormGroup.patchValue(
      {
        id: projektstunde.id,
        projektstundeTyp: getObjektAusListeDurchId(
          projektstunde.projektstundeTypId,
          this.projektstundeTypAuswahl
        ) as ProjektstundeTyp,
        projekt: getObjektAusListeDurchId(projektstunde.projektId, this.projektAuswahl) as Projekt,
        ...this.erstelleZeitAngabenFuerForm(projektstunde),
        stunden: convertUsNummerZuDeString(projektstunde.anzahlStunden),
        bemerkung: projektstunde.bemerkung,
      },
      { emitEvent: false }
    );
  }

  private filtereProjektstundenNachProjektstundeTyp(
    projektstunden: Projektstunde[],
    projektstundeTyp: ProjektstundeTyp
  ) {
    return projektstunden.filter(
      (projektstunde: Projektstunde) => projektstunde.projektstundeTypId === projektstundeTyp.id
    );
  }

  private erstelleZeitAngabenFuerForm(projektstunde: Projektstunde) {
    return {
      tagVon: projektstunde.datumVon.getDate(),
      hhVon: this.hatProjektstundenTypUhrzeit(projektstunde)
        ? transformiereZu2400FallFuerStunde(projektstunde.datumVon)
        : '',
      mmVon: this.hatProjektstundenTypUhrzeit(projektstunde)
        ? transformiereZu2400FallFuerMinuten(projektstunde.datumVon)
        : '',
      tagBis: this.hatProjektstundenTypTagBis(projektstunde) ? projektstunde.datumBis?.getDate() : '',
      hhBis: this.hatProjektstundenTypUhrzeit(projektstunde)
        ? transformiereZu2400FallFuerStunde(projektstunde.datumBis)
        : '',
      mmBis: this.hatProjektstundenTypUhrzeit(projektstunde)
        ? transformiereZu2400FallFuerMinuten(projektstunde.datumBis)
        : '',
    };
  }

  private initialisiereTabellenDaten() {
    this.reisezeitenTabelle.dataSource = new MatMultiSortTableDataSource<ProjektstundeTabellendarstellung>(
      this.reisezeitenSort,
      true
    );
    this.rufbereitschaftenTabelle.dataSource = new MatMultiSortTableDataSource<ProjektstundeTabellendarstellung>(
      this.rufbereitschaftenSort,
      true
    );
    this.sonderarbeitszeitenTabelle.dataSource = new MatMultiSortTableDataSource<ProjektstundeTabellendarstellung>(
      this.sonderarbeitszeitenSort,
      true
    );

    if (this.projektstundenListen.getAnzeigeListe().length > 0) {
      this.aktualisiereTabellenDaten();
    }
  }

  private initialisiereTabellenSortierung() {
    this.reisezeitenTabelle.sortParams = ['tagVon', 'projektnummer'];
    this.reisezeitenTabelle.sortDirs = ['asc', 'asc'];
    this.reisezeitenTabelle.updateSortHeaders();

    this.rufbereitschaftenTabelle.sortParams = ['tagVon', 'uhrzeitVon', 'projektnummer'];
    this.rufbereitschaftenTabelle.sortDirs = ['asc', 'asc', 'asc'];
    this.rufbereitschaftenTabelle.updateSortHeaders();

    this.sonderarbeitszeitenTabelle.sortParams = ['tagVon', 'uhrzeitVon', 'projektnummer'];
    this.sonderarbeitszeitenTabelle.sortDirs = ['asc', 'asc', 'asc'];
    this.sonderarbeitszeitenTabelle.updateSortHeaders();
  }

  private aktualisiereTabellenDaten() {
    this.reisezeitenTabelle.data = this.filtereUndMappeProjektstundenNachProjektstundeTyp(
      this.konstantenService.getProjektstundenTypTatsaechlicheReise()
    );

    this.rufbereitschaftenTabelle.data = this.filtereUndMappeProjektstundenNachProjektstundeTyp(
      this.konstantenService.getProjektstundenTypRufbereitschaft()
    );

    this.sonderarbeitszeitenTabelle.data = this.filtereUndMappeProjektstundenNachProjektstundeTyp(
      this.konstantenService.getProjektstundenTypSonderarbeitszeit()
    );
  }

  private filtereUndMappeProjektstundenNachProjektstundeTyp(
    projektstundenTyp: ProjektstundeTyp
  ): ProjektstundeTabellendarstellung[] {
    return this.tabellenDarstellungService.filtereUndMappeProjektstundenNachProjektstundeTyp(
      this.projektstundenListen.getAnzeigeListe() as Projektstunde[],
      projektstundenTyp,
      this.projektAuswahl
    );
  }

  private aendereAktivierungsstatusSonderzeitenFormGroup(anwOffen: boolean) {
    if (anwOffen) {
      this.sonderzeitenFormGroup.enable();
      this.deaktiviereAbhaengigeFelder();
    } else {
      this.sonderzeitenFormGroup.disable();
    }
  }

  private deaktiviereAbhaengigeFelder() {
    for (const feldAbhaengig of this.projektstundeTypAbhaengigkeiten.abhaengigeFelder) {
      this.sonderzeitenFormGroup.get(feldAbhaengig)?.disable();
    }
  }

  private passeBedingteValidierungAn(projektstundeTyp: ProjektstundeTyp) {
    if (!this.arbeitsnachweisOffen) {
      return;
    }

    for (const feldAbhaengig of this.projektstundeTypAbhaengigkeiten.abhaengigeFelder) {
      this.sonderzeitenFormGroup.get(feldAbhaengig)?.enable();
      this.sonderzeitenFormGroup.get(feldAbhaengig)?.clearValidators();
      this.sonderzeitenFormGroup.get(feldAbhaengig)?.updateValueAndValidity();
    }

    if (!projektstundeTyp) {
      return;
    }

    for (const feldRequiered of this.projektstundeTypAbhaengigkeiten.felder[projektstundeTyp.textKurz].required) {
      this.sonderzeitenFormGroup.get(feldRequiered)?.addValidators(Validators.required);
      this.sonderzeitenFormGroup.get(feldRequiered)?.updateValueAndValidity();
    }

    for (const feldDisabled of this.projektstundeTypAbhaengigkeiten.felder[projektstundeTyp.textKurz].disabled) {
      this.sonderzeitenFormGroup.get(feldDisabled)?.reset();
      this.sonderzeitenFormGroup.get(feldDisabled)?.disable();
    }
  }

  private erstelleProjektstundenAusForm(): Projektstunde {
    return {
      id: this.sonderzeitenFormGroup.getRawValue().id,
      datumVon: this.extrahiereDatumVonAusForm(),
      datumBis: this.extrahiereDatumBisAusForm(),
      projektId: this.sonderzeitenFormGroup.getRawValue().projekt.id,
      projektstundeTypId: this.sonderzeitenFormGroup.getRawValue().projektstundeTyp.id,
      anzahlStunden: convertDeStringZuUsNummer(this.sonderzeitenFormGroup.getRawValue().stunden),
      nichtFakturierfaehig: this.sonderzeitenFormGroup.getRawValue().nichtFakturierfaehig,
      bemerkung: this.sonderzeitenFormGroup.getRawValue().bemerkung,
    };
  }

  private zeigeTabelleFuerProjekstundenTypAn(projektstundeTypId: string) {
    const reisezeiten = this.konstantenService.getProjektstundenTypTatsaechlicheReise().id;
    const rufbereitschaften = this.konstantenService.getProjektstundenTypRufbereitschaft().id;
    const sonderarbeitszeiten = this.konstantenService.getProjektstundenTypSonderarbeitszeit().id;

    switch (projektstundeTypId) {
      case reisezeiten:
        this.ausgewaehlterTab = 0;
        break;
      case rufbereitschaften:
        this.ausgewaehlterTab = 1;
        break;
      case sonderarbeitszeiten:
        this.ausgewaehlterTab = 2;
        break;
    }
  }

  private setDatumsEventListener() {
    this.sonderzeitenFormGroup.get('tagVon')?.valueChanges.subscribe(() => {
      this.aktualisiereStunden();
    });
    this.sonderzeitenFormGroup.get('hhVon')?.valueChanges.subscribe(() => {
      this.aktualisiereStunden();
    });
    this.sonderzeitenFormGroup.get('mmVon')?.valueChanges.subscribe(() => {
      this.aktualisiereStunden();
    });
    this.sonderzeitenFormGroup.get('tagBis')?.valueChanges.subscribe(() => {
      this.aktualisiereStunden();
    });
    this.sonderzeitenFormGroup.get('hhBis')?.valueChanges.subscribe(() => {
      this.aktualisiereStunden();
    });
    this.sonderzeitenFormGroup.get('mmBis')?.valueChanges.subscribe(() => {
      this.aktualisiereStunden();
    });
  }

  private aktualisiereStunden() {
    if (!this.arbeitsnachweisOffen) {
      return;
    }
    if (
      this.sonderzeitenFormGroup.getRawValue().projektstundeTyp &&
      this.sonderzeitenFormGroup.getRawValue().projektstundeTyp !== ProjektstundeTypArtenEnum.TATSAECHLICHE_REISEZEIT
    ) {
      if (this.datumsEingabenValide()) {
        const stunden = this.berechneStundenDifferenz();
        if (stunden) {
          this.sonderzeitenFormGroup.patchValue({
            stunden: stunden,
          });
        }
      }
    }
  }

  private datumsEingabenValide() {
    return (
      !this.sonderzeitenFormGroup.errors?.['tagVonNachTagBis'] &&
      !this.sonderzeitenFormGroup.errors?.['zeitVonNachZeitBis']
    );
  }

  private berechneStundenDifferenz(): string | undefined {
    const tagVon = this.sonderzeitenFormGroup.getRawValue().tagVon;
    const tagBis = this.sonderzeitenFormGroup.get('tagBis')?.enabled
      ? this.sonderzeitenFormGroup.getRawValue().tagBis
      : tagVon;

    if (tagBis === '' || tagVon === '') {
      return undefined;
    }

    const hhVonRaw = this.sonderzeitenFormGroup.getRawValue().hhVon;
    const mmVonRaw = this.sonderzeitenFormGroup.getRawValue().mmVon;
    const hhBisRaw = this.sonderzeitenFormGroup.getRawValue().hhBis;
    const mmBisRaw = this.sonderzeitenFormGroup.getRawValue().mmBis;

    if (hhVonRaw === '' || mmVonRaw === '' || hhBisRaw === '' || mmBisRaw === '') {
      return undefined;
    }

    const hhVon = parseInt(hhVonRaw);
    const mmVon = parseInt(mmVonRaw);
    const hhBis = parseInt(hhBisRaw);
    const mmBis = parseInt(mmBisRaw);

    const dateVon = erzeugeDatumMit2400Fall(this.abrechnungsmonat, parseInt(tagVon), hhVon, mmVon);
    const dateBis = erzeugeDatumMit2400Fall(this.abrechnungsmonat, parseInt(tagBis), hhBis, mmBis);

    const differenzInMinuten = Math.round((dateBis.getTime() - dateVon.getTime()) / (1000 * 60));
    if (differenzInMinuten < 0) {
      return undefined;
    }
    const stunden = Math.trunc(differenzInMinuten / 60);
    const minuten = Math.round(((differenzInMinuten % 60) / 6) * 10);
    const minutenString = minuten < 10 ? `0${minuten}` : minuten;
    return `${stunden},${minutenString}`;
  }

  private pruefeUndMeldeBestimmteAktualisierung(projektstunde: Projektstunde) {
    if (projektstunde.projektstundeTypId === this.konstantenService.getProjektstundenTypTatsaechlicheReise().id) {
      this.reisezeitAktualisiertEvent.emit();
    }
    if (projektstunde.projektstundeTypId === this.konstantenService.getProjektstundenTypSonderarbeitszeit().id) {
      this.sonderzeitAktualisiert.emit();
    }
  }

  private hatProjektstundenTypUhrzeit(projektstunde: Projektstunde) {
    return (
      projektstunde.projektstundeTypId === this.konstantenService.getProjektstundenTypSonderarbeitszeit().id ||
      projektstunde.projektstundeTypId === this.konstantenService.getProjektstundenTypRufbereitschaft().id
    );
  }

  private hatProjektstundenTypTagBis(projektstunde: Projektstunde) {
    return projektstunde.projektstundeTypId === this.konstantenService.getProjektstundenTypRufbereitschaft().id;
  }

  private extrahiereDatumVonAusForm(): Date {
    const projektstundenTyp = this.sonderzeitenFormGroup.getRawValue().projektstundeTyp;
    switch (projektstundenTyp?.textKurz) {
      case ProjektstundeTypArtenEnum.TATSAECHLICHE_REISEZEIT:
      case ProjektstundeTypArtenEnum.ANGERECHNETE_REISEZEIT:
        return konvertiereAbrechnungsmonatToJsDate(
          this.abrechnungsmonat,
          parseInt(this.sonderzeitenFormGroup.getRawValue().tagVon)
        );
      case ProjektstundeTypArtenEnum.SONDER:
      case ProjektstundeTypArtenEnum.RUFBEREITSCHAFT:
        return erzeugeDatumMit2400Fall(
          this.abrechnungsmonat,
          parseInt(this.sonderzeitenFormGroup.getRawValue().tagVon),
          parseInt(this.sonderzeitenFormGroup.getRawValue().hhVon),
          parseInt(this.sonderzeitenFormGroup.getRawValue().mmVon)
        );
      default:
        this.errorService.zeigeTechnischenFehlerAn(
          `ProjektstundenTyp ${this.sonderzeitenFormGroup.getRawValue().projektstundeTyp.textKurz} unbekannt`
        );
        throw Error;
    }
  }

  private extrahiereDatumBisAusForm(): Date | null {
    const projektstundenTyp = this.sonderzeitenFormGroup.getRawValue().projektstundeTyp;
    switch (projektstundenTyp?.textKurz) {
      case ProjektstundeTypArtenEnum.TATSAECHLICHE_REISEZEIT:
      case ProjektstundeTypArtenEnum.ANGERECHNETE_REISEZEIT:
        return null;
      case ProjektstundeTypArtenEnum.SONDER:
        return erzeugeDatumMit2400Fall(
          this.abrechnungsmonat,
          parseInt(this.sonderzeitenFormGroup.getRawValue().tagVon),
          parseInt(this.sonderzeitenFormGroup.getRawValue().hhBis),
          parseInt(this.sonderzeitenFormGroup.getRawValue().mmBis)
        );
      case ProjektstundeTypArtenEnum.RUFBEREITSCHAFT:
        return erzeugeDatumMit2400Fall(
          this.abrechnungsmonat,
          parseInt(this.sonderzeitenFormGroup.getRawValue().tagBis),
          parseInt(this.sonderzeitenFormGroup.getRawValue().hhBis),
          parseInt(this.sonderzeitenFormGroup.getRawValue().mmBis)
        );
      default:
        this.errorService.zeigeTechnischenFehlerAn(
          `ProjektstundenTyp ${this.sonderzeitenFormGroup.getRawValue().projektstundeTyp.textKurz} unbekannt`
        );
        throw Error;
    }
  }

  private projektstundeKollidiertMitStundenGleicherArt(projektstundeZuPruefen: Projektstunde) {
    if (
      projektstundeZuPruefen.projektstundeTypId !== this.konstantenService.getProjektstundenTypRufbereitschaft().id &&
      projektstundeZuPruefen.projektstundeTypId !== this.konstantenService.getProjektstundenTypSonderarbeitszeit().id
    ) {
      return false;
    }
    const projektstundenZumVergleich: Projektstunde[] =
      projektstundeZuPruefen.projektstundeTypId === this.konstantenService.getProjektstundenTypRufbereitschaft().id
        ? this.filtereProjektstundenNachProjektstundeTyp(
            this.projektstundenListen.getAnzeigeListe() as Projektstunde[],
            this.konstantenService.getProjektstundenTypRufbereitschaft()
          )
        : this.filtereProjektstundenNachProjektstundeTyp(
            this.projektstundenListen.getAnzeigeListe() as Projektstunde[],
            this.konstantenService.getProjektstundenTypSonderarbeitszeit()
          );

    return !!projektstundenZumVergleich.find((projekstundeZumVergleich) => {
      if (projektstundeZuPruefen.id === projekstundeZumVergleich.id) {
        return false;
      }
      if (!projektstundeZuPruefen.datumBis || !projekstundeZumVergleich.datumBis) {
        return false;
      }
      //Überlappung existiert, wenn weder projektstundenZuPrüfen vor vergleich endet, noch nach vergleich startet.
      return !(
        projektstundeZuPruefen.datumBis < projekstundeZumVergleich.datumVon ||
        projektstundeZuPruefen.datumVon > projekstundeZumVergleich.datumBis
      );
    });
  }
}
