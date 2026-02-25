package com.taskflow.service;

import com.taskflow.dto.TaskDto;
import com.taskflow.entity.*;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.exception.UnauthorizedException;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Unit Tests")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    private User owner;
    private Project project;
    private Task task;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("Test User")
                .email("user@test.com")
                .password("encoded_password")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        project = Project.builder()
                .id(1L)
                .name("Test Project")
                .description("A test project")
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .build();

        task = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("A test task")
                .status(TaskStatus.TODO)
                .priority(Priority.MEDIUM)
                .dueDate(LocalDate.now().plusDays(7))
                .project(project)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should return tasks by project ID")
    void findByProject_ShouldReturnTasks() {
        when(taskRepository.findByProjectIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(task));

        List<TaskDto.Response> result = taskService.findByProject(1L, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Task");
        assertThat(result.get(0).getStatus()).isEqualTo("TODO");
        verify(taskRepository).findByProjectIdOrderByCreatedAtDesc(1L);
    }

    @Test
    @DisplayName("Should filter tasks by status")
    void findByProject_WithStatusFilter_ShouldReturnFilteredTasks() {
        when(taskRepository.findByProjectIdAndStatus(1L, TaskStatus.TODO)).thenReturn(List.of(task));

        List<TaskDto.Response> result = taskService.findByProject(1L, TaskStatus.TODO, null);

        assertThat(result).hasSize(1);
        verify(taskRepository).findByProjectIdAndStatus(1L, TaskStatus.TODO);
    }

    @Test
    @DisplayName("Should filter tasks by priority")
    void findByProject_WithPriorityFilter_ShouldReturnFilteredTasks() {
        when(taskRepository.findByProjectIdAndPriority(1L, Priority.MEDIUM)).thenReturn(List.of(task));

        List<TaskDto.Response> result = taskService.findByProject(1L, null, Priority.MEDIUM);

        assertThat(result).hasSize(1);
        verify(taskRepository).findByProjectIdAndPriority(1L, Priority.MEDIUM);
    }

    @Test
    @DisplayName("Should create task successfully")
    void create_ShouldCreateTask() {
        TaskDto.Request request = new TaskDto.Request();
        request.setTitle("New Task");
        request.setDescription("Task description");
        request.setStatus(TaskStatus.TODO);
        request.setPriority(Priority.HIGH);
        request.setProjectId(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDto.Response result = taskService.create(request, "user@test.com");

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Task");
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when project not found during task creation")
    void create_WhenProjectNotFound_ShouldThrowException() {
        TaskDto.Request request = new TaskDto.Request();
        request.setProjectId(99L);
        request.setStatus(TaskStatus.TODO);
        request.setPriority(Priority.MEDIUM);
        request.setTitle("Task");

        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.create(request, "user@test.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Should throw exception when user is not project owner")
    void create_WhenNotProjectOwner_ShouldThrowUnauthorized() {
        TaskDto.Request request = new TaskDto.Request();
        request.setProjectId(1L);
        request.setStatus(TaskStatus.TODO);
        request.setPriority(Priority.MEDIUM);
        request.setTitle("Task");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> taskService.create(request, "other@test.com"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("Should delete task successfully")
    void delete_ShouldDeleteTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.delete(1L, "user@test.com");

        verify(taskRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting task with wrong user")
    void delete_WhenNotOwner_ShouldThrowUnauthorized() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.delete(1L, "hacker@test.com"))
                .isInstanceOf(UnauthorizedException.class);

        verify(taskRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should map task entity to response DTO correctly")
    void toResponse_ShouldMapCorrectly() {
        TaskDto.Response response = taskService.toResponse(task);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Test Task");
        assertThat(response.getStatus()).isEqualTo("TODO");
        assertThat(response.getPriority()).isEqualTo("MEDIUM");
        assertThat(response.getProjectId()).isEqualTo(1L);
    }
}
