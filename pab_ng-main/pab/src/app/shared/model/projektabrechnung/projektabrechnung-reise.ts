export interface ProjektabrechnungReise {
  id: string;
  projektabrechnungId: string;
  mitarbeiterId: string;
  angerechneteReisezeit: number;
  kostensatz: number;
  belegeLautArbeitsnachweisKosten: number;
  belegeViadeeKosten: number;
  spesenKosten: number;
  zuschlaegeKosten: number;
  tatsaechlicheReisezeit: number;
  tatsaechlicheReisezeitInformatorisch: number;
  stundensatz: number;
  belegeLautArbeitsnachweisLeistung: number;
  belegeViadeeLeistung: number;
  spesenLeistung: number;
  zuschlaegeLeistung: number;
  pauschaleAnzahl: number;
  pauschaleProTag: number;
  zuletztGeaendertAm: Date;
  zuletztGeaendertVon: string;
}
