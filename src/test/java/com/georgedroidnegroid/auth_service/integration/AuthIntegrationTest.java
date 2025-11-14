package com.georgedroidnegroid.auth_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.georgedroidnegroid.auth_service.dto.UserRegisterDto;
import com.georgedroidnegroid.auth_service.entity.User;
import com.georgedroidnegroid.auth_service.repository.AuthRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserRegisterDto testUserDto;

    @BeforeEach
    void setUp() {
        authRepository.deleteAll();

        testUserDto = UserRegisterDto.builder()
                .email("elbondarenko04@gmail.com")
                .password("123")
                .build();
    }

    @AfterEach
    void tearDown() {
        authRepository.deleteAll();
    }

    @Test
    void register_WithValidData_ShouldReturnJwtToken() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jwtToken", notNullValue()));
    }

//    @Test
//    void register_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
//        UserRegisterDto invalidDto = UserRegisterDto.builder()
//                .email("invalid-email")
//                .password("testPassword123")
//                .build();
//
//        mockMvc.perform(post("/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDto)))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void login_WithValidCredentials_ShouldReturnJwtToken() throws Exception {
        User user = User.builder()
                .email(testUserDto.getEmail())
                .passwordHash(passwordEncoder.encode(testUserDto.getPassword()))
                .build();
        authRepository.save(user);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jwtToken", notNullValue()));
    }

    @Test
    void login_WithInvalidPassword_ShouldReturnUnauthorized() throws Exception {
        User user = User.builder()
                .email(testUserDto.getEmail())
                .passwordHash(passwordEncoder.encode("correctPassword"))
                .build();
        authRepository.save(user);

        UserRegisterDto wrongPasswordDto = UserRegisterDto.builder()
                .email(testUserDto.getEmail())
                .password("wrongPassword")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPasswordDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", notNullValue()));
    }

//    @Test
//    void login_WithNonExistentUser_ShouldReturnUnauthorized() throws Exception {
//        UserRegisterDto nonExistentDto = UserRegisterDto.builder()
//                .email("nonexistent@test.com")
//                .password("password123")
//                .build();
//
//        mockMvc.perform(post("/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(nonExistentDto)))
//                .andExpect(status().isUnauthorized());
//    }

    @Test
    void register_ThenLogin_ShouldWorkCorrectly() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken", notNullValue()));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken", notNullValue()));
    }
}

