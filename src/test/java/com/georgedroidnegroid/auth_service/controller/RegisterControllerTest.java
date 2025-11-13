package com.georgedroidnegroid.auth_service.controller;

import com.georgedroidnegroid.auth_service.config.JwtUtil;
import com.georgedroidnegroid.auth_service.dto.UserRegisterDto;
import com.georgedroidnegroid.auth_service.entity.User;
import com.georgedroidnegroid.auth_service.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterControllerTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthService authService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private RegisterController registerController;

    private UserRegisterDto userRegisterDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        userRegisterDto = UserRegisterDto.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        testUser = User.builder()
                .id(1)
                .email("test@example.com")
                .passwordHash("$2a$10$encodedPassword")
                .build();
    }

    @Test
    void performLogin_WithValidCredentials_ShouldReturnJwtToken() {
        String expectedToken = "jwt.token.here";

        when(bindingResult.hasErrors()).thenReturn(false);
        when(authService.login(anyString(), anyString())).thenReturn(testUser);
        when(jwtUtil.generateToken(anyString(), anyInt())).thenReturn(expectedToken);

        ResponseEntity<?> response = registerController.performLogin(userRegisterDto, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals(expectedToken, body.get("jwtToken"));

        verify(authService, times(1)).login(userRegisterDto.getEmail(), userRegisterDto.getPassword());
        verify(jwtUtil, times(1)).generateToken(testUser.getEmail(), testUser.getId());
    }

    @Test
    void performLogin_WithValidationErrors_ShouldReturnBadRequest() {
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<?> response = registerController.performLogin(userRegisterDto, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Validation failed", body.get("error"));

        verify(authService, never()).login(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyInt());
    }

    @Test
    void performLogin_WithInvalidCredentials_ShouldReturnUnauthorized() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(authService.login(anyString(), anyString()))
                .thenThrow(new BadCredentialsException("Invalid password"));

        ResponseEntity<?> response = registerController.performLogin(userRegisterDto, bindingResult);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Invalid password", body.get("error"));

        verify(authService, times(1)).login(userRegisterDto.getEmail(), userRegisterDto.getPassword());
        verify(jwtUtil, never()).generateToken(anyString(), anyInt());
    }

    @Test
    void performRegister_WithValidData_ShouldReturnJwtToken() {
        String expectedToken = "jwt.token.here";

        when(bindingResult.hasErrors()).thenReturn(false);
        doNothing().when(authService).registerUser(anyString(), anyString());
        when(authService.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(anyString(), anyInt())).thenReturn(expectedToken);

        ResponseEntity<?> response = registerController.performRegister(userRegisterDto, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals(expectedToken, body.get("jwtToken"));

        verify(authService, times(1)).registerUser(userRegisterDto.getEmail(), userRegisterDto.getPassword());
        verify(authService, times(1)).findByEmail(userRegisterDto.getEmail());
        verify(jwtUtil, times(1)).generateToken(testUser.getEmail(), testUser.getId());
    }

    @Test
    void performRegister_WithValidationErrors_ShouldReturnBadRequest() {
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<?> response = registerController.performRegister(userRegisterDto, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Validation failed", body.get("error"));

        verify(authService, never()).registerUser(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyInt());
    }

    @Test
    void performRegister_WhenUserNotFoundAfterRegistration_ShouldThrowException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doNothing().when(authService).registerUser(anyString(), anyString());
        when(authService.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            registerController.performRegister(userRegisterDto, bindingResult);
        });

        verify(authService, times(1)).registerUser(userRegisterDto.getEmail(), userRegisterDto.getPassword());
        verify(authService, times(1)).findByEmail(userRegisterDto.getEmail());
        verify(jwtUtil, never()).generateToken(anyString(), anyInt());
    }

    @Test
    void convertToUser_ShouldMapDtoToEntity() {
        when(modelMapper.map(userRegisterDto, User.class)).thenReturn(testUser);

        User result = registerController.convertToUser(userRegisterDto);

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(modelMapper, times(1)).map(userRegisterDto, User.class);
    }
}

