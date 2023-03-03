import { OverlayRef } from '@angular/cdk/overlay';

export class SpinnerOverlayRef {
  private closed = false;
  private startzeitpunkt: number;

  constructor(private overlayRef: OverlayRef) {
    this.startzeitpunkt = Date.now();
  }

  close(): void {
    const endzeitpunkt: number = Date.now();
    const zeitraum: number = endzeitpunkt - this.startzeitpunkt;
    if (zeitraum >= 1000) {
      this.stoppeSpinner();
    } else {
      setTimeout(() => {
        this.stoppeSpinner();
      }, 1000 - zeitraum);
    }
  }

  isClosed(): boolean {
    return this.closed;
  }

  private stoppeSpinner(): void {
    this.overlayRef.dispose();
    this.closed = true;
  }
}
