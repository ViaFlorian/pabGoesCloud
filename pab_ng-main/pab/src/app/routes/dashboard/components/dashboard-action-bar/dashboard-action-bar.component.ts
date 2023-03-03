import { Component } from '@angular/core';
import { NavigationService } from '../../../../shared/service/navigation.service';

@Component({
  selector: 'pab-dashboard-action-bar',
  templateUrl: './dashboard-action-bar.component.html',
  styleUrls: ['./dashboard-action-bar.component.scss'],
})
export class DashboardActionBarComponent {
  constructor(private navigationService: NavigationService) {}

  navigiereZu(url: string): void {
    this.navigationService.navigiereZu(url, false);
  }
}
