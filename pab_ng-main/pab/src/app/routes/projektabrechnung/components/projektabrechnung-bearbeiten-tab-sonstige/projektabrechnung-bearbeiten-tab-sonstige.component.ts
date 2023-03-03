import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { combineLatest } from 'rxjs';
import {
  convertDeStringZuUsNummer,
  convertUsNummerZuDeStringMitGenauZweiNachkommastellen,
  rundeNummerAufZweiNackommastellen,
} from '../../../../shared/util/nummer-converter.util';
import { entferneUnerlaubteZeichenAusFormGroupKomponente } from '../../../../shared/util/regex-control.util';
import { NavigationService } from '../../../../shared/service/navigation.service';
import { ProjektabrechnungAuslagenQueryParams } from '../../../../shared/model/query-params/projektabrechnung-auslagen-query-params';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { ProjekttypEnum } from '../../../../shared/enum/projekttyp.enum';
import { ProjektabrechnungKostenLeistung } from '../../../../shared/model/projektabrechnung/projektabrechnung-kosten-leistung';
import { Projektabrechnung } from '../../../../shared/model/projektabrechnung/projektabrechnung';

@Component({
  selector: 'pab-projektabrechnung-bearbeiten-tab-sonstige',
  templateUrl: './projektabrechnung-bearbeiten-tab-sonstige.component.html',
  styleUrls: ['./projektabrechnung-bearbeiten-tab-sonstige.component.scss'],
})
export class ProjektabrechnungBearbeitenTabSonstigeComponent implements OnInit, OnChanges {
  @Input()
  aktuelleProjektabrechnung: Projektabrechnung | undefined;

  @Input()
  pabSonstigeFormGroup!: FormGroup;

  @Input()
  projektabrechnungOffen!: boolean;

  @Input()
  pabAuswahlFormGroup!: FormGroup;

  @Input()
  mitarbeiterAlle!: Mitarbeiter[];

  @Output()
  aktualisiereGesamtKostenUndLeistungEvent = new EventEmitter(true);

  @Output()
  oeffneAuslagenDialogEvent = new EventEmitter(true);

  constructor(private navigationService: NavigationService) {}

  ngOnInit() {
    this.reagiereAufFormChange();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['projektabrechnungOffen']) {
      this.aendereAktivierungsstatusFormGroup(changes['projektabrechnungOffen'].currentValue);
    }
  }

  entferneUnerlaubteZeichen(komponentenName: string, erlaubteExp: string) {
    entferneUnerlaubteZeichenAusFormGroupKomponente(this.pabSonstigeFormGroup, komponentenName, erlaubteExp);
  }

  oeffneAuslagenDialog(): void {
    this.oeffneAuslagenDialogEvent.emit();
  }

  navigiereZuAuslagenErfassen(): void {
    const projektabrechnungKostenLeistung: ProjektabrechnungKostenLeistung =
      this.pabAuswahlFormGroup.getRawValue().mitarbeiter;

    const queryParams: ProjektabrechnungAuslagenQueryParams = {
      jahr: this.pabAuswahlFormGroup.getRawValue().abrechnungsmonat.jahr,
      monat: this.pabAuswahlFormGroup.getRawValue().abrechnungsmonat.monat,
      projektId: this.pabAuswahlFormGroup.getRawValue().projekt.id,
      mitarbeiterId: projektabrechnungKostenLeistung.ohneMitarbeiterBezug
        ? ''
        : projektabrechnungKostenLeistung.mitarbeiterId,
    };
    const url: string = '/projektabrechnung/auslagen';
    this.navigationService.navigiereZuMitQueryParams(url, false, queryParams);
  }

  private aendereAktivierungsstatusFormGroup(projektabrechnungOffen: boolean): void {
    this.pabSonstigeFormGroup.disable();

    if (!projektabrechnungOffen) {
      return;
    }

    const projektabrechnungKostenLeistung: ProjektabrechnungKostenLeistung =
      this.pabAuswahlFormGroup.getRawValue().mitarbeiter;
    const mitarbeiter: Mitarbeiter = getObjektAusListeDurchId(
      projektabrechnungKostenLeistung.mitarbeiterId,
      this.mitarbeiterAlle
    ) as Mitarbeiter;
    if (projektabrechnungKostenLeistung.ohneMitarbeiterBezug || !mitarbeiter.intern) {
      this.pabSonstigeFormGroup.get('kostenSonstigePauschale')?.enable();
    }

    const projekt: Projekt = this.pabAuswahlFormGroup.getRawValue().projekt;
    const istProjektIntern: boolean = !!(projekt && projekt.id && projekt.projekttyp === ProjekttypEnum.INTERN);
    if (!istProjektIntern) {
      this.pabSonstigeFormGroup.get('leistungSonstigePauschale')?.enable();
    }

    if (projektabrechnungKostenLeistung.ohneMitarbeiterBezug || !mitarbeiter.intern || !istProjektIntern) {
      this.pabSonstigeFormGroup.get('bemerkung')?.enable();
    }
  }

  private reagiereAufFormChange() {
    combineLatest([
      this.pabSonstigeFormGroup.get('kostenSonstigeAuslagen')?.valueChanges,
      this.pabSonstigeFormGroup.get('kostenSonstigePauschale')?.valueChanges,
      this.pabSonstigeFormGroup.get('leistungSonstigePauschale')?.valueChanges,
    ]).subscribe(() => {
      this.berechneSonstige();
      this.berechneGesamtKostenUndLeistung();
      this.aktualisiereGesamtKostenUndLeistungEvent.emit();
    });
  }

  private berechneSonstige() {
    let kostenSonstigeBetrag: number = convertDeStringZuUsNummer(
      this.pabSonstigeFormGroup.getRawValue().kostenSonstigeAuslagen
    );
    kostenSonstigeBetrag += convertDeStringZuUsNummer(this.pabSonstigeFormGroup.getRawValue().kostenSonstigePauschale);
    this.pabSonstigeFormGroup
      .get('kostenSonstigeBetrag')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(rundeNummerAufZweiNackommastellen(kostenSonstigeBetrag))
      );

    const leistungSonstigeBetrag: number = convertDeStringZuUsNummer(
      this.pabSonstigeFormGroup.getRawValue().leistungSonstigePauschale
    );
    this.pabSonstigeFormGroup
      .get('leistungSonstigeBetrag')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(rundeNummerAufZweiNackommastellen(leistungSonstigeBetrag))
      );
  }

  private berechneGesamtKostenUndLeistung() {
    const gesamtKosten: number = convertDeStringZuUsNummer(
      this.pabSonstigeFormGroup.getRawValue().kostenSonstigeBetrag
    );
    this.pabSonstigeFormGroup
      .get('kosten')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(rundeNummerAufZweiNackommastellen(gesamtKosten))
      );

    const gesamtLeistung: number = convertDeStringZuUsNummer(
      this.pabSonstigeFormGroup.getRawValue().leistungSonstigeBetrag
    );
    this.pabSonstigeFormGroup
      .get('leistung')
      ?.setValue(
        convertUsNummerZuDeStringMitGenauZweiNachkommastellen(rundeNummerAufZweiNackommastellen(gesamtLeistung))
      );
  }
}
