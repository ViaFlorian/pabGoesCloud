import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BerichteActionBarComponent } from './components/berichte-action-bar/berichte-action-bar.component';
import { BerichteB002Component } from './pages/berichte-b002/berichte-b002.component';
import { RouterModule, Routes } from '@angular/router';
import { MaterialModule } from '../../shared/material/material.module';
import { SharedModule } from '../../shared/shared.module';
import { MatMultiSortModule } from 'ngx-mat-multi-sort';
import { BerichteB004Component } from './pages/berichte-b004/berichte-b004.component';
import { BerichteB012Component } from './pages/berichte-b012/berichte-b012.component';
import { BerichteB011Component } from './pages/berichte-b011/berichte-b011.component';
import { BerichteB005Component } from './pages/berichte-b005/berichte-b005.component';
import { BerichteB006Component } from './pages/berichte-b006/berichte-b006.component';
import { BerichteB009Component } from './pages/berichte-b009/berichte-b009.component';
import { BerichteB010Component } from './pages/berichte-b010/berichte-b010.component';
import { BerichteB015Component } from './pages/berichte-b015/berichte-b015.component';
import { BerichteB016Component } from './pages/berichte-b016/berichte-b016.component';
import { BerichteB013Component } from './pages/berichte-b013/berichte-b013.component';
import { BerichteB014Component } from './pages/berichte-b014/berichte-b014.component';
import { BerichteB007Component } from './pages/berichte-b007/berichte-b007.component';
import { BerichteB008Component } from './pages/berichte-b008/berichte-b008.component';

const routes: Routes = [
  {
    path: 'b002',
    component: BerichteB002Component,
  },
  {
    path: 'b004',
    component: BerichteB004Component,
  },
  {
    path: 'b005',
    component: BerichteB005Component,
  },
  {
    path: 'b006',
    component: BerichteB006Component,
  },
  {
    path: 'b007',
    component: BerichteB007Component,
  },
  {
    path: 'b008',
    component: BerichteB008Component,
  },
  {
    path: 'b009',
    component: BerichteB009Component,
  },
  {
    path: 'b010',
    component: BerichteB010Component,
  },
  {
    path: 'b011',
    component: BerichteB011Component,
  },
  {
    path: 'b012',
    component: BerichteB012Component,
  },
  {
    path: 'b013',
    component: BerichteB013Component,
  },
  {
    path: 'b014',
    component: BerichteB014Component,
  },
  {
    path: 'b015',
    component: BerichteB015Component,
  },
  {
    path: 'b016',
    component: BerichteB016Component,
  },
];

@NgModule({
  declarations: [
    BerichteActionBarComponent,
    BerichteB002Component,
    BerichteB004Component,
    BerichteB005Component,
    BerichteB006Component,
    BerichteB007Component,
    BerichteB008Component,
    BerichteB009Component,
    BerichteB010Component,
    BerichteB011Component,
    BerichteB012Component,
    BerichteB013Component,
    BerichteB014Component,
    BerichteB015Component,
    BerichteB016Component,
  ],
  imports: [CommonModule, SharedModule, RouterModule.forChild(routes), MaterialModule, MatMultiSortModule],
})
export class BerichteModule {}
