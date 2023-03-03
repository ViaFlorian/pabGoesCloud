import { Pipe, PipeTransform } from '@angular/core';
import { SonstigeProjektkosten } from '../model/projektabrechnung/sonstige-projektkosten';

@Pipe({
  name: 'summiereSonstigeProjektkostenKosten',
})
export class SummiereSonstigeProjektkostenKostenPipe implements PipeTransform {
  transform(value: SonstigeProjektkosten[]): number {
    if (value.length === 0) {
      return 0;
    }
    return value.reduce((sum, b) => sum + b.kosten, 0);
  }
}
