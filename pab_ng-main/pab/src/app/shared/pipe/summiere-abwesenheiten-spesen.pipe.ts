import { Pipe, PipeTransform } from '@angular/core';
import { Abwesenheit } from '../model/arbeitsnachweis/abwesenheit';

@Pipe({
  name: 'summiereAbwesenheitenSpesen',
})
export class SummiereAbwesenheitenSpesenPipe implements PipeTransform {
  transform(value: Abwesenheit[]): number {
    if (value.length === 0) {
      return 0;
    }
    return value.reduce((sum, b) => sum + (b.spesen ? b.spesen : 0), 0);
  }
}
