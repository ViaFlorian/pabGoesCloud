export interface ProjektabrechnungBerechneteLeistung {
  id: string;
  projektabrechnungId: string;
  mitarbeiterId: string;
  leistung: number;
  zuletztGeaendertAm: Date;
  zuletztGeaendertVon: string;
}
