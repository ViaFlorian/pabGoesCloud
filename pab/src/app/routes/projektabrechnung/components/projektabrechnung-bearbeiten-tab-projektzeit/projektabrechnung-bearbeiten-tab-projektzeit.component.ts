import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { combineLatest } from 'rxjs';
import {
  convertDeStringZuUsNummer,
  convertUsNummerZuDeStringMitGenauZweiNachkommastellen,
  rundeNummerAufZweiNackommastellen,
} from '../../../../shared/util/nummer-converter.util';
import { entferneUnerlaubteZeichenAusFormGroupKomponente } from '../../../../shared/util/regex-control.util';
import { BearbeitenProjektzeitGesamtstundenErrorStateMatcher } from '../../validation/bearbeiten-projektzeit-gesamtstunden-error-state-matcher';
import { ProjektabrechnungKostenLeistung } from '../../../../shared/model/projektabrechnung/projektabrechnung-kosten-leistung';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { ProjekttypEnum } from '../../../../shared/enum/projekttyp.enum';

@Component({
  selector: 'pab-projektabrechnung-bearbeiten-tab-projektzeit',
  templateUrl: './projektabrechnung-bearbeiten-tab-projektzeit.component.html',
  styleUrls: ['./projektabrechnung-bearbeiten-tab-projektzeit.component.scss'],
})
export class ProjektabrechnungBearbeitenTabProjektzeitComponent implements OnInit, OnChanges {
  @Input()
  pabProjektzeitFormGroup!: FormGroup;

  @Input()
  pabProjektzeitAlternativeStundensaetzeFormArray!: FormArray;

  @Input()
  projektabrechnungOffen!: boolean;

  @Input()
  pabAuswahlFormGroup!: FormGroup;

  @Input()
  mitarbeiterAlle!: Mitarbeiter[];

  @Output()
  ergaenzeAlternativenStundensatzEvent = new EventEmitter(true);

  @Output()
  aktualisiereGesamtKostenUndLeistungEvent = new EventEmitter(true);

  bearbeitenProjektzeitGesamtstundenErrorStateMatcher: BearbeitenProjektzeitGesamtstundenErrorStateMatcher =
    new BearbeitenProjektzeitGesamtstundenErrorStateMatcher();

  constructor(private fb: FormBuilder) {}

