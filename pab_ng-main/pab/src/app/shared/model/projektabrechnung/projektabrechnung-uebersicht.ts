export interface ProjektabrechnungUebersicht {
  projektabrechnungId: string;
  jahr: number;
  monat: number;
  statusId: number;
  projektId: string;
  kundeId: string;
  organisationseinheitId: string;
  sachbearbeiterId: string;
  anzahlMitarbeiter: number;
  anzahlKorrekturbuchungen: number;
  leistung: number;
  kosten: number;
}
