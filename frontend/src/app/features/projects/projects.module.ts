import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ProjectDetailComponent } from './project-detail/project-detail.component';

const routes: Routes = [
  { path: ':id', component: ProjectDetailComponent }
];

@NgModule({
  declarations: [ProjectDetailComponent],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule.forChild(routes)]
})
export class ProjectsModule {}
