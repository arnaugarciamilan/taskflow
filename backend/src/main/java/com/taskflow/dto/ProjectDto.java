package com.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

public class ProjectDto {

    @Data
    public static class Request {
        @NotBlank(message = "Project name is required")
        @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
        private String name;

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        private String description;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private UserDto.Response owner;
        private LocalDateTime createdAt;
        private int taskCount;
    }
}
