import { Component, Inject } from '@angular/core';
import { MSAL_GUARD_CONFIG, MsalGuardConfiguration } from '@azure/msal-angular';
import { Subject } from 'rxjs';

@Component({
  selector: 'pab-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  title = 'pab';
  private readonly _destroying$ = new Subject<void>();

  constructor(@Inject(MSAL_GUARD_CONFIG) private msalGuardConfig: MsalGuardConfiguration) {}
}
