import { Pipe, PipeTransform } from '@angular/core';
import { ProjektstundeTabellendarstellung } from '../../routes/arbeitsnachweise/model/projektstunde-tabellendarstellung';

@Pipe({
  name: 'summiereProjektStunden',
})
export class SummiereProjektStundenPipe implements PipeTransform {
  transform(value: ProjektstundeTabellendarstellung[]): number {
    if (value.length === 0) {
      return 0;
    }
    return value.reduce((sum, p) => sum + p.anzahlStunden, 0);
  }
}
