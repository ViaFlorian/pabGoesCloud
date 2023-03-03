import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NavigationService } from '../../../../shared/service/navigation.service';

@Component({
  selector: 'pab-projektabrechnung-korrekturbuchung-action-bar',
  templateUrl: './projektabrechnung-korrekturbuchung-action-bar.component.html',
  styleUrls: ['./projektabrechnung-korrekturbuchung-action-bar.component.scss'],
})
export class ProjektabrechnungKorrekturbuchungActionBarComponent {
  @Output()
  speichernEvent = new EventEmitter<any>();

  @Input()
  istListeVeraendert!: boolean;

  constructor(private navigationService: NavigationService) {}

  speichern() {
    this.speichernEvent.emit();
  }

  speichernUndSchliessen() {
    // this.speichern();
    // this.navigationService.geheZurueck();
  }
}
