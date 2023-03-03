export interface ProjektabrechnungProjektzeit {
  id: string;
  projektabrechnungId: string;
  mitarbeiterId: string;
  stundenLautArbeitsnachweis: number;
  stundenLautArbeitsnachweisOriginal: number;
  laufendeNummer: number;
  stundensatz: number;
  stundensatzVormonat: number;
  stundensatzVertrag: number;
  kostensatzVertrag: number;
  kostensatz: number;
  kostensatzVormonat: number;
  zuletztGeaendertAm: Date;
  zuletztGeaendertVon: string;
}
