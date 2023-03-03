import { Component, Input } from '@angular/core';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { MatDatepicker } from '@angular/material/datepicker';
import { FormGroup } from '@angular/forms';
import { BearbeitungsstatusEnum } from '../../../../shared/enum/bearbeitungsstatus.enum';
import { InternExternEnum } from '../../../../shared/enum/intern-extern.enum';
import { OperatorEnum } from '../../../../shared/enum/operator.enum';
import { OffenVorhandenEnum } from '../../../../shared/enum/offen-vorhanden.enum';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { entferneUnerlaubteZeichenAusFormGroupKomponente } from '../../../../shared/util/regex-control.util';
import { Observable } from 'rxjs';
import * as CompareUtil from '../../../../shared/util/compare.util';

@Component({
  selector: 'pab-arbeitsnachweis-uebersicht-filter',
  templateUrl: './arbeitsnachweis-uebersicht-filter.component.html',
  styleUrls: ['./arbeitsnachweis-uebersicht-filter.component.scss'],
  providers: [
    {
      provide: MAT_DATE_FORMATS,
      useValue: {
        parse: {
          dateInput: 'MM.yy',
        },
        display: {
          dateInput: 'MM.yy',
          monthYearLabel: 'MMM y',
          dateA11yLabel: 'LL',
          monthYearA11yLabel: 'MMMM y',
        },
      },
    },
  ],
})
export class ArbeitsnachweisUebersichtFilterComponent {
  @Input()
  formGroup!: FormGroup;

  @Input()
  mitarbeiterAuswahl!: Observable<Mitarbeiter[]>;

  @Input()
  sachbearbeiterAuswahl!: Mitarbeiter[];

  eOffenVorhandenEnum = OffenVorhandenEnum;
  eBearbeitungsstatusEnum = [
    BearbeitungsstatusEnum.ALLE,
    BearbeitungsstatusEnum.ERFASST,
    BearbeitungsstatusEnum.ABGERECHNET,
    BearbeitungsstatusEnum.ABGESCHLOSSEN,
  ];
  eInternExternEnum = InternExternEnum;
  eOperatorEnum = OperatorEnum;

  setAbrechnungsmonatAbWann(value: Date, datepicker: MatDatepicker<Date>) {
    const normalizedDatum = this.erzeugeDatumMitMonatUndJahr(value);
    this.formGroup.get('abrechnungsmonatAbWann')?.setValue(normalizedDatum);
    datepicker.close();
  }

  setAbrechnungsmonatBisWann(value: Date, datepicker: MatDatepicker<Date>) {
    const normalizedDatum = this.erzeugeDatumMitMonatUndJahr(value);
    this.formGroup.get('abrechnungsmonatBisWann')?.setValue(normalizedDatum);
    datepicker.close();
  }

  onSetzeFilterZurueck(identifier: string): void {
    this.formGroup.get(identifier)?.setValue('');
  }

  onLeereFilter(): void {
    this.formGroup.reset();
  }

  entferneUnerlaubteZeichen(komponentenName: string, erlaubteExp: string) {
    entferneUnerlaubteZeichenAusFormGroupKomponente(this.formGroup, komponentenName, erlaubteExp);
  }

  sindMitarbeiterGleich(mitarbeiter1: Mitarbeiter, mitarbeiter2: Mitarbeiter): boolean {
    return CompareUtil.sindMitarbeiterGleich(mitarbeiter1, mitarbeiter2);
  }

  private erzeugeDatumMitMonatUndJahr(value: Date) {
    return new Date(value.getFullYear(), value.getMonth(), 1);
  }
}
