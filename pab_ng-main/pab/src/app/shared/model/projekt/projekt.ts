export interface Projekt {
  id: string;
  projektnummer: string;
  bezeichnung: string;
  kundeId: string;
  organisationseinheitId: string;
  sachbearbeiterId: string;
  verantwortlicherMitarbeiterId: string;
  projekttyp: string;
  istAktiv: boolean;
  geschaeftsstelle: string;
}
