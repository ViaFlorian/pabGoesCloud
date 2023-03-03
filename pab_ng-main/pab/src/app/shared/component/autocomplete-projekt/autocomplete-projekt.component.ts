import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Projekt } from '../../model/projekt/projekt';
import { debounceTime, map, Observable, startWith } from 'rxjs';
import { ProjekteAnzeigeNamePipe } from '../../pipe/projekte-anzeige-name.pipe';

@Component({
  selector: 'pab-autocomplete-projekt',
  templateUrl: './autocomplete-projekt.component.html',
  styleUrls: ['./autocomplete-projekt.component.scss'],
})
export class AutocompleteProjektComponent implements OnInit, OnChanges {
  @Input()
  parentFormGroup!: FormGroup;

  @Input()
  parentFormFieldName!: string;

  @Input()
  projektAuswahl!: Projekt[];

  @Input()
  mitErrorPlatzhalter!: boolean;

  projektAuswahlGefiltert!: Observable<Projekt[]>;

  constructor(public projektAnzeigeNamePipe: ProjekteAnzeigeNamePipe) {}

  ngOnInit(): void {
    this.filtereProjektAuswahlBeiEingabe();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['projektAuswahl']) {
      const projektAuswahlChange = changes['projektAuswahl'];
      if (
        projektAuswahlChange.currentValue &&
        projektAuswahlChange.previousValue &&
        projektAuswahlChange.currentValue.length !== projektAuswahlChange.previousValue.length
      ) {
        this.parentFormGroup
          .get(this.parentFormFieldName)
          ?.setValue(this.parentFormGroup.get(this.parentFormFieldName)?.value);
      }
    }
  }

  onVerlasseEingabefeldProjekt() {
    const ausgewaehltesProjekt = this.parentFormGroup.get(this.parentFormFieldName)?.value;
    // Nichts zu tuen, wenn Typ Projekt oder leerer String
    if (typeof ausgewaehltesProjekt !== 'string' || !ausgewaehltesProjekt) {
      return;
    }

    const projekt: Projekt[] = this.filterProjektAuswahl(ausgewaehltesProjekt);
    if (projekt.length === 1) {
      this.parentFormGroup.get(this.parentFormFieldName)?.setValue(projekt[0]);
    } else {
      this.parentFormGroup.get(this.parentFormFieldName)?.setValue({} as Projekt);
    }
  }

  private filtereProjektAuswahlBeiEingabe() {
    this.projektAuswahlGefiltert = this.parentFormGroup.get(this.parentFormFieldName)!.valueChanges.pipe(
      debounceTime(500),
      startWith(''),
      map((value: string | Projekt) => {
        const name = typeof value === 'string' ? value : this.projektAnzeigeNamePipe.transform(value);
        const list: Projekt[] = name ? this.filterProjektAuswahl(name) : this.projektAuswahl.slice();
        return this.sortiereProjekte(list.slice(0, 50));
      })
    );
  }

  private filterProjektAuswahl(name: string): Projekt[] {
    const filterValue = name.toLowerCase();

    return this.projektAuswahl.filter((option) =>
      this.projektAnzeigeNamePipe.transform(option).toLowerCase().includes(filterValue)
    );
  }

  private sortiereProjekte(projekte: Projekt[]): Projekt[] {
    return projekte.sort((a, b): number => {
      return a.projektnummer.localeCompare(b.projektnummer);
    });
  }
}
