package com.taskflow.service;

import com.taskflow.dto.TaskDto;
import com.taskflow.entity.Priority;
import com.taskflow.entity.Project;
import com.taskflow.entity.Task;
import com.taskflow.entity.TaskStatus;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.exception.UnauthorizedException;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public List<TaskDto.Response> findByProject(Long projectId, TaskStatus status, Priority priority) {
        List<Task> tasks;

        if (status != null && priority != null) {
            tasks = taskRepository.findByProjectIdAndStatusAndPriority(projectId, status, priority);
        } else if (status != null) {
            tasks = taskRepository.findByProjectIdAndStatus(projectId, status);
        } else if (priority != null) {
            tasks = taskRepository.findByProjectIdAndPriority(projectId, priority);
        } else {
            tasks = taskRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
        }

        return tasks.stream().map(this::toResponse).toList();
    }

    public TaskDto.Response findById(Long id) {
        return toResponse(getTaskById(id));
    }

    @Transactional
    public TaskDto.Response create(TaskDto.Request request, String currentUserEmail) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", request.getProjectId()));

        if (!project.getOwner().getEmail().equals(currentUserEmail)) {
            throw new UnauthorizedException("You don't have permission to add tasks to this project");
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .project(project)
                .build();

        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskDto.Response update(Long id, TaskDto.UpdateRequest request, String currentUserEmail) {
        Task task = getTaskById(id);
        validateProjectOwner(task, currentUserEmail);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public void delete(Long id, String currentUserEmail) {
        Task task = getTaskById(id);
        validateProjectOwner(task, currentUserEmail);
        taskRepository.deleteById(id);
    }

    private Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
    }

    private void validateProjectOwner(Task task, String currentUserEmail) {
        if (!task.getProject().getOwner().getEmail().equals(currentUserEmail)) {
            throw new UnauthorizedException("You don't have permission to modify this task");
        }
    }

    public TaskDto.Response toResponse(Task task) {
        TaskDto.Response response = new TaskDto.Response();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus().name());
        response.setPriority(task.getPriority().name());
        response.setDueDate(task.getDueDate());
        response.setProjectId(task.getProject().getId());
        response.setCreatedAt(task.getCreatedAt());
        return response;
    }
}
