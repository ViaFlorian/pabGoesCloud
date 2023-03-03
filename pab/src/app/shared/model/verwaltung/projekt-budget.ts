export interface ProjektBudget {
  id: string;
  projektId: string;
  wertstellung: Date;
  budgetBetrag: number;
  bemerkung: string;
  zuletztGeaendertAm: Date;
  zuletztGeaendertVon: string;
}
