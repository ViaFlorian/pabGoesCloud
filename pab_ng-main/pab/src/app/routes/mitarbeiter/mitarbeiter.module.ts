import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StundenkontoComponent } from './pages/stundenkonto/stundenkonto.component';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { MaterialModule } from '../../shared/material/material.module';
import { MitarbeiterActionBarComponent } from './components/mitarbeiter-action-bar/mitarbeiter-action-bar.component';
import { UrlaubskontoComponent } from './pages/urlaubskonto/urlaubskonto.component';
import { PipesModule } from '../../shared/pipes.module';

const routes: Routes = [
  {
    path: 'stundenkonto',
    component: StundenkontoComponent,
  },
  {
    path: 'urlaubskonto',
    component: UrlaubskontoComponent,
  },
];

@NgModule({
  declarations: [StundenkontoComponent, UrlaubskontoComponent, MitarbeiterActionBarComponent],
  imports: [CommonModule, SharedModule, MaterialModule, PipesModule, RouterModule.forChild(routes)],
})
export class MitarbeiterModule {}
