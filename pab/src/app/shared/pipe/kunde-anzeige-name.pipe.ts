import { Pipe, PipeTransform } from '@angular/core';
import { Kunde } from '../model/kunde/kunde';

@Pipe({
  name: 'kundeAnzeigeName',
})
export class KundeAnzeigeNamePipe implements PipeTransform {
  transform(kunde: Kunde): string {
    if (!(kunde && kunde.id)) {
      return '';
    }
    return `${kunde.bezeichnung}`;
  }
}
