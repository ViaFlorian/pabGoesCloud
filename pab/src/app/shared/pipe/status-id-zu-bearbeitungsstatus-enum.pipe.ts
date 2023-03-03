import { Pipe, PipeTransform } from '@angular/core';
import { BearbeitungsstatusEnum } from '../enum/bearbeitungsstatus.enum';

@Pipe({
  name: 'statusIdZuBearbeitungsstatusEnum',
})
export class StatusIdZuBearbeitungsstatusEnumPipe implements PipeTransform {
  transform(value: number): BearbeitungsstatusEnum {
    switch (value) {
      case 10:
        return BearbeitungsstatusEnum.ERFASST;
      case 40:
        return BearbeitungsstatusEnum.ABGERECHNET;
      case 50:
        return BearbeitungsstatusEnum.ABGESCHLOSSEN;
      default:
        return BearbeitungsstatusEnum.NEU;
    }
  }
}
