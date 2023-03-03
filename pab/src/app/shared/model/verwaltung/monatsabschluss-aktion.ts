export interface MonatsabschlussAktion {
  jahr: number;
  monat: number;
  titel: string;
  istErledigt: boolean;
  durchgefuehrtAm: Date;
  mitarbeiterID: string;
  rang: number;
  lodasExport: number;
}
