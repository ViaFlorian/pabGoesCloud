export interface Faktur {
  id: string;
  projektId: string;
  referenzMonat: number;
  referenzJahr: number;
  rechnungsdatum: Date;
  betragNetto: number;
  rechnungsnummer: string;
  nichtBudgetRelevant: number;
  abweichenderRechnungsempfaengerKundeId: string;
  umsatzsteuer: number;
  bemerkung: string;
  zuletztGeaendertAm: Date;
  zuletztGeaendertVon: string;
}
