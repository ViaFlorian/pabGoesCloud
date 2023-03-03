import { Projekt } from '../projekt/projekt';

export interface BelegTabellendarstellung {
  id: string;
  datum: string;
  projekt: Projekt;
  projektnummer: string;
  belegartName: string;
  betrag: number;
  kilometer: number;
  arbeitsstaette: string;
  belegNr: string;
  bemerkung: string;
}
