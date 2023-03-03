import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'pab-projektabrechnung-bearbeiten-tab-fakturfaehige-leistung',
  templateUrl: './projektabrechnung-bearbeiten-tab-fakturfaehige-leistung.component.html',
  styleUrls: ['./projektabrechnung-bearbeiten-tab-fakturfaehige-leistung.component.scss'],
})
export class ProjektabrechnungBearbeitenTabFakturfaehigeLeistungComponent implements OnInit {
  @Input()
  pabFakturfaehigeLeistungFormGroup!: FormGroup;

  ngOnInit(): void {
    this.pabFakturfaehigeLeistungFormGroup.disable();
  }
}
