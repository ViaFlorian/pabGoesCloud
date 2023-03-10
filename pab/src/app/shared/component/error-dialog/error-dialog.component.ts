import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'pab-error-dialog',
  templateUrl: './error-dialog.component.html',
  styleUrls: ['./error-dialog.component.scss'],
})
export class ErrorDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: ErrorDialogData,
    public dialogRef: MatDialogRef<ErrorDialogComponent>
  ) {}
}

export interface ErrorDialogData {
  title: string;
  text: string;
}
