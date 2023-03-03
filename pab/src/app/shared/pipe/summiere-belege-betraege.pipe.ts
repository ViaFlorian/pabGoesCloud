import { Pipe, PipeTransform } from '@angular/core';
import { BelegTabellendarstellung } from '../model/tabellendarstellung/beleg-tabellendarstellung';

@Pipe({
  name: 'summiereBelegeBetraege',
})
export class SummiereBelegeBetraegePipe implements PipeTransform {
  transform(value: BelegTabellendarstellung[]): number {
    if (value.length === 0) {
      return 0;
    }
    return value.reduce((sum, b) => sum + b.betrag, 0);
  }
}
