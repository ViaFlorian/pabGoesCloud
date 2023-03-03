import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MaterialModule } from '../shared/material/material.module';
import { TitleBarComponent } from './components/title-bar/title-bar.component';
import { SucheComponent } from './components/suche/suche.component';
import { ReactiveFormsModule } from '@angular/forms';
import { SuchergebnisComponent } from './components/suchergebnis/suchergebnis.component';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  declarations: [TitleBarComponent, SucheComponent, SuchergebnisComponent],
  imports: [CommonModule, MaterialModule, RouterModule, ReactiveFormsModule, SharedModule],
  exports: [TitleBarComponent],
})
export class StrukturModule {}
