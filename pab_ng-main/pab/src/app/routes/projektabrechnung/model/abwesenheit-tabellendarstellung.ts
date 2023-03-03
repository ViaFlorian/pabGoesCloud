import { Projekt } from '../../../shared/model/projekt/projekt';

export interface AbwesenheitTabellendarstellung {
  tagVon: string;
  uhrzeitVon: string;
  uhrzeitBis: string;
  projekt: Projekt;
  projektnummer: string;
  spesen: number;
  zuschlag: number;
}
