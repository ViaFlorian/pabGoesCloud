import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SpeichernPostAktionEnum } from '../../../../shared/enum/speichern-post-aktion.enum';

@Component({
  selector: 'pab-skonto-buchung-action-bar',
  templateUrl: './skonto-buchung-action-bar.component.html',
  styleUrls: ['./skonto-buchung-action-bar.component.scss'],
})
export class SkontoBuchungActionBarComponent {
  @Input()
  aenderung!: boolean;

  @Output()
  speichernEvent = new EventEmitter<SpeichernPostAktionEnum>();

  speichern(): void {
    this.speichernEvent.emit(SpeichernPostAktionEnum.KEINE);
  }

  speichernUndSchliessen(): void {
    this.speichernEvent.emit(SpeichernPostAktionEnum.SCHLIESSEN);
  }
}
