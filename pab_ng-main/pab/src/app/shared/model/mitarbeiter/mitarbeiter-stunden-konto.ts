export interface MitarbeiterStundenKonto {
  id: string;
  mitarbeiterId: string;
  wertstellung: Date;
  buchungsdatum: Date;
  anzahlStunden: number;
  lfdSaldo: number;
  buchungstypStundenId: string;
  automatisch: boolean;
  endgueltig: boolean;
  zuletztGeaendertVon: string;
  bemerkung: string;
  geaendert: boolean;
}