  ngOnInit() {
    this.reagiereAufFormChange();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['projektabrechnungOffen']) {
      this.aendereAktivierungsstatusFormGroup(changes['projektabrechnungOffen'].currentValue);
    }
  }

  fuegeAlternativenStundensatzHinzu(): void {
    this.ergaenzeAlternativenStundensatzEvent.emit();
  }

  loescheAlternativenStundensatz(index: number): void {
    this.pabProjektzeitAlternativeStundensaetzeFormArray.removeAt(index);
  }

  entferneUnerlaubteZeichen(komponentenName: string, erlaubteExp: string) {
    entferneUnerlaubteZeichenAusFormGroupKomponente(this.pabProjektzeitFormGroup, komponentenName, erlaubteExp);
  }

  entferneUnerlaubteZeichenAusAlternativenStundensatz(komponentenName: string, index: number, erlaubteExp: string) {
    entferneUnerlaubteZeichenAusFormGroupKomponente(
      this.pabProjektzeitAlternativeStundensaetzeFormArray.at(index) as FormGroup,
      komponentenName,
      erlaubteExp
    );
  }

  private aendereAktivierungsstatusFormGroup(projektabrechnungOffen: boolean): void {
    this.pabProjektzeitFormGroup.disable();

    if (!projektabrechnungOffen) {
      return;
    }

    this.pabProjektzeitFormGroup.get('kostenStunden')?.enable();

    const projektabrechnungKostenLeistung: ProjektabrechnungKostenLeistung =
      this.pabAuswahlFormGroup.getRawValue().mitarbeiter;
    if (projektabrechnungKostenLeistung.ohneMitarbeiterBezug) {
      return;
    }

    const mitarbeiter: Mitarbeiter = getObjektAusListeDurchId(
      projektabrechnungKostenLeistung.mitarbeiterId,
      this.mitarbeiterAlle
    ) as Mitarbeiter;
    if (!mitarbeiter.intern) {
      this.pabProjektzeitFormGroup.get('kostenKostensatz')?.enable();
    }

    const projekt: Projekt = this.pabAuswahlFormGroup.getRawValue().projekt;
    const istProjektIntern: boolean = !!(projekt && projekt.id && projekt.projekttyp === ProjekttypEnum.INTERN);
    if (!istProjektIntern) {
      this.pabProjektzeitFormGroup.get('leistungStundensatz')?.enable();
    }
  }

  private reagiereAufFormChange() {
    combineLatest([
      this.pabProjektzeitFormGroup.get('kostenKostensatz')?.valueChanges,
      this.pabProjektzeitFormGroup.get('kostenStunden')?.valueChanges,
      this.pabProjektzeitFormGroup.get('leistungStundensatz')?.valueChanges,
    ]).subscribe(() => {
      this.berechneProjektzeit();
      this.berechneGesamtKostenUndLeistung();
      this.aktualisiereGesamtKostenUndLeistungEvent.emit();
    });

    this.pabProjektzeitAlternativeStundensaetzeFormArray.valueChanges.subscribe(() => {
      this.pabProjektzeitAlternativeStundensaetzeFormArray.controls.forEach((formControl) => {
        combineLatest([
          formControl.get('kostenKostensatz')?.valueChanges,
          formControl.get('kostenStunden')?.valueChanges,
          formControl.get('leistungStundensatz')?.valueChanges,
        ]).subscribe(() => {
          this.berechneProjektzeit();
          this.berechneGesamtKostenUndLeistung();
          this.aktualisiereGesamtKostenUndLeistungEvent.emit();
        });
      });
    });
  }

  private berechneProjektzeit() {
    const kostenBetrag: number =
      convertDeStringZuUsNummer(this.pabProjektzeitFormGroup.getRawValue().kostenStunden) *
      convertDeStringZuUsNummer(this.pabProjektzeitFormGroup.getRawValue().kostenKostensatz);
    this.pabProjektzeitFormGroup
      .get('kostenBetrag')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(rundeNummerAufZweiNackommastellen(kostenBetrag))
      );

    const leistungBetrag: number =
      convertDeStringZuUsNummer(this.pabProjektzeitFormGroup.getRawValue().kostenStunden) *
      convertDeStringZuUsNummer(this.pabProjektzeitFormGroup.getRawValue().leistungStundensatz);
    this.pabProjektzeitFormGroup
      .get('leistungBetrag')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(rundeNummerAufZweiNackommastellen(leistungBetrag))
      );

    this.pabProjektzeitAlternativeStundensaetzeFormArray.controls.forEach((control) => {
      if (!(control instanceof FormGroup)) {
        return;
      }
      const kostenBetrag: number =
        convertDeStringZuUsNummer((control as FormGroup).getRawValue().kostenStunden) *
        convertDeStringZuUsNummer((control as FormGroup).getRawValue().kostenKostensatz);
      (control as FormGroup)
        .get('kostenBetrag')
        ?.setValue(
          convertUsNummerZuDeStringMitGenauZweiNachkommastellen(rundeNummerAufZweiNackommastellen(kostenBetrag))
        );

      const leistungBetrag: number =
        convertDeStringZuUsNummer((control as FormGroup).getRawValue().kostenStunden) *
        convertDeStringZuUsNummer((control as FormGroup).getRawValue().leistungStundensatz);
      (control as FormGroup)
        .get('leistungBetrag')
        ?.setValue(
          convertUsNummerZuDeStringMitGenauZweiNachkommastellen(rundeNummerAufZweiNackommastellen(leistungBetrag))
        );
    });
  }

  private berechneGesamtKostenUndLeistung() {
    let gesamtKosten: number = convertDeStringZuUsNummer(this.pabProjektzeitFormGroup.getRawValue().kostenBetrag);
    let gesamtLeistung: number = convertDeStringZuUsNummer(this.pabProjektzeitFormGroup.getRawValue().leistungBetrag);

    this.pabProjektzeitAlternativeStundensaetzeFormArray.controls.forEach((control) => {
      if (!(control instanceof FormGroup)) {
        return;
      }

      gesamtKosten += convertDeStringZuUsNummer((control as FormGroup).getRawValue().kostenBetrag);
      gesamtLeistung += convertDeStringZuUsNummer((control as FormGroup).getRawValue().leistungBetrag);
    });

    this.pabProjektzeitFormGroup
      .get('kosten')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(rundeNummerAufZweiNackommastellen(gesamtKosten))
      );
    this.pabProjektzeitFormGroup
      .get('leistung')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(rundeNummerAufZweiNackommastellen(gesamtLeistung))
      );
  }
}
