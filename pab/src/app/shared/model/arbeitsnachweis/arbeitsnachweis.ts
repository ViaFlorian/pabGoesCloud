import { DreiMonatsRegel } from './drei-monats-regel';
import { Fehlerlog } from './fehlerlog';

export interface Arbeitsnachweis {
  id: string;
  jahr: number;
  monat: number;
  statusId: number;
  mitarbeiterId: string;
  stellenfaktor: string;
  firmenwagen: string;
  smartphoneEigen: boolean | null;
  uebertrag: number;
  sollstunden: number;
  auszahlung: number;
  dreiMonatsRegeln: DreiMonatsRegel[] | null;
  fehlerlog: Fehlerlog[];
}
