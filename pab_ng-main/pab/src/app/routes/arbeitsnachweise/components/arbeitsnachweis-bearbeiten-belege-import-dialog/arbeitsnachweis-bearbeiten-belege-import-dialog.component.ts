import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'pab-arbeitsnachweis-bearbeiten-belege-import-dialog',
  templateUrl: './arbeitsnachweis-bearbeiten-belege-import-dialog.component.html',
  styleUrls: ['./arbeitsnachweis-bearbeiten-belege-import-dialog.component.scss'],
})
export class ArbeitsnachweisBearbeitenBelegeImportDialogComponent {
  @Output()
  belegHochgeladenEvent = new EventEmitter<File>();
  dateiName: string = '';

  constructor() {}

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
    this.belegHochgeladenEvent.emit(datei);
  }
}
