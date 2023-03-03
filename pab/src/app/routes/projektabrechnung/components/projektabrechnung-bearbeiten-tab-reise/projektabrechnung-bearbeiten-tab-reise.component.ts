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
import { NavigationService } from '../../../../shared/service/navigation.service';
import { ProjektabrechnungAuslagenQueryParams } from '../../../../shared/model/query-params/projektabrechnung-auslagen-query-params';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { ProjekttypEnum } from '../../../../shared/enum/projekttyp.enum';
import { ProjektabrechnungKostenLeistung } from '../../../../shared/model/projektabrechnung/projektabrechnung-kosten-leistung';
import { Projektabrechnung } from '../../../../shared/model/projektabrechnung/projektabrechnung';

@Component({
  selector: 'pab-projektabrechnung-bearbeiten-tab-reise',
  templateUrl: './projektabrechnung-bearbeiten-tab-reise.component.html',
  styleUrls: ['./projektabrechnung-bearbeiten-tab-reise.component.scss'],
})
export class ProjektabrechnungBearbeitenTabReiseComponent implements OnInit, OnChanges {
  @Input()
  aktuelleProjektabrechnung: Projektabrechnung | undefined;

  @Input()
  pabReiseFormGroup!: FormGroup;

  @Input()
  projektabrechnungOffen!: boolean;

  @Input()
  pabAuswahlFormGroup!: FormGroup;

  @Input()
  mitarbeiterAlle!: Mitarbeiter[];

  @Output()
  aktualisiereGesamtKostenUndLeistungEvent = new EventEmitter(true);

  @Output()
  oeffneBelegeDialogEvent = new EventEmitter(true);

  @Output()
  oeffneAuslagenDialogEvent = new EventEmitter(true);

  @Output()
  oeffneSpesenUndZuschlaegeDialogEvent = new EventEmitter(true);

  @Output()
  navigiereZuArbeitsnachweisBearbeitenEvent = new EventEmitter(true);

  constructor(private navigationService: NavigationService) {}

