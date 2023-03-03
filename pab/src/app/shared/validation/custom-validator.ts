import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { ObjektMitId } from '../model/sonstiges/objekt-mit-id';
import { convertDeStringZuUsNummer } from '../util/nummer-converter.util';

export class CustomValidator {
  static objektIstNichtLeer(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const objektMitId: ObjektMitId = control.value;
      return objektMitId && objektMitId.id ? null : { objektLeer: true };
    };
  }

  static valueIstObjektOderLeererString(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      return typeof value !== 'string' || value === '' ? null : { objektNichtLeererString: true };
    };
  }

  static minFuerString(min: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value: string = control.value;
      const nummer: number = convertDeStringZuUsNummer(value);
      return nummer >= min ? null : { min: true };
    };
  }

  static maxFuerString(max: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value: string = control.value;
      const nummer: number = convertDeStringZuUsNummer(value);
      return nummer <= max ? null : { max: true };
    };
  }
}
