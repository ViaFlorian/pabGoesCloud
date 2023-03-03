export interface ProjektabrechnungSonstige {
  id: string;
  projektabrechnungId: string;
  mitarbeiterId: string;
  bemerkung: string;
  viadeeAuslagen: number;
  pauschaleKosten: number;
  pauschaleLeistung: number;
  zuletztGeaendertAm: Date;
  zuletztGeaendertVon: string;
}
