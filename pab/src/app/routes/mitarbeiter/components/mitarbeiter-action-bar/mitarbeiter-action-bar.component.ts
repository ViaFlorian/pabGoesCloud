import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SpeichernPostAktionEnum } from '../../../../shared/enum/speichern-post-aktion.enum';

@Component({
  selector: 'pab-mitarbeiter-action-bar',
  templateUrl: './mitarbeiter-action-bar.component.html',
  styleUrls: ['./mitarbeiter-action-bar.component.css'],
})
export class MitarbeiterActionBarComponent {
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
