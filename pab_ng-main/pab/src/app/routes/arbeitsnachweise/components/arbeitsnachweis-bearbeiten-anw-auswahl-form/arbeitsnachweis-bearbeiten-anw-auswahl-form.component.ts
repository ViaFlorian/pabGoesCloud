import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { IsInternToTextPipe } from '../../../../shared/pipe/is-intern-to-text.pipe';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { MitarbeiterAnzeigeNamePipe } from '../../../../shared/pipe/mitarbeiter-anzeige-name.pipe';
import { getObjektAusListeDurchId } from '../../../../shared/util/objekt-in-array-finden.util';
import { MitarbeiterAbrechnungsmonat } from '../../../../shared/model/mitarbeiter/mitarbeiter-abrechnungsmonat';
import * as CompareUtil from '../../../../shared/util/compare.util';
import { Abrechnungsmonat } from '../../../../shared/model/sonstiges/abrechnungsmonat';
import * as IconUtil from '../../../../shared/util/icon.util';

@Component({
  selector: 'pab-arbeitsnachweis-bearbeiten-anw-auswahl-form',
  templateUrl: './arbeitsnachweis-bearbeiten-anw-auswahl-form.component.html',
  styleUrls: ['./arbeitsnachweis-bearbeiten-anw-auswahl-form.component.scss'],
})
export class ArbeitsnachweisBearbeitenAnwAuswahlFormComponent implements OnInit {
  @Input()
  anwAuswahlFormGroup!: FormGroup;

  @Input()
  mitarbeiterAuswahl!: Mitarbeiter[];

  @Input()
  sachbearbeiterAuswahl!: Mitarbeiter[];

  @Input()
  abrechnungsmonatAuswahl!: MitarbeiterAbrechnungsmonat[];

  constructor(
    private isInternToTextPipe: IsInternToTextPipe,
    private mitarbeiterAnzeigeNamePipe: MitarbeiterAnzeigeNamePipe
  ) {}

  ngOnInit(): void {
    this.reagiereAufAenderungMitarbeiter();
  }

  sindAbrechnungsmonateGleich(
    abrechnungsmonat1: MitarbeiterAbrechnungsmonat,
    abrechnungsmonat2: MitarbeiterAbrechnungsmonat
  ): boolean {
    return CompareUtil.sindMitarbeiterAbrechnungsmonateGleich(abrechnungsmonat1, abrechnungsmonat2);
  }

  getAbgeschlossenIcon(abrechnungsmonat: Abrechnungsmonat): string {
    return IconUtil.getAbgeschlossenIcon(abrechnungsmonat);
  }

  private reagiereAufAenderungMitarbeiter(): void {
    this.anwAuswahlFormGroup.get('mitarbeiter')?.valueChanges.subscribe((mitarbeiter: Mitarbeiter) => {
      this.patchMitarbeiter(mitarbeiter);
    });
  }

  private patchMitarbeiter(mitarbeiter: Mitarbeiter) {
    if (mitarbeiter && mitarbeiter.id) {
      this.anwAuswahlFormGroup.patchValue({
        internExtern: this.isInternToTextPipe.transform(mitarbeiter),
        geschaeftsstelle: mitarbeiter.geschaeftsstelle,
        sachbearbeiter: this.mitarbeiterAnzeigeNamePipe.transform(
          getObjektAusListeDurchId(mitarbeiter.sachbearbeiterId, this.sachbearbeiterAuswahl) as Mitarbeiter
        ),
      });
    } else {
      this.anwAuswahlFormGroup.get('internExtern')?.reset();
      this.anwAuswahlFormGroup.get('geschaeftsstelle')?.reset();
      this.anwAuswahlFormGroup.get('sachbearbeiter')?.reset();
    }
  }
}
