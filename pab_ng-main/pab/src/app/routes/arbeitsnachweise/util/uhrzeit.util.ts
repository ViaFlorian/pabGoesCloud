import { Abrechnungsmonat } from '../../../shared/model/sonstiges/abrechnungsmonat';
import { konvertiereAbrechnungsmonatToJsDate } from '../../../shared/util/abrechnungsmonat-to-date.util';
import { MitarbeiterAbrechnungsmonat } from '../../../shared/model/mitarbeiter/mitarbeiter-abrechnungsmonat';

export function erzeugeDatumMit2400Fall(
  abrechnungsmonat: Abrechnungsmonat | MitarbeiterAbrechnungsmonat,
  tag: number,
  stunde: number,
  minute: number
): Date {
  const stundeKorrigiert = stunde === 24 && minute === 0 ? 23 : stunde;
  const minuteKorrigiert = stunde === 24 && minute === 0 ? 59 : minute;
  const sekunde = stunde === 24 && minute === 0 ? 59 : 0;

  return konvertiereAbrechnungsmonatToJsDate(abrechnungsmonat, tag, stundeKorrigiert, minuteKorrigiert, sekunde);
}

export function transformiereZu2400FallFuerStunde(uhrzeit: Date | null) {
  if (!uhrzeit) {
    return '';
  }
  if (uhrzeit.getHours() === 23 && uhrzeit.getMinutes() === 59 && uhrzeit.getSeconds() === 59) {
    return 24;
  } else {
    return uhrzeit.getHours();
  }
}

export function transformiereZu2400FallFuerMinuten(uhrzeit: Date | null) {
  if (!uhrzeit) {
    return '';
  }
  if (uhrzeit.getHours() === 23 && uhrzeit.getMinutes() === 59 && uhrzeit.getSeconds() === 59) {
    return 0;
  } else {
    return uhrzeit.getMinutes();
  }
}
