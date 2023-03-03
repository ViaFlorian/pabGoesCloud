import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Projekt } from '../../../../shared/model/projekt/projekt';
import { Kunde } from '../../../../shared/model/kunde/kunde';
import { getObjektAusListeDurchScribeId } from '../../../../shared/util/objekt-in-array-finden.util';
import { KundeAnzeigeNamePipe } from '../../../../shared/pipe/kunde-anzeige-name.pipe';

@Component({
  selector: 'pab-fakturuebersicht-auswahl-form',
  templateUrl: './fakturuebersicht-auswahl-form.component.html',
  styleUrls: ['./fakturuebersicht-auswahl-form.component.scss'],
})
export class FakturuebersichtAuswahlFormComponent implements OnInit {
  @Input()
  fakturAuswahlFormGroup!: FormGroup;

  @Input()
  projektAuswahl!: Projekt[];

  @Input()
  kundeAlle!: Kunde[];

  constructor(private kundeAnzeigeNamePipe: KundeAnzeigeNamePipe) {}

  ngOnInit(): void {
    this.reagiereAufAenderungProjekt();
  }

  private reagiereAufAenderungProjekt(): void {
    this.fakturAuswahlFormGroup.get('projekt')?.valueChanges.subscribe((projekt: Projekt) => {
      this.patchProjekt(projekt);
    });
  }

  private patchProjekt(projekt: Projekt): void {
    if (projekt && projekt.id) {
      const kunde: Kunde = getObjektAusListeDurchScribeId(projekt.kundeId, this.kundeAlle) as Kunde;
      this.fakturAuswahlFormGroup.patchValue({
        rechnungsempfaenger: this.kundeAnzeigeNamePipe.transform(kunde),
        debitorennummer: kunde ? kunde.debitorennummer : '',
      });
    } else {
      this.fakturAuswahlFormGroup.get('rechnungsempfaenger')?.reset();
      this.fakturAuswahlFormGroup.get('debitorennummer')?.reset();
    }
  }
}
