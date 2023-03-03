import { ObjektMitId } from '../model/sonstiges/objekt-mit-id';
import { ObjektMitScribeId } from '../model/sonstiges/objekt-mit-scribe-id';

/**
 * Liefert über eine ID das Objekt aus einer Liste zurück.
 *
 * @param id ID des zu suchenden Objektes
 * @param objektListe Zu durchsuchende Liste
 * @return undefined oder Objekt
 */
export function getObjektAusListeDurchId(id: string, objektListe: ObjektMitId[]): ObjektMitId | undefined {
  return objektListe.find((objekt) => objekt.id === id);
}

/**
 * Liefert über eine ScribeID das Objekt aus einer Liste zurück.
 *
 * @param scribeId ScribeID des zu suchenden Objektes
 * @param objektListe Zu durchsuchende Liste
 * @return undefined oder Objekt
 */
export function getObjektAusListeDurchScribeId(
  scribeId: string,
  objektListe: ObjektMitScribeId[]
): ObjektMitScribeId | undefined {
  return objektListe.find((objekt) => objekt.scribeId === scribeId);
}
