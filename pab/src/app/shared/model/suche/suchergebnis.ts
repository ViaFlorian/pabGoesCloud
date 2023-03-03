import { SuchergebnisAktion } from './suchergebnis-aktion';

export interface Suchergebnis {
  bezeichnung: string;

  typ: string;

  aktion: SuchergebnisAktion[];
}
