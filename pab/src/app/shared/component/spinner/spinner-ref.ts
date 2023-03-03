export class SpinnerRef {
  private closed: boolean = true;
  private startzeitpunkt?: number;

  open() {
    if (!this.closed) {
      return;
    }
    this.closed = false;
    this.startzeitpunkt = Date.now();
  }

  close(): void {
    const endzeitpunkt: number = Date.now();
    const zeitraum: number = endzeitpunkt - this.startzeitpunkt!;
    if (zeitraum >= 1000) {
      this.closed = true;
    } else {
      setTimeout(() => {
        this.closed = true;
      }, 1000 - zeitraum);
    }
  }

  isClosed(): boolean {
    return this.closed;
  }
}
