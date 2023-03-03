export interface Skonto {
  id: string;
  projektId: string;
  wertstellung: Date;
  referenzMonat: number;
  referenzJahr: number;
  skontoNettoBetrag: number;
  umsatzsteuer: number;
  bemerkung: string;
  zuletztGeaendertAm: Date;
  zuletztGeaendertVon: string;
}
