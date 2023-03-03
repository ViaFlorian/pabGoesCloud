import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'pab-info-dialog',
  templateUrl: './info-dialog.component.html',
  styleUrls: ['./info-dialog.component.scss'],
})
export class InfoDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: InfoDialogData,
    public dialogRef: MatDialogRef<InfoDialogComponent>
  ) {}
}

export interface InfoDialogData {
  title: string;
  text: string;
}
