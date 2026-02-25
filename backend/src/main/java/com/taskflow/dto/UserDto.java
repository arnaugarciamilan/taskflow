package com.taskflow.dto;

import com.taskflow.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

public class UserDto {

    @Data
    public static class Request {
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100)
        private String name;

        @NotBlank(message = "Email is required")
        @Email
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 100)
        private String password;

        private Role role = Role.USER;
    }

    @Data
    public static class UpdateRequest {
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100)
        private String name;

        @NotBlank(message = "Email is required")
        @Email
        private String email;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private String email;
        private String role;
        private LocalDateTime createdAt;
    }
}
