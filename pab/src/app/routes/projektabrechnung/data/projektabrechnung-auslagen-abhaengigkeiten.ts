import { ValidatorFn, Validators } from '@angular/forms';
import { BelegartEnum } from '../../../shared/enum/belegart.enum';
import { ViadeeAuslagenKostenartArtenEnum } from '../../../shared/enum/viadee-auslagen-kostenart-arten.enum';
import { CustomValidator } from '../../../shared/validation/custom-validator';

export const viadeeAuslagenKostenartAbhaengigkeiten: {
  felder: {
    [key: string]: {
      required: string[];
      disabled: string[];
      value: string;
      validator: ValidatorFn | ValidatorFn[];
    };
  };
  abhaengigeFelder: string[];
  defaultFelder: string[];
} = {
  felder: {
    [ViadeeAuslagenKostenartArtenEnum.REISE]: {
      required: ['mitarbeiter'],
      disabled: [''],
      value: '',
      validator: [Validators.required, CustomValidator.objektIstNichtLeer()],
    },
    [ViadeeAuslagenKostenartArtenEnum.SONSTIGES]: {
      required: [],
      disabled: ['belegart'],
      value: BelegartEnum.SONSTIGES,
      validator: Validators.required,
    },
  },
  abhaengigeFelder: ['mitarbeiter', 'belegart', 'bemerkung'],
  defaultFelder: ['belegart'],
};

export const belegartAbhaengigkeiten: {
  felder: { [id: string]: { required: string[] } };
  abhaengigeFelder: string[];
} = {
  felder: {
    Sonstiges: {
      required: ['bemerkung'],
    },
  },
  abhaengigeFelder: ['bemerkung'],
};
