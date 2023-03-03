import { Mitarbeiter } from '../../../shared/model/mitarbeiter/mitarbeiter';

export interface ArbeitsnachweisUebersichtTabellendarstellung {
  arbeitsnachweisId: string;
  jahr: number;
  monat: number;
  abrechnungsmonat: string;
  statusId: number;
  mitarbeiter: Mitarbeiter;
  mitarbeiterName: string;
  internExtern: string;
  sachbearbeiter: Mitarbeiter;
  sachbearbeiterName: string;
  summeProjektstunden: number;
  summeSpesen: number;
  summeBelege: number;
}
