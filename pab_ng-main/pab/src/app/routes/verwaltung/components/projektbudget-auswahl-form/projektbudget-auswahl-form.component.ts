import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { Kunde } from '../../../../shared/model/kunde/kunde';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import {
  getObjektAusListeDurchId,
  getObjektAusListeDurchScribeId,
} from '../../../../shared/util/objekt-in-array-finden.util';
import { MitarbeiterAnzeigeNamePipe } from '../../../../shared/pipe/mitarbeiter-anzeige-name.pipe';
import { KundeAnzeigeNamePipe } from '../../../../shared/pipe/kunde-anzeige-name.pipe';
import { OrganisationseinheitAnzeigeNamePipe } from '../../../../shared/pipe/organisationseinheit-anzeige-name.pipe';
import { Organisationseinheit } from '../../../../shared/model/organisationseinheit/organisationseinheit';

@Component({
  selector: 'pab-projektbudget-auswahl-form',
  templateUrl: './projektbudget-auswahl-form.component.html',
  styleUrls: ['./projektbudget-auswahl-form.component.scss'],
})
export class ProjektbudgetAuswahlFormComponent implements OnInit {
  @Input()
  projektbudgetAuswahlFormGroup!: FormGroup;

  @Input()
  projektAuswahl!: Projekt[];

  @Input()
  kundeAlle!: Kunde[];

  @Input()
  mitarbeiterAlle!: Mitarbeiter[];

  @Input()
  sachbearbeiterAlle!: Mitarbeiter[];

  @Input()
  organisationseinheitAlle!: Organisationseinheit[];

  constructor(
    private mitarbeiterAnzeigeNamePipe: MitarbeiterAnzeigeNamePipe,
    private kundeAnzeigeNamePipe: KundeAnzeigeNamePipe,
    private organisationseinheitAnzeigeNamePipe: OrganisationseinheitAnzeigeNamePipe
  ) {}

  ngOnInit(): void {
    this.reagiereAufAenderungProjekt();
  }

  private reagiereAufAenderungProjekt(): void {
    this.projektbudgetAuswahlFormGroup!.get('projekt')?.valueChanges.subscribe((projekt: Projekt) => {
      this.patchProjekt(projekt);
    });
  }

  private patchProjekt(projekt: Projekt): void {
    if (projekt && projekt.id) {
      this.projektbudgetAuswahlFormGroup.patchValue({
        kunde: this.kundeAnzeigeNamePipe.transform(
          getObjektAusListeDurchScribeId(projekt.kundeId, this.kundeAlle) as Kunde
        ),
        projekttyp: projekt.projekttyp,
        sachbearbeiter: projekt.sachbearbeiterId
          ? this.mitarbeiterAnzeigeNamePipe.transform(
              getObjektAusListeDurchId(projekt.sachbearbeiterId, this.sachbearbeiterAlle) as Mitarbeiter
            )
          : 'nicht konfiguriert',
        organisationseinheit: this.organisationseinheitAnzeigeNamePipe.transform(
          getObjektAusListeDurchScribeId(
            projekt.organisationseinheitId,
            this.organisationseinheitAlle
          ) as Organisationseinheit
        ),
        projektverantwortung: projekt.verantwortlicherMitarbeiterId
          ? this.mitarbeiterAnzeigeNamePipe.transform(
              getObjektAusListeDurchId(projekt.verantwortlicherMitarbeiterId, this.mitarbeiterAlle) as Mitarbeiter
            )
          : 'nicht konfiguriert',
        geschaeftsstelle: projekt.geschaeftsstelle,
      });
    } else {
      this.projektbudgetAuswahlFormGroup.get('kunde')?.reset();
      this.projektbudgetAuswahlFormGroup.get('projekttyp')?.reset();
      this.projektbudgetAuswahlFormGroup.get('sachbearbeiter')?.reset();
      this.projektbudgetAuswahlFormGroup.get('organisationseinheit')?.reset();
      this.projektbudgetAuswahlFormGroup.get('projektverantwortung')?.reset();
      this.projektbudgetAuswahlFormGroup.get('geschaeftsstelle')?.reset();
    }
  }
}
