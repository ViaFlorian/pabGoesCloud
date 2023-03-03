import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { Abrechnungsmonat } from '../../../../shared/model/sonstiges/abrechnungsmonat';
import * as CompareUtil from '../../../../shared/util/compare.util';
import * as IconUtil from '../../../../shared/util/icon.util';

@Component({
  selector: 'pab-projektabrechnung-auslagen-filter',
  templateUrl: './projektabrechnung-auslagen-filter.component.html',
  styleUrls: ['./projektabrechnung-auslagen-filter.component.scss'],
})
export class ProjektabrechnungAuslagenFilterComponent {
  @Input()
  auslagenFilterFormGroup!: FormGroup;

  @Input()
  abrechnungsmonatAuswahl!: Abrechnungsmonat[];

  @Input()
  projektAuswahl!: Projekt[];

  @Input()
  mitarbeiterAuswahl!: Mitarbeiter[];

  constructor() {}

  leereAuslagenFilterForm() {
    this.auslagenFilterFormGroup.reset();
  }

  sindAbrechnungsmonateGleich(abrechnungsmonat1: Abrechnungsmonat, abrechnungsmonat2: Abrechnungsmonat): boolean {
    return CompareUtil.sindAbrechnungsmonateGleich(abrechnungsmonat1, abrechnungsmonat2);
  }

  getAbgeschlossenIcon(abrechnungsmonat: Abrechnungsmonat): string {
    return IconUtil.getAbgeschlossenIcon(abrechnungsmonat);
  }
}
