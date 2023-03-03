import { ObjektMitId } from '../model/sonstiges/objekt-mit-id';

export function setzeIdAufNullWennNegativ(objekteMitIdListe: ObjektMitId[]): void {
  objekteMitIdListe.map((objektMitId) => {
    if (parseInt(objektMitId.id) < 0) {
      objektMitId.id = '';
    }
  });
}
