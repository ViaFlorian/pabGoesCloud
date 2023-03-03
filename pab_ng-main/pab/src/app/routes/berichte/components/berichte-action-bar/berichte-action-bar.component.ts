import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ActionBarButtonKonfiguration } from '../../model/action-bar-button-konfiguration';

@Component({
  selector: 'pab-berichte-action-bar',
  templateUrl: './berichte-action-bar.component.html',
  styleUrls: ['./berichte-action-bar.component.scss'],
})
export class BerichteActionBarComponent {
  @Output()
  erstelleExcelEvent = new EventEmitter();

  @Output()
  erstellePDFEvent = new EventEmitter();

  @Output()
  versendePDFEvent = new EventEmitter();

  @Input()
  actionBarButtonKonfiguration!: ActionBarButtonKonfiguration;

  constructor() {}

  erstelleExcel() {
    this.erstelleExcelEvent.emit();
  }

  erstellePDF() {
    this.erstellePDFEvent.emit();
  }

  versendePDF() {
    this.versendePDFEvent.emit();
  }
}
