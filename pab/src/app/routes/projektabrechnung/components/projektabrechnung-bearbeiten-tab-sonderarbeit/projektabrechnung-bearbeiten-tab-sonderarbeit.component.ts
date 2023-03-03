import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { combineLatest } from 'rxjs';
import {
  convertDeStringZuUsNummer,
  convertUsNummerZuDeStringMitGenauZweiNachkommastellen,
  rundeNummerAufZweiNackommastellen,
} from '../../../../shared/util/nummer-converter.util';
import { entferneUnerlaubteZeichenAusFormGroupKomponente } from '../../../../shared/util/regex-control.util';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { ProjekttypEnum } from '../../../../shared/enum/projekttyp.enum';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { ProjektabrechnungKostenLeistung } from '../../../../shared/model/projektabrechnung/projektabrechnung-kosten-leistung';

@Component({
  selector: 'pab-projektabrechnung-bearbeiten-tab-sonderarbeit',
  templateUrl: './projektabrechnung-bearbeiten-tab-sonderarbeit.component.html',
  styleUrls: ['./projektabrechnung-bearbeiten-tab-sonderarbeit.component.scss'],
})
export class ProjektabrechnungBearbeitenTabSonderarbeitComponent implements OnInit, OnChanges {
  @Input()
  pabSonderarbeitFormGroup!: FormGroup;

  @Input()
  projektabrechnungOffen!: boolean;

  @Input()
  pabAuswahlFormGroup!: FormGroup;

  @Input()
  mitarbeiterAlle!: Mitarbeiter[];

  @Output()
  aktualisiereGesamtKostenUndLeistungEvent = new EventEmitter(true);

