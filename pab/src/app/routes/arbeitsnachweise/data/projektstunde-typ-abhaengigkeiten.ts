import { ProjektstundeTypArtenEnum } from '../../../shared/enum/projektstunde-typ-arten.enum';

export const projektstundeTypAbhaengigkeiten: {
  felder: { [typ: string]: { required: string[]; disabled: string[] } };
  abhaengigeFelder: string[];
} = {
  felder: {
    [ProjektstundeTypArtenEnum.SONDER]: {
      required: ['tagVon', 'hhVon', 'mmVon', 'tagBis', 'hhBis', 'mmBis'],
      disabled: ['tagBis'],
    },
    [ProjektstundeTypArtenEnum.RUFBEREITSCHAFT]: {
      required: ['tagVon', 'hhVon', 'mmVon', 'tagBis', 'hhBis', 'mmBis'],
      disabled: [],
    },
    [ProjektstundeTypArtenEnum.TATSAECHLICHE_REISEZEIT]: {
      required: ['tagVon'],
      disabled: ['hhVon', 'mmVon', 'tagBis', 'hhBis', 'mmBis'],
    },
  },
  abhaengigeFelder: ['tagVon', 'hhVon', 'mmVon', 'tagBis', 'hhBis', 'mmBis'],
};
