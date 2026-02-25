import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ProjectService } from '../../core/services/project.service';
import { AuthService } from '../../core/services/auth.service';
import { Project } from '../../core/models/models';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  projects: Project[] = [];
  loading = true;
  showCreateModal = false;
  createForm: FormGroup;
  creating = false;
  currentUser = this.authService.getCurrentUser();

  constructor(
    private projectService: ProjectService,
    private authService: AuthService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.createForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      description: ['', Validators.maxLength(1000)]
    });
  }

  ngOnInit(): void {
    this.loadProjects();
  }

  loadProjects(): void {
    this.loading = true;
    this.projectService.getMyProjects().subscribe({
      next: (projects) => {
        this.projects = projects;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  openProject(id: number): void {
    this.router.navigate(['/projects', id]);
  }

  createProject(): void {
    if (this.createForm.invalid) {
      this.createForm.markAllAsTouched();
      return;
    }
    this.creating = true;
    this.projectService.create(this.createForm.value).subscribe({
      next: (project) => {
        this.projects.unshift(project);
        this.showCreateModal = false;
        this.createForm.reset();
        this.creating = false;
      },
      error: () => this.creating = false
    });
  }

  deleteProject(id: number, event: Event): void {
    event.stopPropagation();
    if (confirm('Are you sure you want to delete this project?')) {
      this.projectService.delete(id).subscribe({
        next: () => this.projects = this.projects.filter(p => p.id !== id)
      });
    }
  }

  logout(): void {
    this.authService.logout();
  }
}
