import { Projekt } from '../../../shared/model/projekt/projekt';

export interface ProjektstundeTabellendarstellung {
  id: string;
  tagVon: string;
  uhrzeitVon: string;
  tagBis: string;
  uhrzeitBis: string;
  projekt: Projekt;
  projektnummer: string;
  anzahlStunden: number;
  nichtFakturierfaehig: boolean;
  bemerkung: string;
  projektstundeTypId: string;
}
