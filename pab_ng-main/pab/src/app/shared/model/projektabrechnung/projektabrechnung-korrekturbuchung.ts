export interface ProjektabrechnungKorrekturbuchung {
  id: string;
  projektabrechnungId: string;
  gegenbuchungID: string;
  istKorrekturbuchung: boolean;
  jahr: number;
  monat: number;
  referenzJahr: number;
  referenzMonat: number;
  mitarbeiterId: string;
  kostenartId: string;
  projektId: string;
  anzahlStundenKosten: number;
  betragKostensatz: number;
  anzahlStundenLeistung: number;
  betragStundensatz: number;
  bemerkung: string;
  stundendifferenzGegenbuchung: number;
  leistung: number;
  zuletztGeaendertAm: Date;
  zuletztGeaendertVon: string;
}
