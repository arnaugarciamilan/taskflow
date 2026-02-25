import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProjectService } from '../../../core/services/project.service';
import { TaskService } from '../../../core/services/task.service';
import { Project, Task, TaskStatus, Priority } from '../../../core/models/models';

@Component({
  selector: 'app-project-detail',
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.scss']
})
export class ProjectDetailComponent implements OnInit {
  project: Project | null = null;
  tasks: Task[] = [];
  loading = true;
  projectId!: number;

  // Filters
  filterStatus: TaskStatus | '' = '';
  filterPriority: Priority | '' = '';

  // Task modal
  showTaskModal = false;
  editingTask: Task | null = null;
  taskForm: FormGroup;
  savingTask = false;

  readonly STATUSES: TaskStatus[] = ['TODO', 'IN_PROGRESS', 'DONE'];
  readonly PRIORITIES: Priority[] = ['LOW', 'MEDIUM', 'HIGH'];

  statusLabels: Record<TaskStatus, string> = {
    TODO: 'To Do',
    IN_PROGRESS: 'In Progress',
    DONE: 'Done'
  };

  priorityColors: Record<Priority, string> = {
    LOW: '#48bb78',
    MEDIUM: '#ed8936',
    HIGH: '#e53e3e'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private projectService: ProjectService,
    private taskService: TaskService,
    private fb: FormBuilder
  ) {
    this.taskForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(2)]],
      description: [''],
      status: ['TODO', Validators.required],
      priority: ['MEDIUM', Validators.required],
      dueDate: [null]
    });
  }

  ngOnInit(): void {
    this.projectId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.projectService.getById(this.projectId).subscribe({
      next: (project) => {
        this.project = project;
        this.loadTasks();
      },
      error: () => {
        this.loading = false;
        this.router.navigate(['/dashboard']);
      }
    });
  }

  loadTasks(): void {
    const status = this.filterStatus || undefined;
    const priority = this.filterPriority || undefined;

    this.taskService.getByProject(this.projectId, status as TaskStatus, priority as Priority).subscribe({
      next: (tasks) => {
        this.tasks = tasks;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  getTasksByStatus(status: TaskStatus): Task[] {
    return this.tasks.filter(t => t.status === status);
  }

  openCreateTask(): void {
    this.editingTask = null;
    this.taskForm.reset({ status: 'TODO', priority: 'MEDIUM' });
    this.showTaskModal = true;
  }

  openEditTask(task: Task, event: Event): void {
    event.stopPropagation();
    this.editingTask = task;
    this.taskForm.patchValue({
      title: task.title,
      description: task.description,
      status: task.status,
      priority: task.priority,
      dueDate: task.dueDate
    });
    this.showTaskModal = true;
  }

  saveTask(): void {
    if (this.taskForm.invalid) {
      this.taskForm.markAllAsTouched();
      return;
    }

    this.savingTask = true;
    const formValue = this.taskForm.value;

    if (this.editingTask) {
      this.taskService.update(this.editingTask.id, formValue).subscribe({
        next: (updated) => {
          const index = this.tasks.findIndex(t => t.id === updated.id);
          if (index >= 0) this.tasks[index] = updated;
          this.tasks = [...this.tasks];
          this.closeModal();
        },
        error: () => this.savingTask = false
      });
    } else {
      this.taskService.create({ ...formValue, projectId: this.projectId }).subscribe({
        next: (task) => {
          this.tasks = [task, ...this.tasks];
          this.closeModal();
        },
        error: () => this.savingTask = false
      });
    }
  }

  deleteTask(id: number, event: Event): void {
    event.stopPropagation();
    if (confirm('Delete this task?')) {
      this.taskService.delete(id).subscribe({
        next: () => this.tasks = this.tasks.filter(t => t.id !== id)
      });
    }
  }

  closeModal(): void {
    this.showTaskModal = false;
    this.editingTask = null;
    this.savingTask = false;
    this.taskForm.reset({ status: 'TODO', priority: 'MEDIUM' });
  }

  applyFilters(): void {
    this.loadTasks();
  }

  clearFilters(): void {
    this.filterStatus = '';
    this.filterPriority = '';
    this.loadTasks();
  }

  isOverdue(date: string): boolean {
    return new Date(date) < new Date();
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }
}

