import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { Kunde } from '../../../../shared/model/kunde/kunde';
import { Organisationseinheit } from '../../../../shared/model/organisationseinheit/organisationseinheit';
import { Mitarbeiter } from '../../../../shared/model/mitarbeiter/mitarbeiter';
import { MitarbeiterAnzeigeNamePipe } from '../../../../shared/pipe/mitarbeiter-anzeige-name.pipe';
import { KundeAnzeigeNamePipe } from '../../../../shared/pipe/kunde-anzeige-name.pipe';
import { OrganisationseinheitAnzeigeNamePipe } from '../../../../shared/pipe/organisationseinheit-anzeige-name.pipe';
import {
  getObjektAusListeDurchId,
  getObjektAusListeDurchScribeId,
} from '../../../../shared/util/objekt-in-array-finden.util';

@Component({
  selector: 'pab-skonto-buchung-auswahl-form',
  templateUrl: './skonto-buchung-auswahl-form.component.html',
  styleUrls: ['./skonto-buchung-auswahl-form.component.scss'],
})
export class SkontoBuchungAuswahlFormComponent implements OnInit {
  @Input()
  skontoBuchungAuswahlFormGroup!: FormGroup;

  @Input()
  projektAuswahl!: Projekt[];

  @Input()
  kundeAlle!: Kunde[];

  @Input()
  organisationseinheitAlle!: Organisationseinheit[];

  @Input()
  mitarbeiterAlle!: Mitarbeiter[];

  constructor(
    private mitarbeiterAnzeigeNamePipe: MitarbeiterAnzeigeNamePipe,
    private kundeAnzeigeNamePipe: KundeAnzeigeNamePipe,
    private organisationseinheitAnzeigeNamePipe: OrganisationseinheitAnzeigeNamePipe
  ) {}

  ngOnInit(): void {
    this.reagiereAufAenderungProjekt();
  }

  private reagiereAufAenderungProjekt(): void {
    this.skontoBuchungAuswahlFormGroup!.get('projekt')?.valueChanges.subscribe((projekt: Projekt) => {
      this.patchProjekt(projekt);
    });
  }

  private patchProjekt(projekt: Projekt): void {
    if (projekt && projekt.id) {
      this.skontoBuchungAuswahlFormGroup.patchValue({
        kunde: this.kundeAnzeigeNamePipe.transform(
          getObjektAusListeDurchScribeId(projekt.kundeId, this.kundeAlle) as Kunde
        ),
        projekttyp: projekt.projekttyp,
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
      });
    } else {
      this.skontoBuchungAuswahlFormGroup.get('kunde')?.reset();
      this.skontoBuchungAuswahlFormGroup.get('projekttyp')?.reset();
      this.skontoBuchungAuswahlFormGroup.get('organisationseinheit')?.reset();
      this.skontoBuchungAuswahlFormGroup.get('projektverantwortung')?.reset();
    }
  }
}
