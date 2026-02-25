package com.taskflow.dto;

import com.taskflow.entity.Priority;
import com.taskflow.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskDto {

    @Data
    public static class Request {
        @NotBlank(message = "Title is required")
        @Size(min = 2, max = 200, message = "Title must be between 2 and 200 characters")
        private String title;

        @Size(max = 2000, message = "Description cannot exceed 2000 characters")
        private String description;

        @NotNull(message = "Status is required")
        private TaskStatus status = TaskStatus.TODO;

        @NotNull(message = "Priority is required")
        private Priority priority = Priority.MEDIUM;

        private LocalDate dueDate;

        @NotNull(message = "Project ID is required")
        private Long projectId;
    }

    @Data
    public static class UpdateRequest {
        @NotBlank(message = "Title is required")
        @Size(min = 2, max = 200)
        private String title;

        @Size(max = 2000)
        private String description;

        @NotNull(message = "Status is required")
        private TaskStatus status;

        @NotNull(message = "Priority is required")
        private Priority priority;

        private LocalDate dueDate;
    }

    @Data
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private String status;
        private String priority;
        private LocalDate dueDate;
        private Long projectId;
        private LocalDateTime createdAt;
    }
}
