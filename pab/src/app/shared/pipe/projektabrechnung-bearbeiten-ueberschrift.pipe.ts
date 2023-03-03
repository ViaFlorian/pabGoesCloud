import { Pipe, PipeTransform } from '@angular/core';
import { BearbeitungsstatusEnum } from '../enum/bearbeitungsstatus.enum';
import { StatusIdZuBearbeitungsstatusEnumPipe } from './status-id-zu-bearbeitungsstatus-enum.pipe';
import { ProjektAbrechnungsmonat } from '../model/projektabrechnung/projekt-abrechnungsmonat';
import { Projekt } from '../model/projekt/projekt';
import { ProjekttypEnum } from '../enum/projekttyp.enum';
import { convertUsNummerZuDeStringMitGenauZweiNachkommastellen } from '../util/nummer-converter.util';

@Pipe({
  name: 'projektabrechnungBearbeitenUeberschrift',
})
export class ProjektabrechnungBearbeitenUeberschriftPipe implements PipeTransform {
  constructor(private statusIdZuBearbeitungsstatusPipe: StatusIdZuBearbeitungsstatusEnumPipe) {}

  transform(
    statusId: number | undefined,
    fertigstellungsgrad: number | undefined,
    abrechnungsmonat: ProjektAbrechnungsmonat,
    projekt: Projekt
  ): string {
    if (!statusId) {
      return 'Projektabrechnung';
    }

    const fertigstellungText: string = fertigstellungsgrad
      ? this.getFertigstellungText(fertigstellungsgrad, projekt)
      : '';

    if (abrechnungsmonat.abgeschlossen) {
      return `Projektabrechnung [Abrechnungsmonat abgeschlossen${fertigstellungText}]`;
    }

    const bearbeitungsstatus: BearbeitungsstatusEnum = this.statusIdZuBearbeitungsstatusPipe.transform(statusId);
    if (bearbeitungsstatus === BearbeitungsstatusEnum.NEU) {
      return `Projektabrechnung [${bearbeitungsstatus}]`;
    }
    return `Projektabrechnung [${bearbeitungsstatus}${fertigstellungText}]`;
  }

  private getFertigstellungText(fertigstellungsgrad: number, projekt: Projekt): string {
    const istRelevantesProjekt: boolean =
      projekt.projekttyp === ProjekttypEnum.FESTPREIS ||
      projekt.projekttyp === ProjekttypEnum.WARTUNG ||
      projekt.projekttyp === ProjekttypEnum.PRODUKT;

    if (istRelevantesProjekt) {
      return `; Fertigstellung ${convertUsNummerZuDeStringMitGenauZweiNachkommastellen(fertigstellungsgrad)}%`;
    }

    return '';
  }
}
