export interface SkontoTabellendarstellung {
  id: string;
  referenzJahr: number;
  referenzMonat: number;
  referenzmonat: string;
  wertstellung: Date;
  skontoNettoBetrag: number;
  umsatzsteuer: number;
  skontoBruttoBetrag: number;
  bemerkung: string;
}
