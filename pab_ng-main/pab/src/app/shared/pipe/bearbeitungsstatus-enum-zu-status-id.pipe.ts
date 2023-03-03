import { Pipe, PipeTransform } from '@angular/core';
import { BearbeitungsstatusEnum } from '../enum/bearbeitungsstatus.enum';

@Pipe({
  name: 'bearbeitungsstatusEnumZuStatusId',
})
export class BearbeitungsstatusEnumZuStatusIdPipe implements PipeTransform {
  transform(value: BearbeitungsstatusEnum): number {
    switch (value) {
      case BearbeitungsstatusEnum.NEU:
        return 0;
      case BearbeitungsstatusEnum.ERFASST:
        return 10;
      case BearbeitungsstatusEnum.ABGERECHNET:
        return 40;
      case BearbeitungsstatusEnum.ABGESCHLOSSEN:
        return 50;
      default:
        return 0;
    }
  }
}
