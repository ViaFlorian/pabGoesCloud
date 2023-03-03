import { ErrorStateMatcher } from '@angular/material/core';
import { AbstractControl } from '@angular/forms';

export class BearbeitenProjektzeitGesamtstundenErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: AbstractControl | null): boolean {
    if (control?.invalid && control.touched) {
      return true;
    } else if (
      control?.parent?.errors?.['bearbeitenProjektzeitGesamtstundenUnterschritten'] ||
      control?.parent?.errors?.['bearbeitenProjektzeitGesamtstundenUeberschritten']
    ) {
      return true;
    }
    return false;
  }
}
