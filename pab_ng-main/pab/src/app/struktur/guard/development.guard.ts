import { Injectable } from '@angular/core';
import { CanMatch } from '@angular/router';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class DevelopmentGuard implements CanMatch {
  canMatch(): boolean {
    return !environment.production;
  }
}
