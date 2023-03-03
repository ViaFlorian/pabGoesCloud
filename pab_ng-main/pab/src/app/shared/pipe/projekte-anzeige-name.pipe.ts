import { Pipe, PipeTransform } from '@angular/core';
import { Projekt } from '../model/projekt/projekt';

@Pipe({
  name: 'projekteAnzeigeName',
})
export class ProjekteAnzeigeNamePipe implements PipeTransform {
  transform(projekt: Projekt): string {
    if (!(projekt && projekt.id)) {
      return '';
    }
    return `${projekt.projektnummer}; ${projekt.bezeichnung}`;
  }
}
