import { Pipe, PipeTransform } from '@angular/core';
import { Abwesenheit } from '../model/arbeitsnachweis/abwesenheit';

@Pipe({
  name: 'summiereAbwesenheitenZuschlaege',
})
export class SummiereAbwesenheitenZuschlaegePipe implements PipeTransform {
  transform(value: Abwesenheit[]): number {
    if (value.length === 0) {
      return 0;
    }
    return value.reduce((sum, b) => sum + (b.zuschlag ? b.zuschlag : 0), 0);
  }
}
