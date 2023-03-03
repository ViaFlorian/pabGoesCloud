export interface Fehlerlog {
  id: string;
  fehlerklasse: string;
  blatt: string;
  zelle: string;
  fehlertext: string;
  anzahl: number;
  arbeitsnachweisId: string;
  projektstundeTypId: string;
  durchgefuehrtVon: string;
}
