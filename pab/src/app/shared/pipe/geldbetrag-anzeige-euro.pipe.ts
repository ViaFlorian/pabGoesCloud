import { Pipe, PipeTransform } from '@angular/core';
import { DecimalPipe } from '@angular/common';

@Pipe({
  name: 'anzeigeGeldbetraegeEuro',
})
export class AnzeigeGeldbetraegeEuroPipe implements PipeTransform {
  constructor(private decimalPipe: DecimalPipe) {}

  transform(num: Number | null): string {
    const betrag: String | null = this.decimalPipe.transform(num?.toString(), '1.2-2', 'de');
    if (betrag === null) {
      return '';
    } else {
      return betrag + ' €';
    }
  }
}
