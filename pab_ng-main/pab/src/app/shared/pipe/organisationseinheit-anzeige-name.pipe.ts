import { Pipe, PipeTransform } from '@angular/core';
import { Organisationseinheit } from '../model/organisationseinheit/organisationseinheit';

@Pipe({
  name: 'organisationseinheitAnzeigeName',
})
export class OrganisationseinheitAnzeigeNamePipe implements PipeTransform {
  transform(organisationseinheit: Organisationseinheit): string {
    if (!(organisationseinheit && organisationseinheit.id)) {
      return '';
    }
    return `${organisationseinheit.bezeichnung}`;
  }
}
