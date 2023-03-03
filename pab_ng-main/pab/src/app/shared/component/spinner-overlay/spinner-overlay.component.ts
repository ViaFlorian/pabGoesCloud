import { Component, Inject } from '@angular/core';

import { spinnerOverlayTitle } from './spinner-overlay-tokens';
import { SpinnerOverlayRef } from './spinner-overlay-ref';

@Component({
  selector: 'pab-spinner-overlay',
  templateUrl: './spinner-overlay.component.html',
  styleUrls: ['./spinner-overlay.component.scss'],
})
export class SpinnerOverlayComponent {
  constructor(public dialogRef: SpinnerOverlayRef, @Inject(spinnerOverlayTitle) public title: string) {}
}