  ngOnInit() {
    this.reagiereAufFormChange();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['projektabrechnungOffen']) {
      this.aendereAktivierungsstatusFormGroup(changes['projektabrechnungOffen'].currentValue);
    }
  }

  entferneUnerlaubteZeichen(komponentenName: string, erlaubteExp: string) {
    entferneUnerlaubteZeichenAusFormGroupKomponente(this.pabSonderarbeitFormGroup, komponentenName, erlaubteExp);
  }

  private aendereAktivierungsstatusFormGroup(projektabrechnungOffen: boolean): void {
    this.pabSonderarbeitFormGroup.disable();

    if (!projektabrechnungOffen) {
      return;
    }

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
      this.pabSonderarbeitFormGroup.get('kostenRufbereitschaftenAnzahlStunden')?.enable();
      this.pabSonderarbeitFormGroup.get('kostenRufbereitschaftenKostensatz')?.enable();
      this.pabSonderarbeitFormGroup.get('leistungRufbereitschaftenAnzahlStunden')?.enable();
      this.pabSonderarbeitFormGroup.get('leistungRufbereitschaftenStundensatz')?.enable();
      this.pabSonderarbeitFormGroup.get('leistungRufbereitschaftenPauschale')?.enable();
      this.pabSonderarbeitFormGroup.get('kostenSonderarbeitszeitenPauschale')?.enable();
      this.pabSonderarbeitFormGroup.get('leistungSonderarbeitszeitenPauschale')?.enable();
    }

    const projekt: Projekt = this.pabAuswahlFormGroup.getRawValue().projekt;
    const istProjektIntern: boolean = !!(projekt && projekt.id && projekt.projekttyp === ProjekttypEnum.INTERN);
    if (!istProjektIntern) {
      this.pabSonderarbeitFormGroup.get('leistungRufbereitschaftenAnzahlStunden')?.enable();
      this.pabSonderarbeitFormGroup.get('leistungRufbereitschaftenStundensatz')?.enable();
      this.pabSonderarbeitFormGroup.get('leistungRufbereitschaftenPauschale')?.enable();
      this.pabSonderarbeitFormGroup.get('leistungSonderarbeitszeitenPauschale')?.enable();
    } else {
      this.pabSonderarbeitFormGroup.get('leistungRufbereitschaftenAnzahlStunden')?.disable();
      this.pabSonderarbeitFormGroup.get('leistungRufbereitschaftenStundensatz')?.disable();
      this.pabSonderarbeitFormGroup.get('leistungRufbereitschaftenPauschale')?.disable();
      this.pabSonderarbeitFormGroup.get('leistungSonderarbeitszeitenPauschale')?.disable();
    }
  }

  private reagiereAufFormChange() {
    combineLatest([
      this.pabSonderarbeitFormGroup.get('kostenRufbereitschaftenAnzahlStunden')?.valueChanges,
      this.pabSonderarbeitFormGroup.get('kostenRufbereitschaftenKostensatz')?.valueChanges,
      this.pabSonderarbeitFormGroup.get('leistungRufbereitschaftenAnzahlStunden')?.valueChanges,
      this.pabSonderarbeitFormGroup.get('leistungRufbereitschaftenStundensatz')?.valueChanges,
      this.pabSonderarbeitFormGroup.get('leistungRufbereitschaftenPauschale')?.valueChanges,
      this.pabSonderarbeitFormGroup.get('kostenSonderarbeitszeitenAnzahlStunden50')?.valueChanges,
      this.pabSonderarbeitFormGroup.get('kostenSonderarbeitszeitenAnzahlStunden100')?.valueChanges,
      this.pabSonderarbeitFormGroup.get('kostenSonderarbeitszeitenKostensatz')?.valueChanges,
      this.pabSonderarbeitFormGroup.get('kostenSonderarbeitszeitenPauschale')?.valueChanges,
      this.pabSonderarbeitFormGroup.get('leistungSonderarbeitszeitenPauschale')?.valueChanges,
    ]).subscribe(() => {
      this.berechneSonderarbeit();
      this.berechneGesamtKostenUndLeistung();
      this.aktualisiereGesamtKostenUndLeistungEvent.emit();
    });
  }

  private berechneSonderarbeit() {
    const kostenRufbereitschaftenBetrag: number =
      convertDeStringZuUsNummer(this.pabSonderarbeitFormGroup.getRawValue().kostenRufbereitschaftenAnzahlStunden) *
      convertDeStringZuUsNummer(this.pabSonderarbeitFormGroup.getRawValue().kostenRufbereitschaftenKostensatz);
    this.pabSonderarbeitFormGroup
      .get('kostenRufbereitschaftenBetrag')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
          rundeNummerAufZweiNackommastellen(kostenRufbereitschaftenBetrag)
        )
      );

    let leistungRufbereitschaftenBetrag: number =
      convertDeStringZuUsNummer(this.pabSonderarbeitFormGroup.getRawValue().leistungRufbereitschaftenAnzahlStunden) *
      convertDeStringZuUsNummer(this.pabSonderarbeitFormGroup.getRawValue().leistungRufbereitschaftenStundensatz);
    leistungRufbereitschaftenBetrag += convertDeStringZuUsNummer(
      this.pabSonderarbeitFormGroup.getRawValue().leistungRufbereitschaftenPauschale
    );
    this.pabSonderarbeitFormGroup
      .get('leistungRufbereitschaftenBetrag')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
          rundeNummerAufZweiNackommastellen(leistungRufbereitschaftenBetrag)
        )
      );

    let kostenSonderarbeitszeitenBetrag: number =
      convertDeStringZuUsNummer(this.pabSonderarbeitFormGroup.getRawValue().kostenSonderarbeitszeitenAnzahlStunden50) *
      0.5 *
      convertDeStringZuUsNummer(this.pabSonderarbeitFormGroup.getRawValue().kostenSonderarbeitszeitenKostensatz);
    kostenSonderarbeitszeitenBetrag +=
      convertDeStringZuUsNummer(this.pabSonderarbeitFormGroup.getRawValue().kostenSonderarbeitszeitenAnzahlStunden100) *
      convertDeStringZuUsNummer(this.pabSonderarbeitFormGroup.getRawValue().kostenSonderarbeitszeitenKostensatz);
    kostenSonderarbeitszeitenBetrag += convertDeStringZuUsNummer(
      this.pabSonderarbeitFormGroup.getRawValue().kostenSonderarbeitszeitenPauschale
    );
    this.pabSonderarbeitFormGroup
      .get('kostenSonderarbeitszeitenBetrag')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
          rundeNummerAufZweiNackommastellen(kostenSonderarbeitszeitenBetrag)
        )
      );

    const leistungSonderarbeitszeitenBetrag: number = convertDeStringZuUsNummer(
      this.pabSonderarbeitFormGroup.getRawValue().leistungSonderarbeitszeitenPauschale
    );
    this.pabSonderarbeitFormGroup
      .get('leistungSonderarbeitszeitenBetrag')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
          rundeNummerAufZweiNackommastellen(leistungSonderarbeitszeitenBetrag)
        )
      );
  }

  private berechneGesamtKostenUndLeistung() {
    let gesamtKosten: number = convertDeStringZuUsNummer(
      this.pabSonderarbeitFormGroup.getRawValue().kostenRufbereitschaftenBetrag
    );
    gesamtKosten += convertDeStringZuUsNummer(
      this.pabSonderarbeitFormGroup.getRawValue().kostenSonderarbeitszeitenBetrag
    );
    this.pabSonderarbeitFormGroup
      .get('kosten')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(rundeNummerAufZweiNackommastellen(gesamtKosten))
      );

    let gesamtLeistung: number = convertDeStringZuUsNummer(
      this.pabSonderarbeitFormGroup.getRawValue().leistungRufbereitschaftenBetrag
    );
    gesamtLeistung += convertDeStringZuUsNummer(
      this.pabSonderarbeitFormGroup.getRawValue().leistungSonderarbeitszeitenBetrag
    );
    this.pabSonderarbeitFormGroup
      .get('leistung')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(rundeNummerAufZweiNackommastellen(gesamtLeistung))
      );
  }
}
