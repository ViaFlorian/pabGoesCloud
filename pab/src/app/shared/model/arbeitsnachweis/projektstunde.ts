export interface Projektstunde {
  id: string;
  datumVon: Date;
  datumBis: Date | null;
  projektId: string;
  projektstundeTypId: string;
  anzahlStunden: number;
  nichtFakturierfaehig: boolean;
  bemerkung: string;
}
