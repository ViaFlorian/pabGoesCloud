import { Pipe, PipeTransform } from '@angular/core';
import { BearbeitungsstatusEnum } from '../enum/bearbeitungsstatus.enum';

@Pipe({
  name: 'bearbeitungsstatusEnumZuFortschritt',
})
export class BearbeitungsstatusEnumZuFortschrittPipe implements PipeTransform {
  transform(value: BearbeitungsstatusEnum): number {
    switch (value) {
      case BearbeitungsstatusEnum.NEU:
        return 0;
      case BearbeitungsstatusEnum.ERFASST:
        return 20;
      case BearbeitungsstatusEnum.ABGERECHNET:
        return 80;
      case BearbeitungsstatusEnum.ABGESCHLOSSEN:
        return 100;
      default:
        return 0;
    }
  }
}