  ngOnInit() {
    this.reagiereAufFormChange();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['projektabrechnungOffen']) {
      this.aendereAktivierungsstatusFormGroup(changes['projektabrechnungOffen'].currentValue);
    }
  }

  oeffneBelegeDialog() {
    this.oeffneBelegeDialogEvent.emit();
  }

  entferneUnerlaubteZeichen(komponentenName: string, erlaubteExp: string) {
    entferneUnerlaubteZeichenAusFormGroupKomponente(this.pabReiseFormGroup, komponentenName, erlaubteExp);
  }

  oeffneAuslagenDialog() {
    this.oeffneAuslagenDialogEvent.emit();
  }

  navigiereZuArbeitsnachweisBearbeiten(): void {
    this.navigiereZuArbeitsnachweisBearbeitenEvent.emit();
  }

  navigiereZuAuslagenErfassen(): void {
    const queryParams: ProjektabrechnungAuslagenQueryParams = {
      jahr: this.pabAuswahlFormGroup.getRawValue().abrechnungsmonat.jahr,
      monat: this.pabAuswahlFormGroup.getRawValue().abrechnungsmonat.monat,
      projektId: this.pabAuswahlFormGroup.getRawValue().projekt.id,
      mitarbeiterId: this.pabAuswahlFormGroup.getRawValue().mitarbeiter.mitarbeiterId,
    };
    const url: string = '/projektabrechnung/auslagen';
    this.navigationService.navigiereZuMitQueryParams(url, false, queryParams);
  }

  oeffneSpesenUndZuschlaegeDialog() {
    this.oeffneSpesenUndZuschlaegeDialogEvent.emit();
  }

  private aendereAktivierungsstatusFormGroup(projektabrechnungOffen: boolean): void {
    this.pabReiseFormGroup.disable();

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
      this.pabReiseFormGroup.get('kostenReisezeitenAngerechneteReisezeit')?.enable();
      this.pabReiseFormGroup.get('kostenReisezeitenKostensatz')?.enable();
      this.pabReiseFormGroup.get('kostenBelegeUndAuslagenSpesen')?.enable();
    }

    const projekt: Projekt = this.pabAuswahlFormGroup.getRawValue().projekt;
    const istProjektIntern: boolean = !!(projekt && projekt.id && projekt.projekttyp === ProjekttypEnum.INTERN);
    if (!istProjektIntern) {
      this.pabReiseFormGroup.get('leistungReisezeitenFakturierteReisezeit')?.enable();
      this.pabReiseFormGroup.get('leistungReisezeitenStundensatz')?.enable();
      this.pabReiseFormGroup.get('leistungBelegeUndAuslagenBelege')?.enable();
      this.pabReiseFormGroup.get('leistungBelegeUndAuslagenAuslagen')?.enable();
      this.pabReiseFormGroup.get('leistungBelegeUndAuslagenSpesen')?.enable();
      this.pabReiseFormGroup.get('leistungBelegeUndAuslagenZuschlaege')?.enable();
      this.pabReiseFormGroup.get('leistungPauschaleAnzahl')?.enable();
      this.pabReiseFormGroup.get('leistungPauschaleProTag')?.enable();
    }
  }

  private reagiereAufFormChange() {
    combineLatest([
      this.pabReiseFormGroup.get('kostenReisezeitenAngerechneteReisezeit')?.valueChanges,
      this.pabReiseFormGroup.get('kostenReisezeitenKostensatz')?.valueChanges,
      this.pabReiseFormGroup.get('leistungReisezeitenFakturierteReisezeit')?.valueChanges,
      this.pabReiseFormGroup.get('leistungReisezeitenStundensatz')?.valueChanges,
      this.pabReiseFormGroup.get('kostenBelegeUndAuslagenBelege')?.valueChanges,
      this.pabReiseFormGroup.get('kostenBelegeUndAuslagenAuslagen')?.valueChanges,
      this.pabReiseFormGroup.get('kostenBelegeUndAuslagenSpesen')?.valueChanges,
      this.pabReiseFormGroup.get('kostenBelegeUndAuslagenZuschlaege')?.valueChanges,
      this.pabReiseFormGroup.get('leistungBelegeUndAuslagenBelege')?.valueChanges,
      this.pabReiseFormGroup.get('leistungBelegeUndAuslagenAuslagen')?.valueChanges,
      this.pabReiseFormGroup.get('leistungBelegeUndAuslagenSpesen')?.valueChanges,
      this.pabReiseFormGroup.get('leistungBelegeUndAuslagenZuschlaege')?.valueChanges,
      this.pabReiseFormGroup.get('leistungPauschaleAnzahl')?.valueChanges,
      this.pabReiseFormGroup.get('leistungPauschaleProTag')?.valueChanges,
    ]).subscribe(() => {
      this.berechneReise();
      this.berechneGesamtKostenUndLeistung();
      this.aktualisiereGesamtKostenUndLeistungEvent.emit();
    });
  }

  private berechneReise() {
    const kostenReisezeitenBetrag: number =
      convertDeStringZuUsNummer(this.pabReiseFormGroup.getRawValue().kostenReisezeitenAngerechneteReisezeit) *
      convertDeStringZuUsNummer(this.pabReiseFormGroup.getRawValue().kostenReisezeitenKostensatz);
    this.pabReiseFormGroup
      .get('kostenReisezeitenBetrag')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
          rundeNummerAufZweiNackommastellen(kostenReisezeitenBetrag)
        )
      );

    const leistungReisezeitenBetrag: number =
      convertDeStringZuUsNummer(this.pabReiseFormGroup.getRawValue().leistungReisezeitenFakturierteReisezeit) *
      convertDeStringZuUsNummer(this.pabReiseFormGroup.getRawValue().leistungReisezeitenStundensatz);
    this.pabReiseFormGroup
      .get('leistungReisezeitenBetrag')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
          rundeNummerAufZweiNackommastellen(leistungReisezeitenBetrag)
        )
      );

    let kostenBelegeUndAuslagenBetrag: number = convertDeStringZuUsNummer(
      this.pabReiseFormGroup.getRawValue().kostenBelegeUndAuslagenBelege
    );
    kostenBelegeUndAuslagenBetrag += convertDeStringZuUsNummer(
      this.pabReiseFormGroup.getRawValue().kostenBelegeUndAuslagenAuslagen
    );
    kostenBelegeUndAuslagenBetrag += convertDeStringZuUsNummer(
      this.pabReiseFormGroup.getRawValue().kostenBelegeUndAuslagenSpesen
    );
    kostenBelegeUndAuslagenBetrag += convertDeStringZuUsNummer(
      this.pabReiseFormGroup.getRawValue().kostenBelegeUndAuslagenZuschlaege
    );
    this.pabReiseFormGroup
      .get('kostenBelegeUndAuslagenBetrag')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
          rundeNummerAufZweiNackommastellen(kostenBelegeUndAuslagenBetrag)
        )
      );

    let leistungBelegeUndAuslagenBetrag: number = convertDeStringZuUsNummer(
      this.pabReiseFormGroup.getRawValue().leistungBelegeUndAuslagenBelege
    );
    leistungBelegeUndAuslagenBetrag += convertDeStringZuUsNummer(
      this.pabReiseFormGroup.getRawValue().leistungBelegeUndAuslagenAuslagen
    );
    leistungBelegeUndAuslagenBetrag += convertDeStringZuUsNummer(
      this.pabReiseFormGroup.getRawValue().leistungBelegeUndAuslagenSpesen
    );
    leistungBelegeUndAuslagenBetrag += convertDeStringZuUsNummer(
      this.pabReiseFormGroup.getRawValue().leistungBelegeUndAuslagenZuschlaege
    );
    this.pabReiseFormGroup
      .get('leistungBelegeUndAuslagenBetrag')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
          rundeNummerAufZweiNackommastellen(leistungBelegeUndAuslagenBetrag)
        )
      );

    const leistungPauschaleBetrag: number =
      convertDeStringZuUsNummer(this.pabReiseFormGroup.getRawValue().leistungPauschaleAnzahl) *
      convertDeStringZuUsNummer(this.pabReiseFormGroup.getRawValue().leistungPauschaleProTag);
    this.pabReiseFormGroup
      .get('leistungPauschaleBetrag')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(
          rundeNummerAufZweiNackommastellen(leistungPauschaleBetrag)
        )
      );
  }

  private berechneGesamtKostenUndLeistung() {
    let gesamtKosten: number = convertDeStringZuUsNummer(this.pabReiseFormGroup.getRawValue().kostenReisezeitenBetrag);
    gesamtKosten += convertDeStringZuUsNummer(this.pabReiseFormGroup.getRawValue().kostenBelegeUndAuslagenBetrag);
    this.pabReiseFormGroup
      .get('kosten')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(rundeNummerAufZweiNackommastellen(gesamtKosten))
      );

    let gesamtLeistung: number = convertDeStringZuUsNummer(
      this.pabReiseFormGroup.getRawValue().leistungReisezeitenBetrag
    );
    gesamtLeistung += convertDeStringZuUsNummer(this.pabReiseFormGroup.getRawValue().leistungBelegeUndAuslagenBetrag);
    gesamtLeistung += convertDeStringZuUsNummer(this.pabReiseFormGroup.getRawValue().leistungPauschaleBetrag);
    this.pabReiseFormGroup
      .get('leistung')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(rundeNummerAufZweiNackommastellen(gesamtLeistung))
      );
  }
}
