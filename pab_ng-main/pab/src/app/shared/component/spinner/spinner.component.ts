import { Component, Input } from '@angular/core';

@Component({
  selector: 'pab-spinner',
  templateUrl: './spinner.component.html',
  styleUrls: ['./spinner.component.scss'],
})
export class SpinnerComponent {
  @Input()
  spinnerHeight!: string;

  @Input()
  spinnerWidth!: string;
}
