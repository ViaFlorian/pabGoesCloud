import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ArbeitsnachweisAbrechnung } from '../../../../shared/model/arbeitsnachweis/arbeitsnachweis-abrechnung';
import { MatDialog } from '@angular/material/dialog';
import {
  convertDeStringZuUsNummer,
  convertUsNummerZuDeString,
  rundeNummerAufZweiNackommastellen,
} from '../../../../shared/util/nummer-converter.util';
import {
  ArbeitsnachweisBearbeitenTabAbrechnungDialogComponent,
  ArbeitsnachweisBearbeitenTabAbrechnungFormDialogData,
} from '../arbeitsnachweis-bearbeiten-tab-abrechnung-dialog/arbeitsnachweis-bearbeiten-tab-abrechnung-dialog.component';
import { SpinnerRef } from '../../../../shared/component/spinner/spinner-ref';
import { NavigationService } from '../../../../shared/service/navigation.service';

@Component({
  selector: 'pab-arbeitsnachweis-bearbeiten-tab-abrechnung',
  templateUrl: './arbeitsnachweis-bearbeiten-tab-abrechnung.component.html',
  styleUrls: ['./arbeitsnachweis-bearbeiten-tab-abrechnung.component.scss'],
})
export class ArbeitsnachweisBearbeitenTabAbrechnungComponent {
  @Input()
  abrechnungAuszahlungFormGroup!: FormGroup;

  @Input()
  arbeitsnachweisAbrechnung!: ArbeitsnachweisAbrechnung;

  @Input()
  arbeitsnachweisOffen!: boolean;

  @Input()
  ladeAbrechnungSpinner!: SpinnerRef;

  @Output()
  auszahlungUndSonderstundenAktualisiertEvent = new EventEmitter<{}>();

  constructor(public dialog: MatDialog, public navigationService: NavigationService) {}

  oeffneFormDialog() {
    const data: ArbeitsnachweisBearbeitenTabAbrechnungFormDialogData = {
      abrechnungAuszahlungFormGroup: this.abrechnungAuszahlungFormGroup,
    };
    this.dialog
      .open(ArbeitsnachweisBearbeitenTabAbrechnungDialogComponent, {
        data,
        panelClass: 'dialog-container-small',
      })
      .afterClosed()
      .subscribe((result) => {
        if (!result) {
          this.setzteFormZurueck();
          return;
        }
        this.aktualisiereAusUndSonderzahlung();
      });
  }

  private aktualisiereAusUndSonderzahlung() {
    if (this.abrechnungAuszahlungFormGroup.invalid) {
      return;
    }
    this.aktualisiereAbrechnung();
    this.setzteFormZurueck();
    this.auszahlungUndSonderstundenAktualisiertEvent.emit();
  }

  private setzteFormZurueck() {
    this.abrechnungAuszahlungFormGroup.patchValue({
      sollstunden: this.arbeitsnachweisAbrechnung.sollstunden
        ? convertUsNummerZuDeString(this.arbeitsnachweisAbrechnung.sollstunden)
        : '0',
      auszahlung: this.arbeitsnachweisAbrechnung.auszahlung
        ? convertUsNummerZuDeString(this.arbeitsnachweisAbrechnung.auszahlung)
        : '0',
    });
  }

  private aktualisiereUebertraegeInAbrechnung() {
    this.arbeitsnachweisAbrechnung.uebertragVorAuszahlung = rundeNummerAufZweiNackommastellen(
      this.arbeitsnachweisAbrechnung.vortrag +
        this.arbeitsnachweisAbrechnung.summeIstStunden -
        this.arbeitsnachweisAbrechnung.sollstunden
    );

    this.arbeitsnachweisAbrechnung.uebertragNachAuszahlung = rundeNummerAufZweiNackommastellen(
      this.arbeitsnachweisAbrechnung.uebertragVorAuszahlung -
        this.arbeitsnachweisAbrechnung.sonderarbeitszeit -
        this.arbeitsnachweisAbrechnung.auszahlung
    );
  }

  private aktualisiereAbrechnung() {
    let sollstunden: string = this.abrechnungAuszahlungFormGroup.getRawValue().sollstunden;
    if (!sollstunden) {
      sollstunden = '0';
    }
    this.arbeitsnachweisAbrechnung.sollstunden = convertDeStringZuUsNummer(sollstunden);
    this.aktualisiereUebertraegeInAbrechnung();

    let auszahlung: string = this.abrechnungAuszahlungFormGroup.getRawValue().auszahlung;

    if (!auszahlung) {
      auszahlung = '0';
    }
    this.arbeitsnachweisAbrechnung.auszahlung = convertDeStringZuUsNummer(auszahlung);
    this.aktualisiereUebertraegeInAbrechnung();
  }
}
