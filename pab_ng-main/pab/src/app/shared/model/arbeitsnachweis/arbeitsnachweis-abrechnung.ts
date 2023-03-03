export interface ArbeitsnachweisAbrechnung {
  summeIstStunden: number;
  auszahlung: number;
  sollstunden: number;
  vortrag: number;
  uebertragVorAuszahlung: number;
  uebertragNachAuszahlung: number;
  berechneteSollstunden: number;
  aktuellerStellenfaktor: number;
  ueberAcht: number;
  anAb: number;
  spesenGanztaegig: number;
  fruehstueck: number;
  mittagessen: number;
  abendessen: number;
  zwischenSechsUndZehn: number;
  ueberZehn: number;
  zuschlaegeGanztaegig: number;
  sonderarbeitszeit: number;
  davonWerktag: number;
  davonSamstag: number;
  davonSonntagFeiertag: number;
  rufbereitschaft: number;
  summeBelege: number;
  smartphone: string;
  firmenwagen: string;
  kilometerpauschaleFirmenwagen: number;
  zuschlagSmartphone: number;
  verbindungsentgelt: number;
  jobticket: number;
  warnung?: string;
}
