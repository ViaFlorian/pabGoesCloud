import { Projekt } from '../../../shared/model/projekt/projekt';
import { Mitarbeiter } from '../../../shared/model/mitarbeiter/mitarbeiter';

export interface SonstigeProjektkostenTabellendarstellung {
  id: string;
  jahr: number;
  monat: number;
  abrechnungsmonat: string;
  istAbrechnungsmonatAbgeschlossen: boolean;
  kosten: number;
  mitarbeiter: Mitarbeiter;
  mitarbeiterName: string;
  projekt: Projekt;
  projektName: string;
  bemerkung: string;
  belegartName: string;
  viadeeAuslagenKostenartName: string;
  zuletztGeaendertVon: string;
  zuletztGeaendertAm: Date;
}
