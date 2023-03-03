import { Component } from '@angular/core';
import { NavigationService } from '../../../../shared/service/navigation.service';

@Component({
  selector: 'pab-arbeitsnachweis-uebersicht-action-bar',
  templateUrl: './arbeitsnachweis-uebersicht-action-bar.component.html',
  styleUrls: ['./arbeitsnachweis-uebersicht-action-bar.component.scss'],
})
export class ArbeitsnachweisUebersichtActionBarComponent {
  constructor(private navigationService: NavigationService) {}

  navigiereZu(url: string): void {
    this.navigationService.navigiereZu(url, false);
  }
}
