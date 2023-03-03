import { Pipe, PipeTransform } from '@angular/core';
import { convertUsNummerZuDeString } from '../util/nummer-converter.util';

@Pipe({
  name: 'usNummerZuDeString',
})
export class UsNummerZuDeStringPipe implements PipeTransform {
  transform(nummer: number): string {
    return convertUsNummerZuDeString(nummer);
  }
}
