import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { Abrechnungsmonat } from '../../../shared/model/sonstiges/abrechnungsmonat';

export class CustomValidatorVerwaltung {
  static abrechnungsmonatIstNichtAbgeschlossen(abrechnungsmonate: Abrechnungsmonat[]): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const date: Date = new Date(control.value);
      const abrechnungsmonat: Abrechnungsmonat | undefined = abrechnungsmonate.find(
        (element) => element.jahr === date.getFullYear() && element.monat === date.getMonth()
      );
      return abrechnungsmonat && !abrechnungsmonat.abgeschlossen ? null : { abrechnungsmoantAbgeschlossen: true };
    };
  }
}
