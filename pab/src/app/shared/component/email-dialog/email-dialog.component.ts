import { Component, EventEmitter, Inject, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MitarbeiterQuittungEmailDaten } from '../../model/arbeitsnachweis/mitarbeiter-quittung-email-daten';

@Component({
  selector: 'pab-email-dialog',
  templateUrl: './email-dialog.component.html',
  styleUrls: ['./email-dialog.component.scss'],
})
export class EmailDialogComponent {
  @Output()
  sendeEmailEvent = new EventEmitter<MitarbeiterQuittungEmailDaten>();
  @Output()
  oeffnePdfAnhangEvent = new EventEmitter();

  emailFormGroup;

  ausgewaehlteEmailAdressen: string[] = [];

  constructor(
    private fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: EmailDialogData,
    public dialogRef: MatDialogRef<EmailDialogComponent>
  ) {
    this.emailFormGroup = this.fb.nonNullable.group({
      empfaenger: [
        {
          value: this.data.empfanger,
          disabled: !this.data.empfangerBearbeitbar,
        },
      ],
      betreff: [this.data.betreff],
      nachricht: [this.data.nachricht],
    });
  }

  sendeEmail() {
    this.sendeEmailEvent.emit(this.getEmailDatenAusForm());
    this.dialogRef.close();
  }

  oeffnePdfAnhang() {
    this.oeffnePdfAnhangEvent.emit();
  }

  private getEmailDatenAusForm(): MitarbeiterQuittungEmailDaten {
    return {
      emailAdressen: this.data.empfangerBearbeitbar
        ? this.ausgewaehlteEmailAdressen
        : [this.emailFormGroup.getRawValue().empfaenger],
      betreff: this.emailFormGroup.getRawValue().betreff,
      nachricht: this.emailFormGroup.getRawValue().nachricht,
      dateiname: this.data.dateiname,
    };
  }
}

export interface EmailDialogData {
  empfanger: string;
  empfangerBearbeitbar: boolean;
  mitarbeiterEmails: string[];
  betreff: string;
  nachricht: string;
  dateiname: string;
  abbruchButtonText: string;
  sumbmitButtonText: string;
}
