import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SpeichernPostAktionEnum } from '../../../../shared/enum/speichern-post-aktion.enum';

@Component({
  selector: 'pab-projektbudget-action-bar',
  templateUrl: './projektbudget-action-bar.component.html',
  styleUrls: ['./projektbudget-action-bar.component.scss'],
})
export class ProjektbudgetActionBarComponent {
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
