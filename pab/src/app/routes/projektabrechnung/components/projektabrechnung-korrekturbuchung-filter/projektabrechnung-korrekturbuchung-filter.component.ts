import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { Kunde } from '../../../../shared/model/kunde/kunde';
import { Organisationseinheit } from '../../../../shared/model/organisationseinheit/organisationseinheit';
import { getObjektAusListeDurchScribeId } from '../../../../shared/util/objekt-in-array-finden.util';
import { KundeAnzeigeNamePipe } from '../../../../shared/pipe/kunde-anzeige-name.pipe';
import { OrganisationseinheitAnzeigeNamePipe } from '../../../../shared/pipe/organisationseinheit-anzeige-name.pipe';

@Component({
  selector: 'pab-projektabrechnung-korrekturbuchung-filter',
  templateUrl: './projektabrechnung-korrekturbuchung-filter.component.html',
  styleUrls: ['./projektabrechnung-korrekturbuchung-filter.component.scss'],
})
export class ProjektabrechnungKorrekturbuchungFilterComponent implements OnInit {
  @Input()
  korrekturbuchungFilterGroup!: FormGroup;

  @Input()
  projektAuswahl!: Projekt[];

  @Input()
  kundeAuswahl!: Kunde[];

  @Input()
  organisationseinheitAuswahl!: Organisationseinheit[];

  constructor(
    private kundeAnzeigeNamePipe: KundeAnzeigeNamePipe,
    private organisationseinheitAnzeigeNamePipe: OrganisationseinheitAnzeigeNamePipe
  ) {}

  ngOnInit() {
    this.reagiereAufAenderungProjekt();
  }

  leereKorrekturbuchungFilterForm() {
    this.korrekturbuchungFilterGroup.reset();
  }

  private reagiereAufAenderungProjekt(): void {
    this.korrekturbuchungFilterGroup.get('projekt')?.valueChanges.subscribe((projekt) => {
      this.patchProjekt(projekt);
    });
  }

  private patchProjekt(projekt: Projekt) {
    if (projekt && projekt.id) {
      this.korrekturbuchungFilterGroup.patchValue({
        kunde: this.kundeAnzeigeNamePipe.transform(
          getObjektAusListeDurchScribeId(projekt.kundeId, this.kundeAuswahl) as Kunde
        ),
        organisationseinheit: this.organisationseinheitAnzeigeNamePipe.transform(
          getObjektAusListeDurchScribeId(
            projekt.organisationseinheitId,
            this.organisationseinheitAuswahl
          ) as Organisationseinheit
        ),
      });
    } else {
      this.korrekturbuchungFilterGroup.get('kunde')?.reset();
      this.korrekturbuchungFilterGroup.get('organisationseinheit')?.reset();
    }
  }
}
