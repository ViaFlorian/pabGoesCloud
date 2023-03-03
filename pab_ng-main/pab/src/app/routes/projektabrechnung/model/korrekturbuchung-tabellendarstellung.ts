export interface KorrekturbuchungTabellendarstellung {
  id: string;
  typ: string;
  abrechnungsmonat: string;
  mitarbeiterName: string;
  kostenartBezeichnung: string;
  anzahlStundenKosten: number;
  betragKostensatz: number;
  kosten: number;
  anzahlStundenLeistung: number;
  betragStundensatz: number;
  leistung: number;
  bemerkung: string;
  zuletztGeaendertAm: Date;
  zuletztGeaendertVon: string;
}
