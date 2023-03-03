import { Pipe, PipeTransform } from '@angular/core';
import { Mitarbeiter } from '../model/mitarbeiter/mitarbeiter';

@Pipe({
  name: 'isInternToText',
})
export class IsInternToTextPipe implements PipeTransform {
  transform(mitarbeiter: Mitarbeiter): string {
    return mitarbeiter && mitarbeiter.intern ? 'Intern' : 'Extern';
  }
}
