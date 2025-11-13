package com.georgedroidnegroid.auth_service.service;

import com.georgedroidnegroid.auth_service.entity.User;
import com.georgedroidnegroid.auth_service.repository.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1)
                .email("test@example.com")
                .passwordHash("$2a$10$encodedPassword")
                .build();
    }

    @Test
    void save_ShouldSaveUser() {
        when(authRepository.save(any(User.class))).thenReturn(testUser);

        authService.save(testUser);

        verify(authRepository, times(1)).save(testUser);
    }

    @Test
    void delete_ShouldDeleteUser() {
        doNothing().when(authRepository).delete(any(User.class));

        authService.delete(testUser);

        verify(authRepository, times(1)).delete(testUser);
    }

    @Test
    void registerUser_ShouldCreateAndSaveUser() {
        String email = "newuser@example.com";
        String password = "password123";
        String encodedPassword = "$2a$10$encodedPassword";

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(authRepository.save(any(User.class))).thenReturn(testUser);

        authService.registerUser(email, password);

        verify(passwordEncoder, times(1)).encode(password);
        verify(authRepository, times(1)).save(any(User.class));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnUser() {
        String email = "test@example.com";
        String password = "password123";

        when(authRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPasswordHash())).thenReturn(true);

        User result = authService.login(email, password);

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(authRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, testUser.getPasswordHash());
    }

    @Test
    void login_WithInvalidEmail_ShouldThrowUsernameNotFoundException() {
        String email = "nonexistent@example.com";
        String password = "password123";

        when(authRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            authService.login(email, password);
        });

        verify(authRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_WithInvalidPassword_ShouldThrowBadCredentialsException() {
        String email = "test@example.com";
        String password = "wrongpassword";

        when(authRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPasswordHash())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> {
            authService.login(email, password);
        });

        verify(authRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, testUser.getPasswordHash());
    }

    @Test
    void findByEmail_WithExistingEmail_ShouldReturnUser() {
        String email = "test@example.com";
        when(authRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        Optional<User> result = authService.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(testUser.getEmail(), result.get().getEmail());
        verify(authRepository, times(1)).findByEmail(email);
    }

    @Test
    void findByEmail_WithNonExistingEmail_ShouldReturnEmpty() {
        String email = "nonexistent@example.com";
        when(authRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> result = authService.findByEmail(email);

        assertFalse(result.isPresent());
        verify(authRepository, times(1)).findByEmail(email);
    }

    @Test
    void findById_WithExistingId_ShouldReturnUser() {
        Integer id = 1;
        when(authRepository.findById(id)).thenReturn(Optional.of(testUser));

        Optional<User> result = authService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        verify(authRepository, times(1)).findById(id);
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        Integer id = 999;
        when(authRepository.findById(id)).thenReturn(Optional.empty());

        Optional<User> result = authService.findById(id);

        assertFalse(result.isPresent());
        verify(authRepository, times(1)).findById(id);
    }
}

