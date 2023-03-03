import { Projekt } from '../../../shared/model/projekt/projekt';
import { Mitarbeiter } from '../../../shared/model/mitarbeiter/mitarbeiter';
import { Organisationseinheit } from '../../../shared/model/organisationseinheit/organisationseinheit';
import { Kunde } from '../../../shared/model/kunde/kunde';

export interface ProjektabrechnungUebersichtTabellendarstellung {
  projektabrechnungId: string;
  jahr: number;
  monat: number;
  abrechnungsmonat: string;
  statusId: number;
  projekt: Projekt;
  projektnummer: string;
  projektBezeichnung: string;
  projekttyp: string;
  organisationseinheit: Organisationseinheit;
  organisationseinheitName: string;
  kunde: Kunde;
  kundeName: string;
  sachbearbeiter: Mitarbeiter;
  sachbearbeiterName: string;
  anzahlMitarbeiter: number;
  hatKorrekturbuchungen: boolean;
  kosten: number;
  leistung: number;
}
