import { Mitarbeiter } from '../../../shared/model/mitarbeiter/mitarbeiter';

export interface NichtAbgeschlosseneMitarbeiterTabellendarstellung {
  mitarbeiterName: string;
  mitarbeiter: Mitarbeiter;
  internExtern: string;
  sachbearbeiterName: string;
  bearbeitungsstatus: string;
}
