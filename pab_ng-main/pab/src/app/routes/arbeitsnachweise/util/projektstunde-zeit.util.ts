import { ProjektstundeZeitBackend } from '../../../shared/model/backend-kommunikation/projektstunde-zeit-backend';
import { Projektstunde } from '../../../shared/model/arbeitsnachweis/projektstunde';
import { ProjektstundeTypArtenEnum } from '../../../shared/enum/projektstunde-typ-arten.enum';
import { format } from 'date-fns';
import { ProjektstundeTyp } from '../../../shared/model/konstanten/projektstunde-typ';

export function erstelleDatumVonAusProjektstundeBackendZeit(
  projektstunde: Projektstunde,
  projektstundeTyp: ProjektstundeTyp
): Date {
  const projektstundeBackendZeit = projektstunde as unknown as ProjektstundeZeitBackend;

  switch (projektstundeTyp.textKurz) {
    case ProjektstundeTypArtenEnum.NORMAL:
    case ProjektstundeTypArtenEnum.TATSAECHLICHE_REISEZEIT:
    case ProjektstundeTypArtenEnum.ANGERECHNETE_REISEZEIT:
      return new Date(projektstundeBackendZeit.tagVon);
    case ProjektstundeTypArtenEnum.SONDER:
    case ProjektstundeTypArtenEnum.RUFBEREITSCHAFT:
      return new Date(`${projektstundeBackendZeit.tagVon}T${projektstundeBackendZeit.uhrzeitVon}`);
    default:
      console.error('ProjektstundenTyp unbekannt');
      return new Date('invalid');
  }
}

export function erstelleDatumBisAusProjektstundeBackendZeit(
  projektstunde: Projektstunde,
  projektstundeTyp: ProjektstundeTyp
): Date | null {
  const projektstundeBackendZeit = projektstunde as unknown as ProjektstundeZeitBackend;

  switch (projektstundeTyp.textKurz) {
    case ProjektstundeTypArtenEnum.NORMAL:
    case ProjektstundeTypArtenEnum.TATSAECHLICHE_REISEZEIT:
    case ProjektstundeTypArtenEnum.ANGERECHNETE_REISEZEIT:
      return null;
    case ProjektstundeTypArtenEnum.SONDER:
      return new Date(`${projektstundeBackendZeit.tagVon}T${projektstundeBackendZeit.uhrzeitBis}`);
    case ProjektstundeTypArtenEnum.RUFBEREITSCHAFT:
      return new Date(`${projektstundeBackendZeit.tagBis}T${projektstundeBackendZeit.uhrzeitBis}`);
    default:
      console.error('ProjektstundenTyp unbekannt');
      return null;
  }
}

export function erstelleUhrzeitVonAusProjektstunde(
  projektstunde: Projektstunde,
  projektstundeTyp: ProjektstundeTyp
): string {
  switch (projektstundeTyp.textKurz) {
    case ProjektstundeTypArtenEnum.NORMAL:
    case ProjektstundeTypArtenEnum.TATSAECHLICHE_REISEZEIT:
    case ProjektstundeTypArtenEnum.ANGERECHNETE_REISEZEIT:
      return '';
    case ProjektstundeTypArtenEnum.SONDER:
    case ProjektstundeTypArtenEnum.RUFBEREITSCHAFT:
      return extrahiereUhrzeitStringAusDate(projektstunde.datumVon);
    default:
      console.error('ProjektstundenTyp unbekannt');
      return '';
  }
}

export function erstelleTagBisAusProjektstundeNachTyp(
  projektstunde: Projektstunde,
  projektstundeTyp: ProjektstundeTyp
): string {
  switch (projektstundeTyp.textKurz) {
    case ProjektstundeTypArtenEnum.NORMAL:
    case ProjektstundeTypArtenEnum.TATSAECHLICHE_REISEZEIT:
    case ProjektstundeTypArtenEnum.ANGERECHNETE_REISEZEIT:
    case ProjektstundeTypArtenEnum.SONDER:
      return '';
    case ProjektstundeTypArtenEnum.RUFBEREITSCHAFT:
      if (!projektstunde.datumBis) {
        console.error('Datum bis benötigt aber leer');
        return '';
      }
      return format(projektstunde.datumBis, 'yyyy-MM-dd');

    default:
      console.error('ProjektstundenTyp unbekannt');
      return '';
  }
}

export function erstelleUhrzeitBisAusProjektstunde(
  projektstunde: Projektstunde,
  projektstundeTyp: ProjektstundeTyp
): string {
  switch (projektstundeTyp.textKurz) {
    case ProjektstundeTypArtenEnum.NORMAL:
    case ProjektstundeTypArtenEnum.TATSAECHLICHE_REISEZEIT:
    case ProjektstundeTypArtenEnum.ANGERECHNETE_REISEZEIT:
      return '';
    case ProjektstundeTypArtenEnum.SONDER:
    case ProjektstundeTypArtenEnum.RUFBEREITSCHAFT:
      if (!projektstunde.datumBis) {
        console.error('Datum bis benötigt aber leer');
        return '';
      }
      return extrahiereUhrzeitStringAusDate(projektstunde.datumBis);
    default:
      console.error('ProjektstundenTyp unbekannt');
      return '';
  }
}

export function extrahiereUhrzeitStringAusDate(time: Date): string {
  const hour = time.getHours() < 10 ? `0${time.getHours()}` : time.getHours().toString();
  const minute = time.getMinutes() < 10 ? `0${time.getMinutes()}` : time.getMinutes().toString();
  return `${hour}:${minute}:00`;
}
