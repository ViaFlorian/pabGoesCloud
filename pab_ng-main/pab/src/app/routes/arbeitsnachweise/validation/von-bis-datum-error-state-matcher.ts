import { ErrorStateMatcher } from '@angular/material/core';
import { AbstractControl } from '@angular/forms';

export class VonBisDatumErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: AbstractControl | null): boolean {
    if (control?.invalid && control.touched) {
      return true;
    } else if (
      control?.parent?.errors?.['zeitVonNachZeitBis'] ||
      control?.parent?.errors?.['tagVonNachTagBis'] ||
      control?.parent?.errors?.['zeitUeber2400'] ||
      control?.parent?.errors?.['datumKollidiert']
    ) {
      return true;
    }
    return false;
  }
}
