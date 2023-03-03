import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { debounceTime, map, Observable, startWith } from 'rxjs';
import { Projekt } from '../../model/projekt/projekt';
import { KundeAnzeigeNamePipe } from '../../pipe/kunde-anzeige-name.pipe';
import { Kunde } from '../../model/kunde/kunde';

@Component({
  selector: 'pab-autocomplete-kunde',
  templateUrl: './autocomplete-kunde.component.html',
  styleUrls: ['./autocomplete-kunde.component.scss'],
})
export class AutocompleteKundeComponent implements OnInit, OnChanges {
  @Input()
  parentFormGroup!: FormGroup;

  @Input()
  parentFormFieldName!: string;

  @Input()
  kundeAuswahl!: Kunde[];

  @Input()
  label!: string;

  kundeAuswahlGefiltert!: Observable<Kunde[]>;

  constructor(public kundeAnzeigeNamePipe: KundeAnzeigeNamePipe) {}

  ngOnInit(): void {
    this.filtereKundenAuswahlBeiEingabe();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['kundeAuswahl']) {
      const kundeAuswahlChange = changes['kundeAuswahl'];
      if (
        kundeAuswahlChange.currentValue &&
        kundeAuswahlChange.previousValue &&
        kundeAuswahlChange.currentValue.length !== kundeAuswahlChange.previousValue.length
      ) {
        this.parentFormGroup
          .get(this.parentFormFieldName)
          ?.setValue(this.parentFormGroup.get(this.parentFormFieldName)?.value);
      }
    }
  }

  onVerlasseEingabefeldKunde() {
    const ausgewaehlterKunde = this.parentFormGroup.get(this.parentFormFieldName)?.value;
    if (!ausgewaehlterKunde || typeof ausgewaehlterKunde !== 'string') {
      return;
    }

    const kunde: Kunde[] = this.filterKundeAuswahl(ausgewaehlterKunde);
    if (kunde.length === 1) {
      this.parentFormGroup.get(this.parentFormFieldName)?.setValue(kunde[0]);
    } else {
      this.parentFormGroup.get(this.parentFormFieldName)?.setValue({} as Kunde);
    }
  }

  private filtereKundenAuswahlBeiEingabe() {
    this.kundeAuswahlGefiltert = this.parentFormGroup.get(this.parentFormFieldName)!.valueChanges.pipe(
      debounceTime(500),
      startWith(''),
      map((value: string | Projekt) => {
        const name = typeof value === 'string' ? value : value.bezeichnung;
        const list: Kunde[] = name ? this.filterKundeAuswahl(name) : this.kundeAuswahl.slice();
        return list.slice(0, 50);
      })
    );
  }

  private filterKundeAuswahl(name: string): Kunde[] {
    const filterValue = name.toLowerCase();
    return this.kundeAuswahl.filter((option) => option.bezeichnung.toLowerCase().includes(filterValue));
  }
}
