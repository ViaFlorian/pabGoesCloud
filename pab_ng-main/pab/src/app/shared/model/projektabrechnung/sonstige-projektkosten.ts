export interface SonstigeProjektkosten {
  id: string;
  jahr: number;
  monat: number;
  kosten: number;
  mitarbeiterId: string;
  projektId: string;
  bemerkung: string;
  belegartId: string;
  viadeeAuslagenKostenartId: string;
  zuletztGeaendertVon: string;
  zuletztGeaendertAm: Date;
}
