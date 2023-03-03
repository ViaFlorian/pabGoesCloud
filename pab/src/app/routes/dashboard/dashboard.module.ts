import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardActionBarComponent } from './components/dashboard-action-bar/dashboard-action-bar.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { MaterialModule } from '../../shared/material/material.module';
import { DashboardChangelogComponent } from './components/dashboard-changelog/dashboard-changelog.component';
import { DashboardUebersichtComponent } from './components/dashboard-uebersicht/dashboard-uebersicht.component';
import { NgxChartsModule, PieChartModule } from '@swimlane/ngx-charts';
import { DashboardUebersichtOeLeiterComponent } from './components/dashboard-uebersicht-oe-leiter/dashboard-uebersicht-oe-leiter.component';
import { PipesModule } from '../../shared/pipes.module';

const routes: Routes = [
  {
    path: '',
    component: DashboardComponent,
  },
];

@NgModule({
  declarations: [
    DashboardActionBarComponent,
    DashboardChangelogComponent,
    DashboardUebersichtComponent,
    DashboardUebersichtOeLeiterComponent,
    DashboardComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    MaterialModule,
    PipesModule,
    RouterModule.forChild(routes),
    PieChartModule,
    NgxChartsModule,
  ],
})
export class DashboardModule {}
