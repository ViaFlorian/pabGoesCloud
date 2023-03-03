import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'pab-suche',
  templateUrl: './suche.component.html',
  styleUrls: ['./suche.component.scss'],
})
export class SucheComponent {
  sucheFormGroup: FormGroup;

  @Output()
  sucheEvent = new EventEmitter<string>();

  constructor(private fb: FormBuilder) {
    this.sucheFormGroup = this.fb.nonNullable.group({
      suchbegriff: ['', []],
    });
  }

  reagiereAufSucheEingabe() {
    this.sucheEvent.emit(this.sucheFormGroup.get('suchbegriff')?.value);
  }

  suchbegriffZuruecksetzen() {
    this.sucheFormGroup.get('suchbegriff')?.setValue('');
    this.sucheEvent.emit();
  }
}
