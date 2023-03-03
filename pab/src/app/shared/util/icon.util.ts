import { Abrechnungsmonat } from '../model/sonstiges/abrechnungsmonat';
import { ProjektAbrechnungsmonat } from '../model/projektabrechnung/projekt-abrechnungsmonat';
import { MitarbeiterAbrechnungsmonat } from '../model/mitarbeiter/mitarbeiter-abrechnungsmonat';

export function getAbgeschlossenIconMitBoolean(istAbgeschlossen: boolean): string {
  return istAbgeschlossen ? 'lock' : 'lock_open';
}

export function getAbgeschlossenIcon(
  abrechnungsmonat: Abrechnungsmonat | MitarbeiterAbrechnungsmonat | ProjektAbrechnungsmonat
): string {
  if (!abrechnungsmonat || !abrechnungsmonat.jahr) {
    return '';
  }
  return getAbgeschlossenIconMitBoolean(abrechnungsmonat.abgeschlossen);
}
