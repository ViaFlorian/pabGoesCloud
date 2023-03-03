export interface Mitarbeiter {
  id: string;
  personalNr: number;
  nachname: string;
  vorname: string;
  intern: boolean;
  geschaeftsstelle: string;
  istAktiv: boolean;
  mitarbeiterTypId: number;
  sachbearbeiterId: string;
  email: string;
}
