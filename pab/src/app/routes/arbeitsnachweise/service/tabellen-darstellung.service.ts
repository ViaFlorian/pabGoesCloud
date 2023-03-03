import { Injectable } from '@angular/core';
import { Projektstunde } from '../../../shared/model/arbeitsnachweis/projektstunde';
import { ProjektstundeTabellendarstellung } from '../model/projektstunde-tabellendarstellung';
import { getObjektAusListeDurchId } from '../../../shared/util/objekt-in-array-finden.util';
import { Projekt } from '../../../shared/model/projekt/projekt';
import { DatePipe } from '@angular/common';
import { TimeToTextPipe } from '../../../shared/pipe/time-to-text.pipe';
import { ProjektstundeTyp } from '../../../shared/model/konstanten/projektstunde-typ';

@Injectable({
  providedIn: 'root',
})
export class TabellenDarstellungService {
  constructor(private datePipe: DatePipe, private timeToText: TimeToTextPipe) {}

  mapProjektstundeZuTabellendarstellung(
    projektstunden: Projektstunde[],
    projektAuswahl: Projekt[]
  ): ProjektstundeTabellendarstellung[] {
    return projektstunden.map((projektstunde) => {
      const tagVon = this.datePipe.transform(projektstunde.datumVon.toString(), 'dd');
      const tagBis = projektstunde.datumBis ? this.datePipe.transform(projektstunde.datumBis.toString(), 'dd') : null;
      const projekt = getObjektAusListeDurchId(projektstunde.projektId, projektAuswahl) as Projekt as Projekt;
      return {
        id: projektstunde.id,
        tagVon: tagVon ? tagVon : '00',
        uhrzeitVon: this.timeToText.transform(projektstunde.datumVon),
        tagBis: tagBis ? tagBis : '00',
        uhrzeitBis: this.timeToText.transform(projektstunde.datumBis),
        projekt: projekt,
        projektnummer: projekt.projektnummer,
        anzahlStunden: projektstunde.anzahlStunden,
        nichtFakturierfaehig: projektstunde.nichtFakturierfaehig,
        bemerkung: projektstunde.bemerkung,
        projektstundeTypId: projektstunde.projektstundeTypId,
      };
    });
  }

  filtereUndMappeProjektstundenNachProjektstundeTyp(
    projektstunden: Projektstunde[],
    projektstundenTyp: ProjektstundeTyp,
    projektAuswahl: Projekt[]
  ): ProjektstundeTabellendarstellung[] {
    return this.mapProjektstundeZuTabellendarstellung(
      projektstunden.filter((projektstunde) => projektstunde.projektstundeTypId === projektstundenTyp.id),
      projektAuswahl
    );
  }
}
