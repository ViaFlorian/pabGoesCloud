import { AfterViewInit, ChangeDetectorRef, Component, Input, OnChanges, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { SonstigeProjektkosten } from '../../../../shared/model/projektabrechnung/sonstige-projektkosten';
import { StatusListen } from '../../../../shared/model/sonstiges/status-listen';
import { BenutzerService } from '../../../../shared/service/benutzer.service';
import { IdGeneratorService } from '../../../../shared/service/id-generator.service';
import { SonstigeProjektkostenTabellendarstellung } from '../../model/sonstige-projektkosten-tabellendarstellung';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { Abrechnungsmonat } from '../../../../shared/model/sonstiges/abrechnungsmonat';
import { MatMultiSort, MatMultiSortTableDataSource, TableData } from 'ngx-mat-multi-sort';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { Belegart } from '../../../../shared/model/konstanten/belegart';
import { ViadeeAuslagenKostenart } from '../../../../shared/model/konstanten/viadee-auslagen-kostenart';
import { AbrechnungsmonatAnzeigeNamePipe } from '../../../../shared/pipe/abrechnungsmonat-anzeige-name.pipe';
import { MitarbeiterAnzeigeNameKurzformPipe } from '../../../../shared/pipe/mitarbeiter-anzeige-name-kurzform.pipe';
import { ProjekteAnzeigeNamePipe } from '../../../../shared/pipe/projekte-anzeige-name.pipe';
import { debounceTime } from 'rxjs';
import { convertUsNummerZuDeString } from '../../../../shared/util/nummer-converter.util';
import { entferneUnerlaubteZeichenAusFormGroupKomponente } from '../../../../shared/util/regex-control.util';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';
import {
  belegartAbhaengigkeiten,
  viadeeAuslagenKostenartAbhaengigkeiten,
} from '../../data/projektabrechnung-auslagen-abhaengigkeiten';
import { KonstantenService } from '../../../../shared/service/konstanten.service';
import * as CompareUtil from '../../../../shared/util/compare.util';
import * as IconUtil from '../../../../shared/util/icon.util';

@Component({
  selector: 'pab-projektabrechnung-auslagen-form-tabelle',
  templateUrl: './projektabrechnung-auslagen-form-tabelle.component.html',
  styleUrls: ['./projektabrechnung-auslagen-form-tabelle.component.scss'],
})
export class ProjektabrechnungAuslagenFormTabelleComponent implements OnInit, OnChanges, AfterViewInit {
  @Input()
  auslagenFilterFormGroup!: FormGroup;

  @Input()
  auslagenDetailsFormGroup!: FormGroup;

  @Input()
  sonstigeProjektkostenListe!: StatusListen;

  @Input()
  mitarbeiterAuswahl!: Mitarbeiter[];

  @Input()
  alleMitarbeiter!: Mitarbeiter[];

  @Input()
  projektAuswahl!: Projekt[];

  @Input()
  abrechnungsmonatAuswahl!: Abrechnungsmonat[];

  @Input()
  belegartAuswahl!: Belegart[];

  @Input()
  viadeeAuslagenKostenartAuswahl!: ViadeeAuslagenKostenart[];

  @Input()
  projektabrechnungAuslagenSpinner!: SpinnerRef;

  abrechnungsmonatAbgeschlossen = true;

  sonstigeProjektkostenTabellendarstellung: SonstigeProjektkostenTabellendarstellung[] = [];

  viadeeAuslagenKostenartAbhaengigkeiten = viadeeAuslagenKostenartAbhaengigkeiten;
  belegartAbhaengigkeiten = belegartAbhaengigkeiten;

  @ViewChild(FormGroupDirective) formRef!: FormGroupDirective;

  @ViewChild(MatMultiSort) sort!: MatMultiSort;

  tabelle: TableData<SonstigeProjektkostenTabellendarstellung>;

  spalten = [
    { id: 'abrechnungsmonat', name: 'Abrechnungsmonat' },
    { id: 'projektName', name: 'Projekt' },
    { id: 'mitarbeiterName', name: 'Mitarbeiter/in' },
    { id: 'viadeeAuslagenKostenartName', name: 'Kostenart' },
    { id: 'belegartName', name: 'Belegart' },
    { id: 'kosten', name: 'Betrag (Brutto)' },
    { id: 'bemerkung', name: 'Bemerkung' },
    { id: 'zuletztGeaendertAm', name: 'Geändert am' },
    { id: 'zuletztGeaendertVon', name: 'Geändert von' },
    { id: 'actions', name: '' },
  ];

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private benutzerService: BenutzerService,
    private idGeneratorService: IdGeneratorService,
    private konstantenService: KonstantenService,
    private abrechnungsmonatAnzeigeNamePipe: AbrechnungsmonatAnzeigeNamePipe,
    private mitarbeiterAnzeigeNameKurzformPipe: MitarbeiterAnzeigeNameKurzformPipe,
    private projekteAnzeigeNamePipe: ProjekteAnzeigeNamePipe
  ) {
    this.tabelle = new TableData<SonstigeProjektkostenTabellendarstellung>(this.spalten);
    this.tabelle.dataSource = new MatMultiSortTableDataSource<SonstigeProjektkostenTabellendarstellung>(
      new MatMultiSort(),
      true
    );
  }

  ngOnInit(): void {
    this.auslagenFilterFormGroup.valueChanges.pipe(debounceTime(500)).subscribe(() => {
      if (!this.auslagenFilterFormGroup.valid) {
        return;
      }
      this.filtereSonstigeProjektUndAktualisiereTabelle();
      this.leereAuslagenDetailsForm();
    });

    this.auslagenDetailsFormGroup
      .get('viadeeAuslagenKostenart')
      ?.valueChanges.subscribe((viadeeAuslagenKostenart: ViadeeAuslagenKostenart) => {
        if (!this.abrechnungsmonatAbgeschlossen) {
          this.passeBedingteValidierungAnFuerViadeeAuslagenKostenart(viadeeAuslagenKostenart);
        }
      });

    this.auslagenDetailsFormGroup.get('belegart')?.valueChanges.subscribe((belegart) => {
      if (!this.abrechnungsmonatAbgeschlossen) {
        this.passeBedingteValidierungAnFuerBelegart(belegart);
      }
    });
  }

  ngOnChanges(): void {
    this.aktualisiereSonstigeProjektkostenTabellendarstellung();
    this.filtereSonstigeProjektUndAktualisiereTabelle();
  }

  ngAfterViewInit(): void {
    this.initialisiereTabellenSortierung();
    this.initialisiereTabellenDaten();
    this.changeDetectorRef.detectChanges();
  }

  leereAuslagenDetailsForm() {
    this.auslagenDetailsFormGroup.reset();
    setTimeout(() => this.formRef.resetForm(), 100);
    this.aendereAktivierungsstatusAuslagenDetailsFormGroup(false);
  }

  fuegeOderAktualisiereSonstigeProjektkosten() {
    if (this.auslagenDetailsFormGroup.invalid) {
      this.auslagenDetailsFormGroup.markAllAsTouched();
      return;
    }

    const sonstigeProjektkosten: SonstigeProjektkosten = this.erstelleSonstigeProjektkostenAusForm();

    // SonstigeProjektkosten aktualisieren
    if (sonstigeProjektkosten.id) {
      this.sonstigeProjektkostenListe.aktualisiereElement(sonstigeProjektkosten);
    } else {
      sonstigeProjektkosten.id = this.idGeneratorService.generiereId();
      this.sonstigeProjektkostenListe.fuegeNeuesElementHinzu(sonstigeProjektkosten);
    }

    this.aktualisiereSonstigeProjektkostenTabellendarstellung();

    this.tabelle.data = this.sonstigeProjektkostenTabellendarstellung;
    this.filtereSonstigeProjektUndAktualisiereTabelle();
    this.leereAuslagenDetailsForm();
  }

  ladeSonstigeProjektkostenInForm(
    sonstigeProjektkostenTabellendarstellung: SonstigeProjektkostenTabellendarstellung
  ): void {
    const sonstigeProjektkosten: SonstigeProjektkosten = getObjektAusListeDurchId(
      sonstigeProjektkostenTabellendarstellung.id,
      this.sonstigeProjektkostenListe.getAnzeigeListe()
    ) as SonstigeProjektkosten;

    if (!sonstigeProjektkosten.id) {
      return;
    }

    const abrechnungsmonat = {
      jahr: sonstigeProjektkosten.jahr,
      monat: sonstigeProjektkosten.monat,
      abgeschlossen: this.abrechnungsmonatIstAbgeschlossen(sonstigeProjektkosten.jahr, sonstigeProjektkosten.monat),
    } as Abrechnungsmonat;
    this.auslagenDetailsFormGroup.patchValue({
      id: sonstigeProjektkosten.id,
      abrechnungsmonat: abrechnungsmonat,
      projekt: getObjektAusListeDurchId(sonstigeProjektkosten.projektId, this.projektAuswahl) as Projekt,
      mitarbeiter: getObjektAusListeDurchId(sonstigeProjektkosten.mitarbeiterId, this.alleMitarbeiter) as Mitarbeiter,
      viadeeAuslagenKostenart: getObjektAusListeDurchId(
        sonstigeProjektkosten.viadeeAuslagenKostenartId,
        this.viadeeAuslagenKostenartAuswahl
      ) as ViadeeAuslagenKostenart,
      belegart: getObjektAusListeDurchId(sonstigeProjektkosten.belegartId, this.belegartAuswahl) as Belegart,
      betrag: convertUsNummerZuDeString(sonstigeProjektkosten.kosten),
      bemerkung: sonstigeProjektkosten.bemerkung,
    });
    this.aendereAktivierungsstatusAuslagenDetailsFormGroup(abrechnungsmonat.abgeschlossen);
  }

  entferneUnerlaubteZeichen(komponentenName: string, erlaubteExp: string) {
    entferneUnerlaubteZeichenAusFormGroupKomponente(this.auslagenDetailsFormGroup, komponentenName, erlaubteExp);
  }

  loescheSonstigeProjektkosten(sonstigeProjektkostenTabellendarstellung: SonstigeProjektkostenTabellendarstellung) {
    const sonstigeProjektkosten: SonstigeProjektkosten = getObjektAusListeDurchId(
      sonstigeProjektkostenTabellendarstellung.id,
      this.sonstigeProjektkostenListe.getAnzeigeListe()
    ) as SonstigeProjektkosten;

    this.sonstigeProjektkostenListe.loescheElement(sonstigeProjektkosten);

    this.aktualisiereSonstigeProjektkostenTabellendarstellung();

    this.tabelle.data = this.sonstigeProjektkostenTabellendarstellung;
    this.filtereSonstigeProjektUndAktualisiereTabelle();
    this.leereAuslagenDetailsForm();
  }

  abrechnungsmonatIstAbgeschlossen(jahr: number, monat: number): boolean {
    const abrechnungsmonat = this.abrechnungsmonatAuswahl.filter((abrechnungsmonat) => {
      return abrechnungsmonat.jahr === jahr && abrechnungsmonat.monat === monat;
    }) as Abrechnungsmonat[];
    return abrechnungsmonat.length === 1 ? abrechnungsmonat[0].abgeschlossen : true;
  }

  sindAbrechnungsmonateGleich(abrechnungsmonat1: Abrechnungsmonat, abrechnungsmonat2: Abrechnungsmonat): boolean {
    return CompareUtil.sindAbrechnungsmonateGleich(abrechnungsmonat1, abrechnungsmonat2);
  }

  getAbgeschlossenIcon(abrechnungsmonat: Abrechnungsmonat): string {
    return IconUtil.getAbgeschlossenIcon(abrechnungsmonat);
  }

  getAbgeschlossenIconMitBoolean(abgeschlossen: boolean): string {
    return IconUtil.getAbgeschlossenIconMitBoolean(abgeschlossen);
  }

  private initialisiereTabellenDaten() {
    this.tabelle.dataSource = new MatMultiSortTableDataSource<SonstigeProjektkostenTabellendarstellung>(
      this.sort,
      true
    );
    this.tabelle.data = this.sonstigeProjektkostenTabellendarstellung;
  }

  private initialisiereTabellenSortierung() {
    this.tabelle.sortParams = ['zuletztGeaendertAm', 'abrechnungsmonat', 'projektName', 'mitarbeiterName', 'kosten'];
    this.tabelle.sortDirs = ['desc', 'desc', 'asc', 'asc', 'asc'];

    this.tabelle.updateSortHeaders();
  }

  private filtereSonstigeProjektUndAktualisiereTabelle() {
    if (this.sonstigeProjektkostenTabellendarstellung.length === 0) {
      return;
    }

    this.projektabrechnungAuslagenSpinner.open();

    let sonstigeProjektkostenGefiltert: SonstigeProjektkostenTabellendarstellung[] =
      this.sonstigeProjektkostenTabellendarstellung.slice();

    sonstigeProjektkostenGefiltert = this.filterNachMitarbeiter(sonstigeProjektkostenGefiltert);
    sonstigeProjektkostenGefiltert = this.filterNachProjekt(sonstigeProjektkostenGefiltert);
    sonstigeProjektkostenGefiltert = this.filterNachAbrechnungmonat(sonstigeProjektkostenGefiltert);
    this.tabelle.data = sonstigeProjektkostenGefiltert;

    this.projektabrechnungAuslagenSpinner.close();
  }

  private filterNachMitarbeiter(
    sonstigeProjektkostenGefiltert: SonstigeProjektkostenTabellendarstellung[]
  ): SonstigeProjektkostenTabellendarstellung[] {
    const mitarbeiter: Mitarbeiter = this.auslagenFilterFormGroup.getRawValue().mitarbeiter;
    if (!mitarbeiter || !mitarbeiter.id) {
      return sonstigeProjektkostenGefiltert;
    }

    return sonstigeProjektkostenGefiltert.filter((sonstigeProjektkosten) => {
      return sonstigeProjektkosten.mitarbeiterName === this.mitarbeiterAnzeigeNameKurzformPipe.transform(mitarbeiter);
    });
  }

  private filterNachProjekt(
    sonstigeProjektkostenGefiltert: SonstigeProjektkostenTabellendarstellung[]
  ): SonstigeProjektkostenTabellendarstellung[] {
    const projekt: Projekt = this.auslagenFilterFormGroup.getRawValue().projekt;
    if (!projekt || !projekt.id) {
      return sonstigeProjektkostenGefiltert;
    }

    return sonstigeProjektkostenGefiltert.filter((sonstigeProjektkosten) => {
      return sonstigeProjektkosten.projektName === this.projekteAnzeigeNamePipe.transform(projekt);
    });
  }

  private filterNachAbrechnungmonat(
    sonstigeProjektkostenGefiltert: SonstigeProjektkostenTabellendarstellung[]
  ): SonstigeProjektkostenTabellendarstellung[] {
    const abrechnungsmonat: Abrechnungsmonat = this.auslagenFilterFormGroup.getRawValue().abrechnungsmonat;
    if (!abrechnungsmonat || !abrechnungsmonat.jahr) {
      return sonstigeProjektkostenGefiltert;
    }

    return sonstigeProjektkostenGefiltert.filter((sonstigeProjektkosten) => {
      return (
        sonstigeProjektkosten.jahr === abrechnungsmonat.jahr && sonstigeProjektkosten.monat === abrechnungsmonat.monat
      );
    });
  }

  private aktualisiereSonstigeProjektkostenTabellendarstellung() {
    this.sonstigeProjektkostenTabellendarstellung = (
      this.sonstigeProjektkostenListe.getAnzeigeListe() as SonstigeProjektkosten[]
    ).map((sonstigeProjektkosten) => {
      const mitarbeiter: Mitarbeiter = getObjektAusListeDurchId(
        sonstigeProjektkosten.mitarbeiterId,
        this.alleMitarbeiter
      ) as Mitarbeiter;
      const projekt: Projekt = getObjektAusListeDurchId(
        sonstigeProjektkosten.projektId,
        this.projektAuswahl
      ) as Projekt;
      const belegart: Belegart = getObjektAusListeDurchId(
        sonstigeProjektkosten.belegartId,
        this.belegartAuswahl
      ) as Belegart;
      const viadeeAuslagenKostenart: ViadeeAuslagenKostenart = getObjektAusListeDurchId(
        sonstigeProjektkosten.viadeeAuslagenKostenartId,
        this.viadeeAuslagenKostenartAuswahl
      ) as ViadeeAuslagenKostenart;
      const abrechnungsmonat: Abrechnungsmonat = this.abrechnungsmonatAuswahl.find((element: Abrechnungsmonat) => {
        return element.jahr === sonstigeProjektkosten.jahr && element.monat === sonstigeProjektkosten.monat;
      }) as Abrechnungsmonat;

      return {
        id: sonstigeProjektkosten.id,
        jahr: sonstigeProjektkosten.jahr,
        monat: sonstigeProjektkosten.monat,
        abrechnungsmonat: this.abrechnungsmonatAnzeigeNamePipe.transform(sonstigeProjektkosten),
        istAbrechnungsmonatAbgeschlossen: abrechnungsmonat.abgeschlossen,
        kosten: sonstigeProjektkosten.kosten,
        mitarbeiter: mitarbeiter,
        mitarbeiterName: this.mitarbeiterAnzeigeNameKurzformPipe.transform(mitarbeiter),
        projekt: projekt,
        projektName: this.projekteAnzeigeNamePipe.transform(projekt),
        bemerkung: sonstigeProjektkosten.bemerkung,
        belegartName: belegart.textKurz,
        viadeeAuslagenKostenartName: viadeeAuslagenKostenart.bezeichnung,
        zuletztGeaendertVon: sonstigeProjektkosten.zuletztGeaendertVon,
        zuletztGeaendertAm: sonstigeProjektkosten.zuletztGeaendertAm,
      } as SonstigeProjektkostenTabellendarstellung;
    });
  }

  private erstelleSonstigeProjektkostenAusForm(): SonstigeProjektkosten {
    return {
      id: this.auslagenDetailsFormGroup.getRawValue().id,
      jahr: this.auslagenDetailsFormGroup.getRawValue().abrechnungsmonat.jahr,
      monat: this.auslagenDetailsFormGroup.getRawValue().abrechnungsmonat.monat,
      kosten: this.auslagenDetailsFormGroup.getRawValue().betrag,
      mitarbeiterId: this.auslagenDetailsFormGroup.getRawValue().mitarbeiter?.id,
      projektId: this.auslagenDetailsFormGroup.getRawValue().projekt.id,
      bemerkung: this.auslagenDetailsFormGroup.getRawValue().bemerkung,
      belegartId: this.auslagenDetailsFormGroup.getRawValue().belegart.id,
      viadeeAuslagenKostenartId: this.auslagenDetailsFormGroup.getRawValue().viadeeAuslagenKostenart.id,
      zuletztGeaendertVon: this.benutzerService.getBenutzerNamen(),
      zuletztGeaendertAm: new Date(),
    };
  }

  private aendereAktivierungsstatusAuslagenDetailsFormGroup(abgeschlossen: boolean) {
    if (abgeschlossen) {
      this.abrechnungsmonatAbgeschlossen = true;
      this.auslagenDetailsFormGroup.disable();
    } else {
      this.abrechnungsmonatAbgeschlossen = false;
      this.auslagenDetailsFormGroup.enable();
    }
  }

  private passeBedingteValidierungAnFuerViadeeAuslagenKostenart(viadeeAuslagenKostenart: ViadeeAuslagenKostenart) {
    for (const feldAbhaengig of this.viadeeAuslagenKostenartAbhaengigkeiten.abhaengigeFelder) {
      this.auslagenDetailsFormGroup.get(feldAbhaengig)?.enable();
      if (this.viadeeAuslagenKostenartAbhaengigkeiten.defaultFelder.indexOf(feldAbhaengig) === -1) {
        this.auslagenDetailsFormGroup.get(feldAbhaengig)?.clearValidators();
        this.auslagenDetailsFormGroup.get(feldAbhaengig)?.updateValueAndValidity();
      }
    }

    if (!viadeeAuslagenKostenart.id) {
      return;
    }

    for (const feldRequiered of this.viadeeAuslagenKostenartAbhaengigkeiten.felder[viadeeAuslagenKostenart.bezeichnung]
      .required) {
      this.auslagenDetailsFormGroup
        .get(feldRequiered)
        ?.addValidators(
          this.viadeeAuslagenKostenartAbhaengigkeiten.felder[viadeeAuslagenKostenart.bezeichnung].validator
        );
      this.auslagenDetailsFormGroup.get(feldRequiered)?.updateValueAndValidity();
    }

    for (const feldDisabled of this.viadeeAuslagenKostenartAbhaengigkeiten.felder[viadeeAuslagenKostenart.bezeichnung]
      .disabled) {
      this.auslagenDetailsFormGroup.get(feldDisabled)?.reset();
      this.auslagenDetailsFormGroup
        .get(feldDisabled)
        ?.setValue(
          this.konstantenService.getBelegartByTextKurz(
            this.viadeeAuslagenKostenartAbhaengigkeiten.felder[viadeeAuslagenKostenart.bezeichnung].value
          )
        );
      this.auslagenDetailsFormGroup.get(feldDisabled)?.disable();
    }

    this.passeBedingteValidierungAnFuerBelegart(this.auslagenDetailsFormGroup.getRawValue().belegart);
  }

  private passeBedingteValidierungAnFuerBelegart(belegart: Belegart) {
    for (const feldAbhaengig of this.belegartAbhaengigkeiten.abhaengigeFelder) {
      this.auslagenDetailsFormGroup.get(feldAbhaengig)?.enable();
      this.auslagenDetailsFormGroup.get(feldAbhaengig)?.clearValidators();
      this.auslagenDetailsFormGroup.get(feldAbhaengig)?.updateValueAndValidity();
    }

    if (!belegart.id || !this.belegartAbhaengigkeiten.felder[belegart.textKurz]) {
      return;
    }

    for (const feldRequiered of this.belegartAbhaengigkeiten.felder[belegart.textKurz].required) {
      this.auslagenDetailsFormGroup.get(feldRequiered)?.addValidators(Validators.required);
      this.auslagenDetailsFormGroup.get(feldRequiered)?.updateValueAndValidity();
    }
  }
}
