import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class IdGeneratorService {
  private letzteVergebeneId = 0;

  public generiereId(): string {
    this.letzteVergebeneId = this.letzteVergebeneId - 1;
    return this.letzteVergebeneId.toString();
  }
}
