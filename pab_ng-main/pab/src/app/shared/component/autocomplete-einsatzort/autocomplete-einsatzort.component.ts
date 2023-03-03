import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { debounceTime, map, Observable, startWith } from 'rxjs';

@Component({
  selector: 'pab-autocomplete-einsatzort',
  templateUrl: './autocomplete-einsatzort.component.html',
  styleUrls: ['./autocomplete-einsatzort.component.scss'],
})
export class AutocompleteEinsatzortComponent implements OnInit, OnChanges {
  @Input()
  parentFormGroup!: FormGroup;

  @Input()
  parentFormFieldName!: string;

  @Input()
  einsatzortAuswahl!: string[];

  einsatzortAuswahlGefiltert!: Observable<string[]>;

  ngOnInit(): void {
    this.filterEinsatzortAuswahlBeiEingabe();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['einsatzortAuswahl']) {
      const einsatzortAuswahlChange = changes['einsatzortAuswahl'];
      if (
        einsatzortAuswahlChange.currentValue &&
        einsatzortAuswahlChange.previousValue &&
        einsatzortAuswahlChange.currentValue.length !== einsatzortAuswahlChange.previousValue.length
      ) {
        this.parentFormGroup
          .get(this.parentFormFieldName)
          ?.setValue(this.parentFormGroup.get(this.parentFormFieldName)?.value);
      }
    }
  }

  zeigeEinsatzortAuswahlWarnung() {
    const value = this.parentFormGroup.get(this.parentFormFieldName)!.value;
    return value !== '' && !this.einsatzortAuswahl.includes(value);
  }

  private filterEinsatzortAuswahlBeiEingabe() {
    this.einsatzortAuswahlGefiltert = this.parentFormGroup.get(this.parentFormFieldName)!.valueChanges.pipe(
      debounceTime(500),
      startWith(''),
      map((value: string) => {
        const list: string[] = value ? this.filterEinsatzortAuswahl(value) : this.einsatzortAuswahl.slice();
        return this.sortiereEinsatzorte(list.slice(0, 50));
      })
    );
  }

  private filterEinsatzortAuswahl(name: string): string[] {
    const filterValue = name.toLowerCase();
    return this.einsatzortAuswahl.filter((option) => option.toLowerCase().includes(filterValue));
  }

  private sortiereEinsatzorte(einsatzorte: string[]): string[] {
    return einsatzorte.sort((a, b): number => {
      return a.localeCompare(b);
    });
  }
}
