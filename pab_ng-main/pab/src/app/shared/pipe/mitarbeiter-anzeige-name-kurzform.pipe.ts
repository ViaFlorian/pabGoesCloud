import { Pipe, PipeTransform } from '@angular/core';
import { Mitarbeiter } from '../model/mitarbeiter/mitarbeiter';

@Pipe({
  name: 'mitarbeiterAnzeigeNameKurzform',
})
export class MitarbeiterAnzeigeNameKurzformPipe implements PipeTransform {
  transform(mitarbeiter: Mitarbeiter | undefined): string {
    if (!(mitarbeiter && mitarbeiter.id)) {
      return '';
    }
    return `${mitarbeiter.nachname}, ${mitarbeiter.vorname}`;
  }
}
