import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { FormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { entferneUnerlaubteZeichenAusFormGroupKomponente } from '../../../../shared/util/regex-control.util';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { Belegart } from '../../../../shared/model/konstanten/belegart';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { Beleg } from '../../../../shared/model/arbeitsnachweis/beleg';
import { SmartphoneBesitzArtenEnum } from '../../../../shared/enum/smartphone-besitz-arten.enum';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import {
  convertDeStringZuUsNummer,
  convertUsNummerZuDeString,
  rundeNummerAufZweiNackommastellen,
} from '../../../../shared/util/nummer-converter.util';
import { belegartAbhaengigkeiten } from '../../data/belegart-abhaengigkeiten';
import { StatusListen } from '../../../../shared/model/sonstiges/status-listen';
import { ErrorService } from '../../../../shared/service/error.service';
import { BelegTabellendarstellung } from '../../../../shared/model/tabellendarstellung/beleg-tabellendarstellung';
import { DatePipe } from '@angular/common';
import { IdGeneratorService } from '../../../../shared/service/id-generator.service';
import { MitarbeiterAbrechnungsmonat } from '../../../../shared/model/mitarbeiter/mitarbeiter-abrechnungsmonat';
import { konvertiereAbrechnungsmonatToJsDate } from '../../../../shared/util/abrechnungsmonat-to-date.util';

@Component({
  selector: 'pab-arbeitsnachweis-bearbeiten-tab-belege',
  templateUrl: './arbeitsnachweis-bearbeiten-tab-belege.component.html',
  styleUrls: ['./arbeitsnachweis-bearbeiten-tab-belege.component.scss'],
})
export class ArbeitsnachweisBearbeitenTabBelegeComponent implements OnInit, AfterViewInit, OnChanges {
  @Input()
  smartphoneAuswahlFormGroup!: FormGroup;

  @Input()
  arbeitsnachweisOffen!: boolean;

  @Input()
  belegeFormGroup!: FormGroup;

  @Input()
  projektAuswahl!: Projekt[];

  @Input()
  belegartenAuswahl!: Belegart[];

  @Input()
  einsatzortAuswahl!: string[];

  @Input()
  belegeListen!: StatusListen;

  @Input()
  abrechnungsmonat!: MitarbeiterAbrechnungsmonat;

  @ViewChild('fgdBelegForm') formRef!: FormGroupDirective;

  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  belegartAbhaengigkeiten = belegartAbhaengigkeiten;

  eSmartphoneBesitzArtenEnum = SmartphoneBesitzArtenEnum;

  kilometerpauschale = 0.3;

  spalten = [
    { id: 'tag', name: 'Tag' },
    { id: 'einsatzort', name: 'Einsatzort' },
    { id: 'projektnummer', name: 'Projektnummer' },
    { id: 'belegartName', name: 'Belegart' },
    { id: 'betrag', name: 'Betrag' },
    { id: 'bemerkung', name: 'Bemerkung' },
    { id: 'actions', name: 'Actions' },
  ];

  tabelle: TableData<BelegTabellendarstellung>;

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private errorService: ErrorService,
    private datePipe: DatePipe,
    private idGeneratorService: IdGeneratorService
  ) {
    this.tabelle = new TableData<BelegTabellendarstellung>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<BelegTabellendarstellung>(new MatMultiSort(), true);
  }

  ngOnInit(): void {
    this.belegeFormGroup.get('belegart')?.valueChanges.subscribe((belegart: Belegart) => {
      this.passeBedingteValidierungAn(belegart);
    });

    this.belegeFormGroup.get('kilometer')?.valueChanges.subscribe((kilometer: string) => {
      this.aktualisiereBetrag(kilometer);
    });
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenDaten();
    this.initialisiereTabellenSortierung();
    this.changeDetectorRef.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['belegeListen']) {
      const belegeListenChange = changes['belegeListen'];
      if (
        belegeListenChange.currentValue &&
        belegeListenChange.previousValue &&
        belegeListenChange.currentValue.getAnzeigeListe().length !==
          belegeListenChange.previousValue.getAnzeigeListe().length
      ) {
        this.tabelle.data = this.mapBelegeZuTabellendarstellung(this.belegeListen.getAnzeigeListe() as Beleg[]);
      }
    }
    if (changes['arbeitsnachweisOffen']) {
      this.aendereAktivierungsstatusBelegeFormGroup(changes['arbeitsnachweisOffen'].currentValue);
    }
  }

  ladeBelegInForm(belegTabellendarstellung: BelegTabellendarstellung) {
    const beleg: Beleg = getObjektAusListeDurchId(
      belegTabellendarstellung.id,
      this.belegeListen.getAnzeigeListe()
    ) as Beleg;
    this.belegeFormGroup.patchValue({
      id: beleg.id,
      tag: beleg.datum.getDate(),
      belegart: getObjektAusListeDurchId(beleg.belegartId, this.belegartenAuswahl) as Belegart,
      projekt: getObjektAusListeDurchId(beleg.projektId, this.projektAuswahl) as Projekt,
      einsatzort: beleg.arbeitsstaette,
      betrag: convertUsNummerZuDeString(beleg.betrag),
      kilometer: convertUsNummerZuDeString(beleg.kilometer),
      bemerkung: beleg.bemerkung,
      belegNr: beleg.belegNr,
    });
  }

  loescheBeleg(belegTabellendarstellung: BelegTabellendarstellung) {
    const beleg: Beleg = getObjektAusListeDurchId(
      belegTabellendarstellung.id,
      this.belegeListen.getAnzeigeListe()
    ) as Beleg;

    this.belegeListen.loescheElement(beleg);

    this.tabelle.data = this.mapBelegeZuTabellendarstellung(this.belegeListen.getAnzeigeListe() as Beleg[]);

    if (beleg.id === this.belegeFormGroup.getRawValue().id) {
      this.leereForm();
    }
  }

  entferneUnerlaubteZeichen(komponentenName: string, erlaubteExp: string) {
    entferneUnerlaubteZeichenAusFormGroupKomponente(this.belegeFormGroup, komponentenName, erlaubteExp);
  }

  fuegeOderAktualisiereBeleg() {
    if (this.belegeFormGroup.invalid) {
      this.belegeFormGroup.markAllAsTouched();
      return;
    }

    const beleg: Beleg = this.erstelleBelegAusForm();

    // Aktualisiere Beleg
    if (beleg.id) {
      this.belegeListen.aktualisiereElement(beleg);
    } else {
      beleg.id = this.idGeneratorService.generiereId();
      this.belegeListen.fuegeNeuesElementHinzu(beleg);
    }

    this.tabelle.data = this.mapBelegeZuTabellendarstellung(this.belegeListen.getAnzeigeListe() as Beleg[]);

    this.belegeFormGroup.reset();
    setTimeout(() => {
      this.formRef.resetForm();
      this.disableAbhaengigeFelder();
      this.belegeFormGroup.patchValue({
        projekt: getObjektAusListeDurchId(beleg.projektId.toString(), this.projektAuswahl) as Projekt,
      });
    }, 100);
  }

  leereForm() {
    this.belegeFormGroup.reset();
    setTimeout(() => {
      this.formRef.resetForm();
      this.disableAbhaengigeFelder();
    }, 100);
  }

  private initialisiereTabellenDaten() {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<BelegTabellendarstellung>(this.sort, true);
    this.tabelle.data = this.mapBelegeZuTabellendarstellung(this.belegeListen.getAnzeigeListe() as Beleg[]);
  }

  private initialisiereTabellenSortierung() {
    this.tabelle.sortParams = ['tag', 'projektnummer', 'belegart'];
    this.tabelle.sortDirs = ['asc', 'asc', 'asc'];

    this.tabelle.updateSortHeaders();
  }

  private disableAbhaengigeFelder() {
    for (const feldAbhaengig of this.belegartAbhaengigkeiten.abhaengigeFelder) {
      this.belegeFormGroup.get(feldAbhaengig)?.disable();
    }
  }

  private erstelleBelegAusForm(): Beleg {
    return {
      id: this.belegeFormGroup.getRawValue().id,
      datum: konvertiereAbrechnungsmonatToJsDate(
        this.abrechnungsmonat,
        parseInt(this.belegeFormGroup.getRawValue().tag)
      ),
      projektId: this.belegeFormGroup.getRawValue().projekt.id,
      belegartId: this.belegeFormGroup.getRawValue().belegart.id,
      betrag: convertDeStringZuUsNummer(this.belegeFormGroup.getRawValue().betrag),
      kilometer: convertDeStringZuUsNummer(this.belegeFormGroup.getRawValue().kilometer),
      arbeitsstaette: this.belegeFormGroup.getRawValue().einsatzort,
      belegNr: this.belegeFormGroup.getRawValue().belegNr,
      bemerkung: this.belegeFormGroup.getRawValue().bemerkung,
    } as Beleg;
  }

  private aendereAktivierungsstatusBelegeFormGroup(anwOffen: boolean) {
    if (anwOffen) {
      this.belegeFormGroup.enable();
      this.smartphoneAuswahlFormGroup.enable();
      this.disableAbhaengigeFelder();
    } else {
      this.belegeFormGroup.disable();
      this.smartphoneAuswahlFormGroup.disable();
    }
  }

  private passeBedingteValidierungAn(belegart: Belegart) {
    if (!this.arbeitsnachweisOffen) {
      return;
    }

    for (const feldAbhaengig of this.belegartAbhaengigkeiten.abhaengigeFelder) {
      this.belegeFormGroup.get(feldAbhaengig)?.enable();
      this.belegeFormGroup.get(feldAbhaengig)?.clearValidators();
      this.belegeFormGroup.get(feldAbhaengig)?.updateValueAndValidity();
    }

    if (!belegart.id) {
      return;
    }

    for (const feldRequiered of this.belegartAbhaengigkeiten.felder[belegart.textKurz].required) {
      this.belegeFormGroup.get(feldRequiered)?.addValidators(Validators.required);
      this.belegeFormGroup.get(feldRequiered)?.updateValueAndValidity();
    }

    for (const feldDisabled of this.belegartAbhaengigkeiten.felder[belegart.textKurz].disabled) {
      this.belegeFormGroup.get(feldDisabled)?.reset();
      this.belegeFormGroup.get(feldDisabled)?.disable();
    }
  }

  private mapBelegeZuTabellendarstellung(belege: Beleg[]): BelegTabellendarstellung[] {
    return belege.map((beleg) => {
      const datum = this.datePipe.transform(beleg.datum.toString(), 'dd');
      const projekt: Projekt = getObjektAusListeDurchId(beleg.projektId, this.projektAuswahl) as Projekt;

      return {
        id: beleg.id,
        datum: datum ? datum : '00',
        projekt: projekt,
        projektnummer: projekt.projektnummer,
        belegartName: (getObjektAusListeDurchId(beleg.belegartId, this.belegartenAuswahl) as Belegart).textKurz,
        betrag: beleg.betrag,
        kilometer: beleg.kilometer,
        arbeitsstaette: beleg.arbeitsstaette,
        belegNr: beleg.belegNr,
        bemerkung: beleg.bemerkung,
      };
    });
  }

  private aktualisiereBetrag(kilometer: string) {
    if (kilometer) {
      const betrag = convertDeStringZuUsNummer(kilometer) * this.kilometerpauschale;

      const betragGerundet = rundeNummerAufZweiNackommastellen(betrag);

      this.belegeFormGroup.patchValue({
        betrag: convertUsNummerZuDeString(betragGerundet),
      });
    }
  }
}
