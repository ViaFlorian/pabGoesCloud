import { Pipe, PipeTransform } from '@angular/core';
import { getObjektAusListeDurchId } from '../util/objekt-in-array-finden.util';
import { BuchungstypStunden } from '../model/konstanten/buchungstyp-stunden';

@Pipe({
  name: 'buchungstypIdStundenkontoZuText',
})
export class BuchungstypIdStundenkontoZuTextPipe implements PipeTransform {
  transform(buchungstypId: string, buchungstypStundenkonto: BuchungstypStunden[]): string {
    const objektAusListeDurchId = getObjektAusListeDurchId(
      buchungstypId,
      buchungstypStundenkonto
    ) as BuchungstypStunden;
    if (!objektAusListeDurchId) {
      return '';
    }
    return objektAusListeDurchId.bezeichnung;
  }
}
