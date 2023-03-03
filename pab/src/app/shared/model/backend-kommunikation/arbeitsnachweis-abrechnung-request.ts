import { Arbeitsnachweis } from '../arbeitsnachweis/arbeitsnachweis';
import { Projektstunde } from '../arbeitsnachweis/projektstunde';
import { Abwesenheit } from '../arbeitsnachweis/abwesenheit';
import { Beleg } from '../arbeitsnachweis/beleg';

export interface ArbeitsnachweisAbrechnungRequest {
  arbeitsnachweis: Arbeitsnachweis | undefined;
  projektstundenNormal: Projektstunde[];
  reisezeiten: Projektstunde[];
  rufbereitschaften: Projektstunde[];
  sonderarbeitszeiten: Projektstunde[];
  abwesenheiten: Abwesenheit[];
  belege: Beleg[];
}
