import { Pipe, PipeTransform } from '@angular/core';
import { BearbeitungsstatusEnum } from '../enum/bearbeitungsstatus.enum';
import { Arbeitsnachweis } from '../model/arbeitsnachweis/arbeitsnachweis';
import { StatusIdZuBearbeitungsstatusEnumPipe } from './status-id-zu-bearbeitungsstatus-enum.pipe';

@Pipe({
  name: 'arbeitsnachweisBearbeitenUeberschrift',
})
export class ArbeitsnachweisBearbeitenUeberschriftPipe implements PipeTransform {
  constructor(private statusIdZuBearbeitungsstatusPipe: StatusIdZuBearbeitungsstatusEnumPipe) {}

  transform(arbeitsnachweis: Arbeitsnachweis | undefined): string {
    if (arbeitsnachweis) {
      const bearbeitungsstatus: BearbeitungsstatusEnum = this.statusIdZuBearbeitungsstatusPipe.transform(
        arbeitsnachweis.statusId
      );
      return `Arbeitsnachweis [${bearbeitungsstatus}]`;
    }
    return 'Arbeitsnachweis';
  }
}
