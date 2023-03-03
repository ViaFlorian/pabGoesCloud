import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ErrorDialogComponent, ErrorDialogData } from '../component/error-dialog/error-dialog.component';
import { InfoDialogComponent, InfoDialogData } from '../component/info-dialog/info-dialog.component';
import { ConfirmDialogComponent, ConfirmDialogData } from '../component/confirm-dialog/confirm-dialog.component';

@Injectable({
  providedIn: 'root',
})
export class BenachrichtigungService {
  private errorDialogRef: MatDialogRef<ErrorDialogComponent, any> | undefined = undefined;

  constructor(private snackBar: MatSnackBar, private dialog: MatDialog) {}

  erstelleBenachrichtigung(...nachrichten: string[]): void {
    const nachricht = nachrichten.length > 1 ? nachrichten.join('\n\n ') : nachrichten[0];
    const zusatzZeit = 1000 * (nachrichten.length - 1);
    this.snackBar.open(nachricht, '', {
      politeness: 'assertive',
      duration: 3000 + zusatzZeit,
      panelClass: ['multiline-snackbar'],
    });
  }

  erstelleWarnung(...warnungen: string[]) {
    if (!warnungen || warnungen.length === 0) {
      return;
    }

    const warnung = warnungen.length > 1 ? warnungen.join('\n\n ') : warnungen[0];
    const zusatzZeit = 1000 * (warnungen.length - 1);
    this.snackBar.open(warnung, 'Schließen', {
      duration: 5000 + zusatzZeit,
      panelClass: ['multiline-snackbar'],
    });
  }

  erstelleErrorMessage(nachricht: string): MatDialogRef<ErrorDialogComponent, any> {
    if (this.errorDialogRef !== undefined) {
      return this.errorDialogRef;
    }

    const data: ErrorDialogData = {
      title: 'Fehler',
      text: nachricht,
    };
    const dialogRef = this.dialog.open(ErrorDialogComponent, {
      data,
      panelClass: 'dialog-container-small',
    });
    this.errorDialogRef = dialogRef;
    dialogRef.afterClosed().subscribe(() => {
      this.errorDialogRef = undefined;
    });
    return dialogRef;
  }

  erstelleInfoMessage(nachricht: string, titel: string = 'Information'): MatDialogRef<InfoDialogComponent, any> {
    const data: InfoDialogData = {
      title: titel,
      text: nachricht,
    };
    return this.dialog.open(InfoDialogComponent, {
      data,
      panelClass: 'dialog-container-small',
    });
  }

  erstelleBestaetigungMessage(nachricht: string, titel: string): MatDialogRef<ConfirmDialogComponent, any> {
    const data: ConfirmDialogData = {
      title: titel,
      text: nachricht,
    };
    return this.dialog.open(ConfirmDialogComponent, {
      data,
      panelClass: 'dialog-container-small',
    });
  }
}
