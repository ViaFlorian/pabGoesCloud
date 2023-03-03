export interface MitarbeiterUrlaubskonto {
  id: string;
  mitarbeiterId: string;
  wertstellung: Date;
  buchungsdatum: Date;
  anzahlTage: number;
  lfdSaldo: number;
  buchungstypUrlaubId: string;
  automatisch: boolean;
  endgueltig: boolean;
  zuletztGeaendertVon: string;
  bemerkung: string;
  geaendert: boolean;
}
