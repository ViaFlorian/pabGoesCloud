import { Component, EventEmitter, Inject, Output } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'pab-arbeitsnachweis-bearbeiten-anw-import-dialog',
  templateUrl: './arbeitsnachweis-bearbeiten-anw-import-dialog.component.html',
  styleUrls: ['./arbeitsnachweis-bearbeiten-anw-import-dialog.component.scss'],
})
export class ArbeitsnachweisBearbeitenAnwImportDialogComponent {
  @Output()
  anwHochgeladenEvent = new EventEmitter<File>();

  akzeptierterDateiTyp: string = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
  dateiName: string = '';

  constructor(@Inject(MAT_DIALOG_DATA) public data: ArbeitsnachweisBearbeitenAnwImportData) {}

  onFileAusgewaehlt(fileAuswahlEvent: Event) {
    const inputTarget = fileAuswahlEvent.target as HTMLInputElement;

    if (!inputTarget || !inputTarget.files) {
      return;
    }
    const datei: File = inputTarget.files[0];

    if (datei) {
      this.verarbeiteDatei(datei);
    }
  }

  verarbeiteDatei(datei: File) {
    this.dateiName = datei.name;
    this.anwHochgeladenEvent.emit(datei);
  }
}

export interface ArbeitsnachweisBearbeitenAnwImportData {
  validierungsModus: boolean;
}
