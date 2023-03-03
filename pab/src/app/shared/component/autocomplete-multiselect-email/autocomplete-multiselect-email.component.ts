import { Component, ElementRef, Input, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { map, Observable, startWith } from 'rxjs';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { MatChipInputEvent } from '@angular/material/chips';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { BenachrichtigungService } from '../../service/benachrichtigung.service';

@Component({
  selector: 'pab-autocomplete-multiselect-email',
  templateUrl: './autocomplete-multiselect-email.component.html',
  styleUrls: ['./autocomplete-multiselect-email.component.scss'],
})
export class AutocompleteMultiselectEmailComponent {
  @Input()
  emailAdressenAuswahl!: string[];

  @Input()
  ausgewaehlteEmailAdressen!: string[];

  formGroup!: FormGroup;

  gefilterteEmailAdressen: Observable<string[]>;

  separatorKeysCodes: number[] = [ENTER, COMMA];

  @ViewChild('emailInput') emailInput!: ElementRef<HTMLInputElement>;

  constructor(private benachrichtigungsService: BenachrichtigungService, private fb: FormBuilder) {
    this.formGroup = this.fb.nonNullable.group({
      email: [''],
    });
    this.gefilterteEmailAdressen = this.formGroup.get('email')!.valueChanges.pipe(
      startWith(''),
      map((emailAdresse: string | null) => {
        const list: string[] = emailAdresse
          ? this.filterEmailAdressen(emailAdresse)
          : this.emailAdressenAuswahl.slice();
        return list.sort().slice(0, 50);
      })
    );
  }

  hinzufuegen($event: MatChipInputEvent): void {
    const value = ($event.value || '').trim();

    if (value) {
      if (!this.emailAdressenAuswahl.includes(value)) {
        this.benachrichtigungsService.erstelleWarnung('Nur Email-Adressen aus der Liste können ausgewählt werden');
        return;
      }
      this.ausgewaehlteEmailAdressen.push(value);
    }

    // Clear the input value
    $event.chipInput!.clear();

    this.formGroup.get('email')!.setValue(null);
  }

  entfernen(emailAdresse: string): void {
    const index = this.ausgewaehlteEmailAdressen.indexOf(emailAdresse);

    if (index >= 0) {
      this.ausgewaehlteEmailAdressen.splice(index, 1);
    }
  }

  waehleEmail($event: MatAutocompleteSelectedEvent): void {
    this.ausgewaehlteEmailAdressen.push($event.option.viewValue);
    this.emailInput.nativeElement.value = '';
    this.formGroup.get('email')!.setValue(null);
  }

  private filterEmailAdressen(value: string): string[] {
    const filterWert = value.toLowerCase();

    return this.emailAdressenAuswahl.filter((emailAdresse) => emailAdresse.toLowerCase().includes(filterWert));
  }
}
