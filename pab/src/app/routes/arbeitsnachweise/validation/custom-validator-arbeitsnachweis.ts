import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { convertDeStringZuUsNummer } from '../../../shared/util/nummer-converter.util';
import { MitarbeiterAbrechnungsmonat } from '../../../shared/model/mitarbeiter/mitarbeiter-abrechnungsmonat';
import { isValid } from 'date-fns';
import { ProjektstundeTypArtenEnum } from '../../../shared/enum/projektstunde-typ-arten.enum';
import { ProjektstundeTyp } from '../../../shared/model/konstanten/projektstunde-typ';

export class CustomValidatorArbeitsnachweis {
  static tagIstInMonat(formGroup: FormGroup): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const abrechnungsmonat: MitarbeiterAbrechnungsmonat = formGroup.get('abrechnungsmonat')!.value;
      const jahr: number = abrechnungsmonat.jahr;
      const monat: number = abrechnungsmonat.monat;
      const tag = parseInt(control.value);

      const datum: Date = new Date(jahr, monat, tag);
      return isValid(datum) ? null : { dayInMoth: true };
    };
  }

  static zeitpunktBisIstNachVon(anwAuswahlFormGroup: FormGroup): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const abrechnungsmonat: MitarbeiterAbrechnungsmonat = anwAuswahlFormGroup.get('abrechnungsmonat')!.value;
      const jahr: number = abrechnungsmonat.jahr;
      const monat: number = abrechnungsmonat.monat;

      const tagVon = control.getRawValue().tagVon;
      const tagBis = control.get('tagBis')?.enabled ? control.getRawValue().tagBis : tagVon;

      if (tagBis === '' || tagVon === '') {
        return null;
      }

      const hhVonRaw = control.getRawValue().hhVon;
      const mmVonRaw = control.getRawValue().mmVon;
      const hhBisRaw = control.getRawValue().hhBis;
      const mmBisRaw = control.getRawValue().mmBis;

      const zeitAngabenVollstaendig = !(hhVonRaw === '' || mmVonRaw === '' || hhBisRaw === '' || mmBisRaw === '');

      const hhVon = hhVonRaw !== '' ? parseInt(hhVonRaw) : 0;
      const mmVon = mmVonRaw !== '' ? parseInt(mmVonRaw) : 0;
      const hhBis = hhBisRaw !== '' ? parseInt(hhBisRaw) : 0;
      const mmBis = mmBisRaw !== '' ? parseInt(mmBisRaw) : 0;

      const dateBis = new Date(jahr, monat, parseInt(tagBis), hhBis, mmBis);
      const dateVon = new Date(jahr, monat, parseInt(tagVon), hhVon, mmVon);

      if (zeitAngabenVollstaendig) {
        if (dateVon.getDay() === dateBis.getDay() && dateVon >= dateBis) {
          return { zeitVonNachZeitBis: true };
        }
        if ((hhBis === 24 && mmBis > 0) || (hhVon === 24 && mmVon > 0)) {
          return { zeitUeber2400: true };
        }
      }
      if (dateVon > dateBis) {
        return { tagVonNachTagBis: true };
      }

      return null;
    };
  }

  static sonderzeitenAbhanegigesMaxNichtUeberschritten(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const projektstundeTyp: ProjektstundeTyp = control.parent?.getRawValue().projektstundeTyp;
      if (!projektstundeTyp || projektstundeTyp.textKurz !== ProjektstundeTypArtenEnum.TATSAECHLICHE_REISEZEIT) {
        return null;
      }
      const stunden = convertDeStringZuUsNummer(control.value);
      return stunden <= 24 ? null : { stundenUeberMax: true };
    };
  }
}
