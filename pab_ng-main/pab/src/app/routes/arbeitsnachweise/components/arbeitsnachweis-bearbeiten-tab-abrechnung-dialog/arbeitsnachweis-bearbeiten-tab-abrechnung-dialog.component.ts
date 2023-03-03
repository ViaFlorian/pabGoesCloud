import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { entferneUnerlaubteZeichenAusFormGroupKomponente } from '../../../../shared/util/regex-control.util';

@Component({
  selector: 'pab-arbeitsnachweis-bearbente-tab-abrechnung-dialog',
  templateUrl: './arbeitsnachweis-bearbeiten-tab-abrechnung-dialog.component.html',
  styleUrls: ['./arbeitsnachweis-bearbeiten-tab-abrechnung-dialog.component.scss'],
})
export class ArbeitsnachweisBearbeitenTabAbrechnungDialogComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: ArbeitsnachweisBearbeitenTabAbrechnungFormDialogData) {}

  entferneUnerlaubteZeichen(komponentenName: string, erlaubteExp: string) {
    entferneUnerlaubteZeichenAusFormGroupKomponente(
      this.data.abrechnungAuszahlungFormGroup,
      komponentenName,
      erlaubteExp
    );
  }
}

export interface ArbeitsnachweisBearbeitenTabAbrechnungFormDialogData {
  abrechnungAuszahlungFormGroup: any;
}
