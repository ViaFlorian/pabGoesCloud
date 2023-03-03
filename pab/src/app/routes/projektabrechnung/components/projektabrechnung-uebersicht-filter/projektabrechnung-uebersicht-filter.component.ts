import { Component, Input } from '@angular/core';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { FormGroup } from '@angular/forms';
import { BearbeitungsstatusEnum } from '../../../../shared/enum/bearbeitungsstatus.enum';
import { OperatorEnum } from '../../../../shared/enum/operator.enum';
import { MatDatepicker } from '@angular/material/datepicker';
import { entferneUnerlaubteZeichenAusFormGroupKomponente } from '../../../../shared/util/regex-control.util';
import { AktivInaktivEnum } from '../../../../shared/enum/aktiv-inaktiv.enum';
import { BuchungsstatusProjektEnum } from '../../../../shared/enum/buchungsstatus-projekt.enum';
import { ProjekttypEnum } from '../../../../shared/enum/projekttyp.enum';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { Organisationseinheit } from '../../../../shared/model/organisationseinheit/organisationseinheit';
import { Kunde } from '../../../../shared/model/kunde/kunde';
import { Observable } from 'rxjs';
import { OrganisationseinheitService } from '../../../../shared/service/organisationseinheit.service';
import * as CompareUtil from '../../../../shared/util/compare.util';

@Component({
  selector: 'pab-projektabrechnung-uebersicht-filter',
  templateUrl: './projektabrechnung-uebersicht-filter.component.html',
  styleUrls: ['./projektabrechnung-uebersicht-filter.component.scss'],
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
export class ProjektabrechnungUebersichtFilterComponent {
  @Input()
  formGroup!: FormGroup;

  @Input()
  mitarbeiterAuswahl!: Observable<Mitarbeiter[]>;

  @Input()
  sachbearbeiterAuswahl!: Mitarbeiter[];

  @Input()
  projektAuswahl!: Projekt[];

  @Input()
  kundeAuswahl!: Kunde[];

  @Input()
  organisationseinheitAuswahl!: Organisationseinheit[];

  eBearbeitungsstatusEnum = [
    BearbeitungsstatusEnum.ALLE,
    BearbeitungsstatusEnum.ERFASST,
    BearbeitungsstatusEnum.ABGERECHNET,
    BearbeitungsstatusEnum.ABGESCHLOSSEN,
  ];
  eAktivInaktivEnum = AktivInaktivEnum;
  eBuchungsstatusProjektEnum = BuchungsstatusProjektEnum;
  eProjekttypEnum = ProjekttypEnum;
  eOperatorEnum = OperatorEnum;

  constructor(private organisationseinheitService: OrganisationseinheitService) {}

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

  sindOrganisationseinheitenGleich(
    organisationseinheit1: Organisationseinheit,
    organisationseinheit2: Organisationseinheit
  ): boolean {
    return CompareUtil.sindOrganisationseinheitenGleich(organisationseinheit1, organisationseinheit2);
  }

  sindMitarbeiterGleich(mitarbeiter1: Mitarbeiter, mitarbeiter2: Mitarbeiter): boolean {
    return CompareUtil.sindMitarbeiterGleich(mitarbeiter1, mitarbeiter2);
  }

  private erzeugeDatumMitMonatUndJahr(value: Date) {
    return new Date(value.getFullYear(), value.getMonth(), 1);
  }
}
