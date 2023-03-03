import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TestseiteComponent } from './pages/testseite/testseite.component';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    component: TestseiteComponent,
  },
];

@NgModule({
  declarations: [TestseiteComponent],
  imports: [CommonModule, RouterModule.forChild(routes)],
})
export class TestseiteModule {}
