import { Component } from '@angular/core';
import { NavigationService } from '../../service/navigation.service';

@Component({
  selector: 'pab-close-button',
  templateUrl: './close-button.component.html',
  styleUrls: ['./close-button.component.scss'],
})
export class CloseButtonComponent {
  constructor(private navigationService: NavigationService) {}

  goBack(): void {
    this.navigationService.geheZurueck();
  }
}
