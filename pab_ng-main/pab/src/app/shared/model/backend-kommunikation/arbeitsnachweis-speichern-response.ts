import { Arbeitsnachweis } from '../arbeitsnachweis/arbeitsnachweis';

export interface ArbeitsnachweisSpeichernResponse {
  zurueckgesetzteProjekte: number[];
  meldungen: string[];

  arbeitsnachweis: Arbeitsnachweis;
}
