import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from './material/material.module';
import { ConfirmDialogComponent } from './component/confirm-dialog/confirm-dialog.component';
import { SpinnerOverlayComponent } from './component/spinner-overlay/spinner-overlay.component';
import { ErrorDialogComponent } from './component/error-dialog/error-dialog.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { HeadlineComponent } from './component/headline/headline.component';
import { DateAdapter, MAT_DATE_LOCALE } from '@angular/material/core';
import { InfoDialogComponent } from './component/info-dialog/info-dialog.component';
import { CloseButtonComponent } from './component/close-button/close-button.component';
import { DateFnsAdapter } from '@angular/material-date-fns-adapter';
import { AutocompleteProjektComponent } from './component/autocomplete-projekt/autocomplete-projekt.component';
import { AutocompleteEinsatzortComponent } from './component/autocomplete-einsatzort/autocomplete-einsatzort.component';
import { AutocompleteKundeComponent } from './component/autocomplete-kunde/autocomplete-kunde.component';
import { SpinnerComponent } from './component/spinner/spinner.component';
import { PipesModule } from './pipes.module';
import { DragAndDropDirective } from './directives/drag-and-drop.directive';
import { EmailDialogComponent } from './component/email-dialog/email-dialog.component';
import { AutocompleteMitarbeiterComponent } from './component/autocomplete-mitarbeiter/autocomplete-mitarbeiter.component';
import { TabelleLeerCardComponent } from './component/tabelle-leer-card/tabelle-leer-card.component';
import { AutocompleteMultiselectEmailComponent } from './component/autocomplete-multiselect-email/autocomplete-multiselect-email.component';
import { MatChipsModule } from '@angular/material/chips';

const modules = [MaterialModule, CommonModule, ReactiveFormsModule, HttpClientModule, PipesModule];

@NgModule({
  declarations: [
    ConfirmDialogComponent,
    ErrorDialogComponent,
    InfoDialogComponent,
    SpinnerComponent,
    SpinnerOverlayComponent,
    HeadlineComponent,
    CloseButtonComponent,
    AutocompleteProjektComponent,
    AutocompleteEinsatzortComponent,
    AutocompleteMitarbeiterComponent,
    AutocompleteKundeComponent,
    DragAndDropDirective,
    EmailDialogComponent,
    TabelleLeerCardComponent,
    AutocompleteMultiselectEmailComponent,
  ],
  imports: [modules, MatChipsModule],
  exports: [
    modules,
    ConfirmDialogComponent,
    ErrorDialogComponent,
    InfoDialogComponent,
    SpinnerOverlayComponent,
    HeadlineComponent,
    CloseButtonComponent,
    AutocompleteProjektComponent,
    AutocompleteEinsatzortComponent,
    AutocompleteMitarbeiterComponent,
    AutocompleteKundeComponent,
    SpinnerComponent,
    DragAndDropDirective,
    TabelleLeerCardComponent,
  ],
  providers: [
    {
      provide: DateAdapter,
      useClass: DateFnsAdapter,
      deps: [MAT_DATE_LOCALE],
    },
  ],
})
export class SharedModule {}
