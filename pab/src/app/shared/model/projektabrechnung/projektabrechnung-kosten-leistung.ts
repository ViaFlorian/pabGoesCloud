export interface ProjektabrechnungKostenLeistung {
  mitarbeiterId: string;
  projektzeitKosten: number;
  projektzeitLeistung: number;
  reiseKosten: number;
  reiseLeistung: number;
  sonderzeitKosten: number;
  sonderzeitLeistung: number;
  sonstigeKosten: number;
  sonstigeLeistung: number;
  fakturierfaehigeLeistung: number;
  ohneMitarbeiterBezug: boolean;
}
