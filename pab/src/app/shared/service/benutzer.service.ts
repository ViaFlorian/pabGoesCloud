import { Injectable } from '@angular/core';
import { MsalBroadcastService, MsalService } from '@azure/msal-angular';
import { filter, Observable, Subject, takeUntil } from 'rxjs';
import { InteractionStatus } from '@azure/msal-browser';

@Injectable({
  providedIn: 'root',
})
export class BenutzerService {
  private readonly _destroying$ = new Subject<void>();

  constructor(private authService: MsalService, private broadcastService: MsalBroadcastService) {}

  istBenutzerEingeloggt(): boolean {
    return this.getClaimByKey('roles') !== '';
  }

  getBenutzerNamen(): string {
    return this.getClaimByKey('name');
  }

  getBenutzerRollen(): string[] {
    return this.getClaimByKey('roles');
  }

  hatRolleAdministrator(): boolean {
    return this.getBenutzerRollen().includes('PAB-ADMINISTRATOR');
  }

  hatRolleOeLeiter(): boolean {
    return this.getBenutzerRollen().includes('PAB-OELEITER');
  }

  hatRolleGF(): boolean {
    return this.getBenutzerRollen().includes('PAB-GF');
  }

  hatRollePersonalvorgesetzter(): boolean {
    return this.getBenutzerRollen().includes('PAB-PERSONALVORGESETZTER');
  }

  hatRolleSachbearbeiter(): boolean {
    return this.getBenutzerRollen().includes('PAB-SACHBEARBEITER');
  }

  erzeugeBenutzerLoginListener(): Observable<InteractionStatus> {
    return this.broadcastService.inProgress$.pipe(
      filter((status: InteractionStatus) => status === InteractionStatus.None),
      takeUntil(this._destroying$)
    );
  }

  private getClaimByKey(key: string): any {
    const eingeloggterUser = this.authService.instance.getAllAccounts()[0];
    if (eingeloggterUser) {
      return eingeloggterUser.idTokenClaims ? eingeloggterUser.idTokenClaims[key] : '';
    }
    return eingeloggterUser ? eingeloggterUser[key] : '';
  }
}
