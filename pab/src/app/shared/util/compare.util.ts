import { Kunde } from '../model/kunde/kunde';
import { Mitarbeiter } from '../model/mitarbeiter/mitarbeiter';
import { MitarbeiterAbrechnungsmonat } from '../model/mitarbeiter/mitarbeiter-abrechnungsmonat';
import { Organisationseinheit } from '../model/organisationseinheit/organisationseinheit';
import { Projekt } from '../model/projekt/projekt';
import { Abrechnungsmonat } from '../model/sonstiges/abrechnungsmonat';
import { ProjektAbrechnungsmonat } from '../model/projektabrechnung/projekt-abrechnungsmonat';
import { ProjekttypEnum } from '../enum/projekttyp.enum';

export function sindMitarbeiterGleich(mitarbeiter1: string | Mitarbeiter, mitarbeiter2: string | Mitarbeiter): boolean {
  // Ersetze {} damit ein leeres Objekt mit einem leeren string verlgichen werden kann
  const parentString = mitarbeiter1 ? JSON.stringify(mitarbeiter1).replace('{}', '') : '';
  const childString = mitarbeiter2 ? JSON.stringify(mitarbeiter2)?.replace('{}', '') : '';

  return parentString === childString;
}

export function sindAbrechnungsmonateGleich(
  abrechnungsmonat1: Abrechnungsmonat,
  abrechnungsmonat2: Abrechnungsmonat
): boolean {
  return abrechnungsmonat1.jahr === abrechnungsmonat2.jahr && abrechnungsmonat1.monat === abrechnungsmonat2.monat;
}

export function sindMitarbeiterAbrechnungsmonateGleich(
  abrechnungsmonat1: MitarbeiterAbrechnungsmonat,
  abrechnungsmonat2: MitarbeiterAbrechnungsmonat
): boolean {
  return (
    abrechnungsmonat1.arbeitsnachweisId === abrechnungsmonat2.arbeitsnachweisId &&
    abrechnungsmonat1.jahr === abrechnungsmonat2.jahr &&
    abrechnungsmonat1.monat === abrechnungsmonat2.monat
  );
}

export function sindProjektAbrechnungsmonateGleich(
  abrechnungsmonat1: ProjektAbrechnungsmonat,
  abrechnungsmonat2: ProjektAbrechnungsmonat
): boolean {
  return (
    abrechnungsmonat1.projektabrechnungId === abrechnungsmonat2.projektabrechnungId &&
    abrechnungsmonat1.jahr === abrechnungsmonat2.jahr &&
    abrechnungsmonat1.monat === abrechnungsmonat2.monat
  );
}

export function istDatumVonAbrechnungsmonatGleich(abrechnungsmonat1: Date, abrechnungsmonat2: Date): boolean {
  if (
    abrechnungsmonat1.getMonth() !== abrechnungsmonat2.getMonth() ||
    abrechnungsmonat1.getFullYear() !== abrechnungsmonat2.getFullYear()
  ) {
    return true;
  } else {
    return false;
  }
}

export function sindOrganisationseinheitenGleich(
  organisationseinheit1: Organisationseinheit,
  organisationseinheit2: Organisationseinheit
) {
  const parentString = organisationseinheit1 ? JSON.stringify(organisationseinheit1).replace('{}', '') : '';
  const childString = organisationseinheit2 ? JSON.stringify(organisationseinheit2).replace('{}', '') : '';
  return parentString === childString;
}

export function sindProjekteGleich(projekt1: Projekt, projekt2: Projekt) {
  const parentString = projekt1 ? JSON.stringify(projekt1).replace('{}', '') : '';
  const childString = projekt2 ? JSON.stringify(projekt2).replace('{}', '') : '';
  return parentString === childString;
}

export function sindKundenGleich(kunde1: Kunde, kunde2: Kunde) {
  const parentString = kunde1 ? JSON.stringify(kunde1).replace('{}', '') : '';
  const childString = kunde2 ? JSON.stringify(kunde2).replace('{}', '') : '';
  return parentString === childString;
}

export function sindKostenartenGleich(kostenartArray1: Array<String>, kostenartArray2: Array<String>) {
  const parentString = kostenartArray1 === null || kostenartArray1.length === 0 ? '' : JSON.stringify(kostenartArray1);
  const childString = kostenartArray2 === null || kostenartArray2.length === 0 ? '' : JSON.stringify(kostenartArray2);
  return parentString === childString;
}

export function istProjektKunde(projekt: Projekt | undefined): boolean {
  if (!projekt || !projekt.id) {
    return false;
  }

  return (
    projekt.projekttyp === ProjekttypEnum.DIENSTLEISTUNG ||
    projekt.projekttyp === ProjekttypEnum.FESTPREIS ||
    projekt.projekttyp === ProjekttypEnum.PRODUKT ||
    projekt.projekttyp === ProjekttypEnum.WARTUNG
  );
}

export function istProjektFestpreis(projekt: Projekt | undefined): boolean {
  if (!projekt || !projekt.id) {
    return false;
  }

  return (
    projekt.projekttyp === ProjekttypEnum.FESTPREIS ||
    projekt.projekttyp === ProjekttypEnum.PRODUKT ||
    projekt.projekttyp === ProjekttypEnum.WARTUNG
  );
}
