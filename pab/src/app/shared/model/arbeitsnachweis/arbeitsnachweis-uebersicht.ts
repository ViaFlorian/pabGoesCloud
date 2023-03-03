export interface ArbeitsnachweisUebersicht {
  arbeitsnachweisId: string;
  jahr: number;
  monat: number;
  statusId: number;
  mitarbeiterId: string;
  sachbearbeiterId: string;
  summeProjektstunden: number;
  summeSpesen: number;
  summeBelege: number;
}
