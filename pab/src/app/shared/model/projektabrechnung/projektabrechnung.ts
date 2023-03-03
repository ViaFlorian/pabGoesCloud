export interface Projektabrechnung {
  id: string;
  jahr: number;
  monat: number;
  statusId: number;
  projektId: string;
  korrekturVorhanden: boolean;
  fertigstellungsgrad: number;
  budgetBetragZurAbrechnung: number;
}
