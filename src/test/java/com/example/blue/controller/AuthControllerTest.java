package com.example.blue.controller;

import com.example.blue.DTO.AuthResponse;
import com.example.blue.DTO.LoginRequest;
import com.example.blue.DTO.RegisterRequest;
import com.example.blue.exception.ResourceNotFoundException;
import com.example.blue.security.JwtFilter;
import com.example.blue.security.JwtService;
import com.example.blue.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private JwtFilter jwtFilter;
    @MockitoBean
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    public void shouldRegister() throws Exception {
        RegisterRequest request=new RegisterRequest();
        request.setUsername("Harini");
        request.setEmail("harini123");
        request.setPassword("123");
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new AuthResponse("dummy-token"));
        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy-token"));

    }
    @Test
    public void shouldLogin() throws Exception {
        LoginRequest request=new LoginRequest();
        request.setUsername("Harini");
        request.setPassword("123");
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new AuthResponse("dummy-token"));
        mockMvc.perform(post("/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy-token"));
    }
    @Test
    public void shouldReturnNotFoundWhenUsernameDoesNotExist() throws Exception {
        LoginRequest request=new LoginRequest();
        request.setUsername("Harini");
        request.setPassword("123");
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new ResourceNotFoundException("username not found"));
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("username not found"));
    }
    @Test
    public void shouldReturnErrorWhenPasswordIsIncorrect() throws Exception {
        LoginRequest request=new LoginRequest();
        request.setUsername("Harini");
        request.setPassword("123");
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Incorrect password"));
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Incorrect password"));
    }
    @Test
    public void shouldReturnErrorWhenUsernameAlreadyExists() throws Exception {
        RegisterRequest request=new RegisterRequest();
        request.setUsername("Harini");
        request.setEmail("harini123");
        request.setPassword("123");
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Username already exists"));
        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Username already exists"));

    }
    @Test
    public void shouldReturnErrorWhenEmailAlreadyExists() throws Exception {
        RegisterRequest request=new RegisterRequest();
        request.setUsername("Harini");
        request.setEmail("harini123");
        request.setPassword("123");
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Email already exists"));
        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Email already exists"));

    }
}
