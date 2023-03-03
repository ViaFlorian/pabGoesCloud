import { Pipe, PipeTransform } from '@angular/core';
import { ObjektMitMonatUndJahr } from '../model/sonstiges/objekt-mit-monat-und-jahr';

@Pipe({
  name: 'abrechnungsmonatAnzeigeName',
})
export class AbrechnungsmonatAnzeigeNamePipe implements PipeTransform {
  transform(objektMitMonatUndJahr: ObjektMitMonatUndJahr): string {
    if (
      objektMitMonatUndJahr === undefined ||
      objektMitMonatUndJahr.monat === undefined ||
      objektMitMonatUndJahr.jahr === undefined
    ) {
      return '';
    }
    const monat: string =
      objektMitMonatUndJahr.monat <= 9 ? `0${objektMitMonatUndJahr.monat}` : objektMitMonatUndJahr.monat.toString();
    return `${objektMitMonatUndJahr.jahr}/${monat}`;
  }
}
