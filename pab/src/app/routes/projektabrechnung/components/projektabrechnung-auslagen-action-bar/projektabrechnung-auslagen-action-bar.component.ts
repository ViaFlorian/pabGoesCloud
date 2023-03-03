import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NavigationService } from '../../../../shared/service/navigation.service';

@Component({
  selector: 'pab-projektabrechnung-auslagen-action-bar',
  templateUrl: './projektabrechnung-auslagen-action-bar.component.html',
  styleUrls: ['./projektabrechnung-auslagen-action-bar.component.scss'],
})
export class ProjektabrechnungAuslagenActionBarComponent {
  @Output()
  speichernEvent = new EventEmitter<any>();

  @Input()
  istAenderungVorhanden!: boolean;

  constructor(private navigationService: NavigationService) {}

  speichern() {
    this.speichernEvent.emit();
  }

  speichernUndSchliessen() {
    this.speichern();
    this.navigationService.geheZurueck();
  }
}
