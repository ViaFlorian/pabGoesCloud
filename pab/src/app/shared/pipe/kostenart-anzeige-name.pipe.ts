import { Pipe, PipeTransform } from '@angular/core';
import { Kostenart } from '../model/konstanten/kostenart';

@Pipe({
  name: 'kostenartAnzeigeName',
})
export class KostenartAnzeigeNamePipe implements PipeTransform {
  transform(kostenart: Kostenart): string {
    if (!(kostenart && kostenart.id)) {
      return '';
    }
    return `${kostenart.bezeichnung}`;
  }
}
