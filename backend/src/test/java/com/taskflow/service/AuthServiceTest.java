package com.taskflow.service;

import com.taskflow.dto.AuthDto;
import com.taskflow.entity.Role;
import com.taskflow.entity.User;
import com.taskflow.exception.DuplicateResourceException;
import com.taskflow.repository.UserRepository;
import com.taskflow.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private User savedUser;
    private UserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        savedUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@test.com")
                .password("encoded_password")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        mockUserDetails = org.springframework.security.core.userdetails.User.builder()
                .username("john@test.com")
                .password("encoded_password")
                .authorities(List.of())
                .build();
    }

    @Test
    @DisplayName("Should register user and return JWT token")
    void register_ShouldReturnToken() {
        AuthDto.RegisterRequest request = new AuthDto.RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@test.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userDetailsService.loadUserByUsername("john@test.com")).thenReturn(mockUserDetails);
        when(jwtService.generateToken(mockUserDetails)).thenReturn("mocked.jwt.token");

        AuthDto.AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("mocked.jwt.token");
        assertThat(response.getEmail()).isEqualTo("john@test.com");
        assertThat(response.getName()).isEqualTo("John Doe");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void register_WhenEmailExists_ShouldThrowException() {
        AuthDto.RegisterRequest request = new AuthDto.RegisterRequest();
        request.setEmail("john@test.com");
        request.setName("John");
        request.setPassword("password");

        when(userRepository.existsByEmail("john@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("john@test.com");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should login and return JWT token")
    void login_ShouldReturnToken() {
        AuthDto.LoginRequest request = new AuthDto.LoginRequest();
        request.setEmail("john@test.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(savedUser));
        when(userDetailsService.loadUserByUsername("john@test.com")).thenReturn(mockUserDetails);
        when(jwtService.generateToken(mockUserDetails)).thenReturn("mocked.jwt.token");

        AuthDto.AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("mocked.jwt.token");
        assertThat(response.getEmail()).isEqualTo("john@test.com");
    }
}
