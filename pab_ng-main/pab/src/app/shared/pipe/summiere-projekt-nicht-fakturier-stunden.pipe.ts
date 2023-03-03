import { Pipe, PipeTransform } from '@angular/core';
import { ProjektstundeTabellendarstellung } from '../../routes/arbeitsnachweise/model/projektstunde-tabellendarstellung';

@Pipe({
  name: 'summiereProjektNichtFakturierStunden',
})
export class SummiereProjektNichtFakturierStundenPipe implements PipeTransform {
  transform(value: ProjektstundeTabellendarstellung[]): number {
    if (!value) {
      return 0;
    }
    return value.filter((p) => p.nichtFakturierfaehig).reduce((sum, p) => sum + p.anzahlStunden, 0);
  }
}
