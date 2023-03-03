import { Injectable } from '@angular/core';
import { Abrechnungsmonat } from '../model/sonstiges/abrechnungsmonat';
import { MitarbeiterAbrechnungsmonat } from '../model/mitarbeiter/mitarbeiter-abrechnungsmonat';
import { ProjektAbrechnungsmonat } from '../model/projektabrechnung/projekt-abrechnungsmonat';
import { BenachrichtigungService } from './benachrichtigung.service';

@Injectable({
  providedIn: 'root',
})
export class AbrechnungsmonatService {
  constructor(private benachrichtigungService: BenachrichtigungService) {}

  getAktuellenAbrechnungsmonatAsDate(): Date {
    const datum: Date = new Date();
    // Aktueller Abrechnungsmonat ist immer der Vormonat des aktuellen Monats
    datum.setMonth(datum.getMonth() - 1);
    return new Date(datum.getFullYear(), datum.getMonth(), 1);
  }

  getAktuellenAbrechnungsmonat(): Abrechnungsmonat {
    const datum: Date = this.getAktuellenAbrechnungsmonatAsDate();
    return {
      jahr: datum.getFullYear(),
      monat: datum.getMonth() + 1, // Bei Date entspricht Monat den Zahlen 0 bis 11
      abgeschlossen: false,
    } as Abrechnungsmonat;
  }

  sortiereAbrechnungsmonate(
    abrechnungsmonate: MitarbeiterAbrechnungsmonat[] | ProjektAbrechnungsmonat[] | Abrechnungsmonat[]
  ): void {
    abrechnungsmonate.sort((a, b): number => {
      const dateA: Date = new Date(a.jahr, a.monat - 1);
      const dateB: Date = new Date(b.jahr, b.monat - 1);
      return dateB.getTime() - dateA.getTime();
    });
  }

  ergaenzeFehlendenAbrechnungsmonateMitDummy(
    abrechnungsmonate: MitarbeiterAbrechnungsmonat[] | ProjektAbrechnungsmonat[] | Abrechnungsmonat[]
  ) {
    this.fuegeMonatZuAbrechnungsMonatenHinzu(abrechnungsmonate, this.getAktuellenAbrechnungsmonat());
    this.fuegeMonatZuAbrechnungsMonatenHinzu(abrechnungsmonate, this.getAktuellenMonatAsAbrechnungsmonat());
  }

  erhalteVorauswahlFuerAbrechnungsmonat(
    abrechnungsmonate: MitarbeiterAbrechnungsmonat[] | ProjektAbrechnungsmonat[] | Abrechnungsmonat[],
    queryParams?: QueryParams
  ): MitarbeiterAbrechnungsmonat | ProjektAbrechnungsmonat | Abrechnungsmonat | undefined {
    if (abrechnungsmonate.length === 0) {
      return undefined;
    }
    const abrechnungsmonatAuszuwaehlen = this.getAktuellenAbrechnungsmonat();
    if (queryParams) {
      abrechnungsmonatAuszuwaehlen.jahr = queryParams?.jahr;
      abrechnungsmonatAuszuwaehlen.monat = queryParams?.monat;
    }
    const abrechnungsmonat: MitarbeiterAbrechnungsmonat | ProjektAbrechnungsmonat | Abrechnungsmonat | undefined = (
      abrechnungsmonate as Abrechnungsmonat[]
    ).find(
      (element: Abrechnungsmonat) =>
        element.jahr === abrechnungsmonatAuszuwaehlen.jahr && element.monat === abrechnungsmonatAuszuwaehlen.monat
    );

    if (!abrechnungsmonat) {
      this.benachrichtigungService.erstelleErrorMessage(
        'Es ist ein technischer Fehler aufgetreten. Der ausgewählte Abrechnungsmonat ist für das fachliche Objekt (Mitarbeiter, Projekt, ...) unbekannt.'
      );
      return undefined;
    }
    return abrechnungsmonat;
  }

  istMonatAktuellerAbrechnungsmonat(abrechnungsmonat: MitarbeiterAbrechnungsmonat) {
    const aktuellerAbrechnungsmonat = this.getAktuellenAbrechnungsmonat();
    return (
      abrechnungsmonat.jahr === aktuellerAbrechnungsmonat.jahr &&
      abrechnungsmonat.monat === aktuellerAbrechnungsmonat.monat
    );
  }

  ersetzeAbrechnungsmonat(
    abrechnungsmonatAuswahl: MitarbeiterAbrechnungsmonat[],
    abrechnungsmonatAktualisiert: MitarbeiterAbrechnungsmonat
  ) {
    const index = abrechnungsmonatAuswahl.findIndex((abrechnungsmonat) => {
      return (
        abrechnungsmonat.jahr === abrechnungsmonatAktualisiert.jahr && abrechnungsmonat.monat === abrechnungsmonat.monat
      );
    });
    if (index > -1) {
      abrechnungsmonatAuswahl[index] = abrechnungsmonatAktualisiert;
    }
  }

  private getAktuellenMonatAsAbrechnungsmonat(): Abrechnungsmonat {
    const datum: Date = new Date();
    return {
      monat: datum.getMonth() + 1, // Bei Date entspricht Monat den Zahlen 0 bis 11
      jahr: datum.getFullYear(),
      abgeschlossen: false,
    } as Abrechnungsmonat;
  }

  private fuegeMonatZuAbrechnungsMonatenHinzu(
    abrechnungsmonate: MitarbeiterAbrechnungsmonat[],
    abrechnungsmonatHinzuzufuegen: MitarbeiterAbrechnungsmonat
  ) {
    const abrechnungsmonat: MitarbeiterAbrechnungsmonat | undefined = abrechnungsmonate.find(
      (element) =>
        element.monat === abrechnungsmonatHinzuzufuegen.monat && element.jahr === abrechnungsmonatHinzuzufuegen.jahr
    );
    if (abrechnungsmonat) {
      return;
    }
    abrechnungsmonate.push(abrechnungsmonatHinzuzufuegen);
  }
}

export interface QueryParams {
  monat: number;
  jahr: number;
}
