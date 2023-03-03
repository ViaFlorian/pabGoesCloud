import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Mitarbeiter } from '../../model/mitarbeiter/mitarbeiter';
import { debounceTime, map, Observable, startWith, switchMap, tap } from 'rxjs';
import { MitarbeiterAnzeigeNamePipe } from '../../pipe/mitarbeiter-anzeige-name.pipe';

@Component({
  selector: 'pab-autocomplete-mitarbeiter',
  templateUrl: './autocomplete-mitarbeiter.component.html',
  styleUrls: ['./autocomplete-mitarbeiter.component.scss'],
})
export class AutocompleteMitarbeiterComponent implements OnInit {
  @Input()
  parentFormGroup!: FormGroup;

  @Input()
  parentFormFieldName!: string;

  @Input()
  mitarbeiterAuswahl!: Observable<Mitarbeiter[]>;

  @Input()
  mitErrorPlatzhalter!: boolean;

  @Input()
  fehlermeldungenMap: Map<string, string> = new Map();

  mitarbeiterAuswahlDaten: Mitarbeiter[] = [];
  mitarbeiterAuswahlGefiltert!: Observable<Mitarbeiter[]>;

  constructor(public mitarbeiterAnzeigeNamePipe: MitarbeiterAnzeigeNamePipe) {}

  ngOnInit(): void {
    this.filtereMitarbeiterAuswahlBeiEingabe();
  }

  onVerlasseEingabefeldMitarbeiter(): void {
    const ausgewaehlterMitarbeiter = this.parentFormGroup.get(this.parentFormFieldName)?.value;
    // Mitarbeiter kann leer sein, aber die Auswahl-Liste nur ein Element beinhaltet.
    // Dadurch wird eine ungewohlte Auswahl getroffen werden. Entsprechend `return`, wenn Eingabe leer ist.
    if (typeof ausgewaehlterMitarbeiter !== 'string' || !ausgewaehlterMitarbeiter) {
      return;
    }

    const mitarbeiter: Mitarbeiter[] = this.filterMitarbeiterAuswahl(
      ausgewaehlterMitarbeiter,
      this.mitarbeiterAuswahlDaten
    );
    if (mitarbeiter.length === 1) {
      this.parentFormGroup.get(this.parentFormFieldName)?.setValue(mitarbeiter[0]);
    } else {
      this.parentFormGroup.get(this.parentFormFieldName)?.setValue({} as Mitarbeiter);
    }
  }

  erstelleFehlerMeldung(): string | undefined {
    //Methode erstellt fehlermeldung für frontend.
    // Nötig, weil bei kombination von ngIf und ngFor in html der mat-error falsch gerendert wird.
    for (const key of this.fehlermeldungenMap.keys()) {
      if (this.parentFormGroup.get(this.parentFormFieldName)?.errors?.[key]) {
        return this.fehlermeldungenMap.get(key);
      }
    }
    return 'Pflichtfeld';
  }

  private filtereMitarbeiterAuswahlBeiEingabe() {
    this.mitarbeiterAuswahlGefiltert = this.parentFormGroup.get(this.parentFormFieldName)!.valueChanges.pipe(
      debounceTime(500),
      startWith(''),
      switchMap((value: string | Mitarbeiter) => {
        return this.mitarbeiterAuswahl.pipe(
          tap((mitarbeiterAuswahl: Mitarbeiter[]) => {
            this.mitarbeiterAuswahlDaten = mitarbeiterAuswahl;
          }),
          map((mitarbeiterAuswahl: Mitarbeiter[]) => {
            const name = typeof value === 'string' ? value : this.mitarbeiterAnzeigeNamePipe.transform(value);
            const list: Mitarbeiter[] = name
              ? this.filterMitarbeiterAuswahl(name, mitarbeiterAuswahl)
              : mitarbeiterAuswahl.slice();
            return this.sortiereMitarbeiter(list.slice(0, 50));
          })
        );
      })
    );
  }

  private filterMitarbeiterAuswahl(name: string, mitarbeiterAuswahl: Mitarbeiter[]): Mitarbeiter[] {
    const filterValue = name.toLowerCase();

    return mitarbeiterAuswahl.filter((option) =>
      this.mitarbeiterAnzeigeNamePipe.transform(option).toLowerCase().includes(filterValue)
    );
  }

  private sortiereMitarbeiter(mitarbeiters: Mitarbeiter[]): Mitarbeiter[] {
    return mitarbeiters.sort((a, b): number => {
      return a.nachname.localeCompare(b.nachname);
    });
  }
}
