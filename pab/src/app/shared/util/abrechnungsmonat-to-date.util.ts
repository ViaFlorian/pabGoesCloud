import { Abrechnungsmonat } from '../model/sonstiges/abrechnungsmonat';
import { MitarbeiterAbrechnungsmonat } from '../model/mitarbeiter/mitarbeiter-abrechnungsmonat';

export function konvertiereAbrechnungsmonatToJsDate(
  abrechnungsmonat: Abrechnungsmonat | MitarbeiterAbrechnungsmonat,
  tag?: number | undefined,
  stunden?: number,
  minuten?: number,
  sekunden?: number
): Date {
  const day = tag ? tag : 1;
  const hh24 = stunden ? stunden : 0;
  const mm = minuten ? minuten : 0;
  const ss = sekunden ? sekunden : 0;
  return new Date(abrechnungsmonat.jahr, abrechnungsmonat.monat - 1, day, hh24, mm, ss);
}
