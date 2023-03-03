import { Component } from '@angular/core';
import { NavigationService } from '../../../../shared/service/navigation.service';

@Component({
  selector: 'pab-projektabrechnung-uebersicht-action-bar',
  templateUrl: './projektabrechnung-uebersicht-action-bar.component.html',
  styleUrls: ['./projektabrechnung-uebersicht-action-bar.component.scss'],
})
export class ProjektabrechnungUebersichtActionBarComponent {
  constructor(private navigationService: NavigationService) {}

  navigiereZu(url: string): void {
    this.navigationService.navigiereZu(url, false);
  }
}
