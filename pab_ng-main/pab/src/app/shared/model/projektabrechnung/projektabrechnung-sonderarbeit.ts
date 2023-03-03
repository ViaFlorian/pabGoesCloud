export interface ProjektabrechnungSonderarbeit {
  id: string;
  projektabrechnungId: string;
  mitarbeiterId: string;
  rufbereitschaftKostenAnzahlStunden: number;
  rufbereitschaftKostensatz: number;
  rufbereitschaftLeistungAnzahlStunden: number;
  rufbereitschaftStundensatz: number;
  rufbereitschaftLeistungPauschale: number;
  sonderarbeitAnzahlStunden50: number;
  sonderarbeitAnzahlStunden100: number;
  sonderarbeitKostensatz: number;
  sonderarbeitPauschale: number;
  sonderarbeitLeistungPauschale: number;
  zuletztGeaendertAm: Date;
  zuletztGeaendertVon: string;
}
