import { ProjektabrechnungKorrekturbuchung } from './projektabrechnung-korrekturbuchung';

export interface ProjektabrechnungKorrekturbuchungVorgang {
  korrekturbuchung: ProjektabrechnungKorrekturbuchung;
  gegenbuchung?: ProjektabrechnungKorrekturbuchung;
}
