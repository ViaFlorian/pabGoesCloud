import { Projekt } from '../../../shared/model/projekt/projekt';

export interface AbwesenheitTabellendarstellung {
  id: string;
  tagVon: string;
  uhrzeitVon: string;
  uhrzeitBis: string;
  projekt: Projekt;
  projektnummer: string;
  arbeitsstaette: string;
  bemerkung: string;
  dreiMonatsRegelAktiv: boolean;
  mitUebernachtung: boolean;
  mitFruehstueck: boolean;
  mitMitagessen: boolean;
  mitAbendessen: boolean;
}
