import { Pipe, PipeTransform } from '@angular/core';
import { from, Observable } from 'rxjs';
import { Mitarbeiter } from '../model/mitarbeiter/mitarbeiter';

@Pipe({
  name: 'mitarbeiterToObs',
})
export class MitarbeiterToObsPipe implements PipeTransform {
  transform(mitarbeiter: Mitarbeiter[]): Observable<Mitarbeiter[]> {
    return from([mitarbeiter]);
  }
}
