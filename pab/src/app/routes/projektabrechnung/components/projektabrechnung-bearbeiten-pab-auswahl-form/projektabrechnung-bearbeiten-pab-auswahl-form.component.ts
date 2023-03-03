import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { ProjektAbrechnungsmonat } from '../../../../shared/model/projektabrechnung/projekt-abrechnungsmonat';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { Kunde } from '../../../../shared/model/kunde/kunde';
import { Organisationseinheit } from '../../../../shared/model/organisationseinheit/organisationseinheit';
import { MitarbeiterAnzeigeNamePipe } from '../../../../shared/pipe/mitarbeiter-anzeige-name.pipe';
import {
  getObjektAusListeDurchId,
  getObjektAusListeDurchScribeId,
} from '../../../../shared/util/objekt-in-array-finden.util';
import { KundeAnzeigeNamePipe } from '../../../../shared/pipe/kunde-anzeige-name.pipe';
import { OrganisationseinheitAnzeigeNamePipe } from '../../../../shared/pipe/organisationseinheit-anzeige-name.pipe';
import { ProjektabrechnungKostenLeistung } from '../../../../shared/model/projektabrechnung/projektabrechnung-kosten-leistung';
import * as CompareUtil from '../../../../shared/util/compare.util';
import { Abrechnungsmonat } from '../../../../shared/model/sonstiges/abrechnungsmonat';
import * as IconUtil from '../../../../shared/util/icon.util';

@Component({
  selector: 'pab-projektabrechnung-bearbeiten-pab-auswahl-form',
  templateUrl: './projektabrechnung-bearbeiten-pab-auswahl-form.component.html',
  styleUrls: ['./projektabrechnung-bearbeiten-pab-auswahl-form.component.scss'],
})
export class ProjektabrechnungBearbeitenPabAuswahlFormComponent implements OnInit, OnChanges {
  @Input()
  pabAuswahlFormGroup!: FormGroup;

  @Input()
  mitarbeiterAlle!: Mitarbeiter[];

  @Input()
  sachbearbeiterAlle!: Mitarbeiter[];

  @Input()
  kundeAlle!: Kunde[];

  @Input()
  organisationseinheitAlle!: Organisationseinheit[];

  @Input()
  projektAuswahl!: Projekt[];

  @Input()
  abrechnungsmonatAuswahl!: ProjektAbrechnungsmonat[];

  @Input()
  projektabrechnungKostenLeistungAuswahl!: ProjektabrechnungKostenLeistung[];

  anzahlMitarbeiter: number = 0;

  constructor(
    private mitarbeiterAnzeigeNamePipe: MitarbeiterAnzeigeNamePipe,
    private kundeAnzeigeNamePipe: KundeAnzeigeNamePipe,
    private organisationseinheitAnzeigeNamePipe: OrganisationseinheitAnzeigeNamePipe
  ) {}

  ngOnInit(): void {
    this.reagiereAufAenderungProjekt();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['projektabrechnungKostenLeistungAuswahl']) {
      this.anzahlMitarbeiter =
        this.projektabrechnungKostenLeistungAuswahl.length > 0
          ? this.projektabrechnungKostenLeistungAuswahl.length - 1
          : 0;
    }
  }

  sindAbrechnungsmonateGleich(
    abrechnungsmonat1: ProjektAbrechnungsmonat,
    abrechnungsmonat2: ProjektAbrechnungsmonat
  ): boolean {
    return CompareUtil.sindProjektAbrechnungsmonateGleich(abrechnungsmonat1, abrechnungsmonat2);
  }

  getAbgeschlossenIcon(abrechnungsmonat: Abrechnungsmonat): string {
    return IconUtil.getAbgeschlossenIcon(abrechnungsmonat);
  }

  private reagiereAufAenderungProjekt(): void {
    this.pabAuswahlFormGroup.get('projekt')?.valueChanges.subscribe((projekt: Projekt) => {
      this.patchProjekt(projekt);
    });
  }

  private patchProjekt(projekt: Projekt): void {
    if (projekt && projekt.id) {
      this.pabAuswahlFormGroup.patchValue({
        kunde: this.kundeAnzeigeNamePipe.transform(
          getObjektAusListeDurchScribeId(projekt.kundeId, this.kundeAlle) as Kunde
        ),
        projekttyp: projekt.projekttyp,
        sachbearbeiter: projekt.sachbearbeiterId
          ? this.mitarbeiterAnzeigeNamePipe.transform(
              getObjektAusListeDurchId(projekt.sachbearbeiterId, this.sachbearbeiterAlle) as Mitarbeiter
            )
          : 'nicht konfiguriert',
        organisationseinheit: this.organisationseinheitAnzeigeNamePipe.transform(
          getObjektAusListeDurchScribeId(
            projekt.organisationseinheitId,
            this.organisationseinheitAlle
          ) as Organisationseinheit
        ),
        projektverantwortung: projekt.verantwortlicherMitarbeiterId
          ? this.mitarbeiterAnzeigeNamePipe.transform(
              getObjektAusListeDurchId(projekt.verantwortlicherMitarbeiterId, this.mitarbeiterAlle) as Mitarbeiter
            )
          : 'nicht konfiguriert',
        geschaeftsstelle: projekt.geschaeftsstelle,
        kosten: '',
        leistung: '',
      });
    } else {
      this.pabAuswahlFormGroup.get('kunde')?.reset();
      this.pabAuswahlFormGroup.get('projekttyp')?.reset();
      this.pabAuswahlFormGroup.get('sachbearbeiter')?.reset();
      this.pabAuswahlFormGroup.get('organisationseinheit')?.reset();
      this.pabAuswahlFormGroup.get('projektverantwortung')?.reset();
      this.pabAuswahlFormGroup.get('geschaeftsstelle')?.reset();
      this.pabAuswahlFormGroup.get('kosten')?.reset();
      this.pabAuswahlFormGroup.get('leistung')?.reset();
    }
  }
}
