import { Pipe, PipeTransform } from '@angular/core';
import { ProjektabrechnungKostenLeistung } from '../model/projektabrechnung/projektabrechnung-kosten-leistung';
import { Mitarbeiter } from '../model/mitarbeiter/mitarbeiter';
import { getObjektAusListeDurchId } from '../util/objekt-in-array-finden.util';

@Pipe({
  name: 'projaktabrechnungKostenLeistungAnzeigeName',
})
export class ProjaktabrechnungKostenLeistungAnzeigeNamePipe implements PipeTransform {
  transform(projektabrechnungKostenLeistung: ProjektabrechnungKostenLeistung, mitarbeiterAlle: Mitarbeiter[]): string {
    const prefix: string = this.erzeugePrefix(projektabrechnungKostenLeistung);

    if (projektabrechnungKostenLeistung.ohneMitarbeiterBezug) {
      return prefix.length === 0
        ? 'ohne Mitarbeiterbezug'
        : `${prefix} - ohne
      Mitarbeiterbezug`;
    } else {
      const mitarbeiter: Mitarbeiter = getObjektAusListeDurchId(
        projektabrechnungKostenLeistung.mitarbeiterId,
        mitarbeiterAlle
      ) as Mitarbeiter;

      return prefix.length === 0
        ? `leer - ${mitarbeiter.nachname}, ${mitarbeiter.vorname}`
        : `${prefix} - ${mitarbeiter.nachname}, ${mitarbeiter.vorname}`;
    }
  }

  private erzeugePrefix(projektabrechnungKostenLeistung: ProjektabrechnungKostenLeistung): string {
    let prefix: string = '';

    if (projektabrechnungKostenLeistung.projektzeitKosten + projektabrechnungKostenLeistung.projektzeitLeistung > 0) {
      prefix = prefix + 'P';
    }
    if (projektabrechnungKostenLeistung.reiseKosten + projektabrechnungKostenLeistung.reiseLeistung > 0) {
      prefix = prefix + 'R';
    }
    if (projektabrechnungKostenLeistung.sonderzeitKosten + projektabrechnungKostenLeistung.sonderzeitLeistung > 0) {
      prefix = prefix + 'S';
    }
    if (projektabrechnungKostenLeistung.sonstigeKosten + projektabrechnungKostenLeistung.sonstigeLeistung > 0) {
      prefix = prefix + 'Ü';
    }
    if (projektabrechnungKostenLeistung.fakturierfaehigeLeistung > 0) {
      prefix = prefix + 'F';
    }

    return prefix;
  }
}
