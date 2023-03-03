import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SpeichernPostAktionEnum } from '../../../../shared/enum/speichern-post-aktion.enum';

@Component({
  selector: 'pab-fakturuebersicht-action-bar',
  templateUrl: './fakturuebersicht-action-bar.component.html',
  styleUrls: ['./fakturuebersicht-action-bar.component.scss'],
})
export class FakturuebersichtActionBarComponent {
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
