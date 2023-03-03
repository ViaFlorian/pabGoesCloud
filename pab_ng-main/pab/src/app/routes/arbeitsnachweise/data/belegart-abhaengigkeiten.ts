import { BelegartEnum } from '../../../shared/enum/belegart.enum';

export const belegartAbhaengigkeiten: {
  felder: { [key: string]: { required: string[]; disabled: string[] } };
  abhaengigeFelder: string[];
} = {
  felder: {
    [BelegartEnum.PKW]: {
      required: ['kilometer', 'einsatzort'],
      disabled: ['betrag'],
    },
    [BelegartEnum.BAHN]: {
      required: ['betrag', 'einsatzort'],
      disabled: ['kilometer'],
    },
    [BelegartEnum.FLUG]: {
      required: ['betrag', 'einsatzort'],
      disabled: ['kilometer'],
    },
    [BelegartEnum.OPNV]: {
      required: ['betrag', 'einsatzort'],
      disabled: ['kilometer'],
    },
    [BelegartEnum.TAXI]: {
      required: ['betrag', 'einsatzort'],
      disabled: ['kilometer'],
    },
    [BelegartEnum.HOTEL]: {
      required: ['betrag', 'einsatzort'],
      disabled: ['kilometer'],
    },
    [BelegartEnum.PARKEN]: {
      required: ['betrag', 'einsatzort'],
      disabled: ['kilometer'],
    },
    [BelegartEnum.VERBINDUNGSENTGELT]: {
      required: ['betrag'],
      disabled: ['kilometer'],
    },
    [BelegartEnum.SONSTIGES]: {
      required: ['betrag', 'bemerkung'],
      disabled: ['kilometer'],
    },
    [BelegartEnum.JOBTICKET]: {
      required: ['betrag'],
      disabled: ['kilometer'],
    },
  },
  abhaengigeFelder: ['betrag', 'kilometer', 'einsatzort', 'bemerkung'],
};
