export interface LohnartberechnungLog {
  id: string;
  arbeitsnachweisId: string;
  konto: string;
  datum: Date;
  meldung: string;
  wert: number;
  einheit: string;
}
