export interface Beleg {
  id: string;
  datum: Date;
  projektId: string;
  belegartId: string;
  betrag: number;
  kilometer: number;
  arbeitsstaette: string;
  belegNr: string;
  bemerkung: string;
}
