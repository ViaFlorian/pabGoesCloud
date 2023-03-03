export interface FakturTabellendarstellung {
  id: string;
  referenzJahr: number;
  referenzMonat: number;
  referenzmonat: string;
  rechnungsdatum: Date;
  rechnungsnummer: string;
  abweichenderRechnungsempfaengerKundeId: string;
  abwRechnungsempfaenger: string;
  debitorennummer: string;
  betragNetto: number;
  nichtBudgetRelevant: number;
  umsatzsteuer: number;
  betragBrutto: number;
  bemerkung: string;
}
