import { Pipe, PipeTransform } from '@angular/core';
import { Mitarbeiter } from '../model/mitarbeiter/mitarbeiter';

@Pipe({
  name: 'mitarbeiterAnzeigeName',
})
export class MitarbeiterAnzeigeNamePipe implements PipeTransform {
  transform(mitarbeiter: Mitarbeiter): string {
    if (!(mitarbeiter && mitarbeiter.id)) {
      return '';
    }
    return `${mitarbeiter.personalNr}; ${mitarbeiter.nachname}, ${mitarbeiter.vorname}`;
  }
}
