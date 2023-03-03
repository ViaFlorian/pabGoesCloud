import { Component, Input } from '@angular/core';

@Component({
  selector: 'pab-headline',
  templateUrl: './headline.component.html',
  styleUrls: ['./headline.component.scss'],
})
export class HeadlineComponent {
  @Input()
  text = '';
}
