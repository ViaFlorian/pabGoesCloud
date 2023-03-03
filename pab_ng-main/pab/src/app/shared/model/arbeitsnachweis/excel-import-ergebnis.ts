import { Arbeitsnachweis } from './arbeitsnachweis';
import { Projektstunde } from './projektstunde';
import { Beleg } from './beleg';
import { Abwesenheit } from './abwesenheit';
import { Fehlerlog } from './fehlerlog';

export interface ExcelImportErgebnis {
  arbeitsnachweis: Arbeitsnachweis;
  importierteProjektstunden: Projektstunde[];
  importierteRufbereitschaft: Projektstunde[];
  importierteSonderarbeitszeiten: Projektstunde[];
  importierteReisezeiten: Projektstunde[];
  importierteBelege: Beleg[];
  importierteAbwesenheiten: Abwesenheit[];
  fehlerlog: Fehlerlog[];
}
