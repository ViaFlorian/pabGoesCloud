import { Arbeitsnachweis } from '../arbeitsnachweis/arbeitsnachweis';
import { Projektstunde } from '../arbeitsnachweis/projektstunde';
import { Abwesenheit } from '../arbeitsnachweis/abwesenheit';
import { Beleg } from '../arbeitsnachweis/beleg';

export interface ArbeitsnachweisSpeichernRequest {
  arbeitsnachweis: Arbeitsnachweis;
  mitarbeiterId: string;
  neueProjektstunden: Projektstunde[];
  aktualisierteProjektstunden: Projektstunde[];
  geloeschteProjektstunden: Projektstunde[];
  neueAbwesenheiten: Abwesenheit[];
  aktualisierteAbwesenheiten: Abwesenheit[];
  geloschteAbwesenheiten: Abwesenheit[];
  neueBelege: Beleg[];
  aktualisierteBelege: Beleg[];
  geloeschteBelege: Beleg[];
}
