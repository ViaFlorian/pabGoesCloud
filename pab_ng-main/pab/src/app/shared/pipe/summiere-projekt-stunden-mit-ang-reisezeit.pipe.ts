import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'summiereProjektStundenMitAngReisezeit',
})
export class SummiereProjektStundenMitAngReisezeitPipe implements PipeTransform {
  transform(summeProjektstunden: number, angerechneteReisezeit: number): number {
    return summeProjektstunden + angerechneteReisezeit;
  }
}
