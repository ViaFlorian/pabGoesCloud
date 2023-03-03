import { Injectable } from '@angular/core';
import {
  ArbeitsnachweisBearbeitenAnwImportData,
  ArbeitsnachweisBearbeitenAnwImportDialogComponent,
} from '../../routes/arbeitsnachweise/components/arbeitsnachweis-bearbeiten-anw-import-dialog/arbeitsnachweis-bearbeiten-anw-import-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { SpinnerOverlayService } from './spinner-overlay.service';
import { ArbeitsnachweisService } from './arbeitsnachweis.service';
import { Fehlerlog } from '../model/arbeitsnachweis/fehlerlog';
import {
  ArbeitsnachweisBearbeitenUploadFehlerlogDialogComponent,
  ArbeitsnachweisBearbeitenUploadFehlerlogDialogData,
} from '../../routes/arbeitsnachweise/components/arbeitsnachweis-bearbeiten-upload-fehlerlog-dialog/arbeitsnachweis-bearbeiten-upload-fehlerlog-dialog.component';

@Injectable({
  providedIn: 'root',
})
export class ArbeitsnachweisValidatorService {
  constructor(
    public dialog: MatDialog,
    private spinnerOverlayService: SpinnerOverlayService,
    private arbeitsnachweisService: ArbeitsnachweisService
  ) {}

  oeffneValidierungsDialog() {
    const dataAnwImport: ArbeitsnachweisBearbeitenAnwImportData = {
      validierungsModus: true,
    };
    const anwImportDialogRef = this.dialog.open(ArbeitsnachweisBearbeitenAnwImportDialogComponent, {
      data: dataAnwImport,
      panelClass: 'dialog-container-small',
    });

    anwImportDialogRef.componentInstance.anwHochgeladenEvent.subscribe((ausgewaehlterANW: File) => {
      const dataSpinner = {
        title: 'Arbeitsnachweis wird verarbeitet...',
      };
      const spinnerOverlay = this.spinnerOverlayService.open(dataSpinner);
      this.arbeitsnachweisService.validiereArbeitsnachweis(ausgewaehlterANW).subscribe((fehlerlog) => {
        this.oeffneFehlerlogDialog(fehlerlog);
        anwImportDialogRef.close();
        spinnerOverlay.close();
      });
    });
  }

  private oeffneFehlerlogDialog(fehlerlog: Fehlerlog[]) {
    const data: ArbeitsnachweisBearbeitenUploadFehlerlogDialogData = {
      fehlerlog: fehlerlog,
    };

    this.dialog.open(ArbeitsnachweisBearbeitenUploadFehlerlogDialogComponent, {
      data,
      panelClass: 'dialog-container-default',
    });
  }
}
