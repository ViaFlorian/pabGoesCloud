import { ComponentRef, Injectable, Injector } from '@angular/core';
import { Overlay, OverlayConfig, OverlayRef } from '@angular/cdk/overlay';
import { ComponentPortal } from '@angular/cdk/portal';
import { SpinnerOverlayRef } from '../component/spinner-overlay/spinner-overlay-ref';
import { SpinnerOverlayComponent } from '../component/spinner-overlay/spinner-overlay.component';
import { spinnerOverlayTitle } from '../component/spinner-overlay/spinner-overlay-tokens';

export interface SpinnerOverlayConfig {
  title: string;
}

@Injectable({
  providedIn: 'root',
})
export class SpinnerOverlayService {
  private aktuellerSpinner: SpinnerOverlayRef | undefined;

  constructor(private injector: Injector, private overlay: Overlay) {}

  open(config: SpinnerOverlayConfig): SpinnerOverlayRef {
    const overlayConfig = this.createOverlayConfig();
    const overlayRef = this.createOverlay(overlayConfig);
    const spinnerRef = new SpinnerOverlayRef(overlayRef);

    if (!this.aktuellerSpinner || this.aktuellerSpinner.isClosed()) {
      this.aktuellerSpinner = spinnerRef;
      this.attachDialogContainer(overlayRef, config, spinnerRef);
    }

    return spinnerRef;
  }

  private createOverlayConfig(): OverlayConfig {
    return new OverlayConfig({
      hasBackdrop: true,
      backdropClass: 'spinner-backdrop',
      panelClass: 'spinner-panel',
      scrollStrategy: this.overlay.scrollStrategies.block(),
      positionStrategy: this.overlay.position().global().centerHorizontally().centerVertically(),
    });
  }

  private createOverlay(overlayConfig: OverlayConfig): OverlayRef {
    return this.overlay.create(overlayConfig);
  }

  private attachDialogContainer(
    overlayRef: OverlayRef,
    config: SpinnerOverlayConfig,
    dialogRef: SpinnerOverlayRef
  ): SpinnerOverlayComponent {
    const injector = this.createInjector(config, dialogRef);

    const containerPortal = new ComponentPortal(SpinnerOverlayComponent, null, injector);
    const containerRef: ComponentRef<SpinnerOverlayComponent> = overlayRef.attach(containerPortal);

    return containerRef.instance;
  }

  private createInjector(config: SpinnerOverlayConfig, dialogRef: SpinnerOverlayRef): Injector {
    return Injector.create({
      providers: [
        { provide: SpinnerOverlayRef, useValue: dialogRef },
        { provide: spinnerOverlayTitle, useValue: config.title },
      ],
    });
  }
}
