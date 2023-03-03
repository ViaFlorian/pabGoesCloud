export interface Abwesenheit {
  id: string;
  datumVon: Date;
  datumBis: Date;
  projektId: string;
  arbeitsstaette: string;
  bemerkung: string;
  dreiMonatsRegelAktiv: boolean;
  mitUebernachtung: boolean;
  mitFruehstueck: boolean;
  mitMittagessen: boolean;
  mitAbendessen: boolean;
  spesen?: number;
  zuschlag?: number;
}
