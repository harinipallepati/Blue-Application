package com.example.blue.integration;

import com.example.blue.DTO.LoginRequest;
import com.example.blue.DTO.RegisterRequest;
import com.example.blue.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    public void TestRegisterRequest() throws Exception {
        userRepository.deleteAll();
        RegisterRequest request=new RegisterRequest();
        request.setUsername("Harini");
        request.setPassword("123");
        request.setEmail("harini123");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
    @Test
    public void TestLoginRequest() throws Exception {
        LoginRequest request=new LoginRequest();
        request.setUsername("Harini");
        request.setPassword("123");
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
    @Test
    public void TestRegisterRequestUsernameExists() throws Exception {
        RegisterRequest request=new RegisterRequest();
        request.setUsername("Harini");
        request.setPassword("123");
        request.setEmail("harini12");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Username already Exists."));

    }
    @Test
    public void TestRegisterRequestEmailExists() throws Exception {
        RegisterRequest request=new RegisterRequest();
        request.setUsername("harini");
        request.setPassword("123");
        request.setEmail("harini123");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Email already Exists."));

    }
    @Test
    public void TestLoginRequestUsernameNotFound() throws Exception {
        LoginRequest request=new LoginRequest();
        request.setUsername("arini");
        request.setPassword("123");
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("User not found"));
    }
    @Test
    public void TestLoginRequestInvalidCredentials() throws Exception {
        LoginRequest request=new LoginRequest();
        request.setUsername("harini");
        request.setPassword("1234");
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Invalid Credentials"));
    }
    @Test
    public void TestLoginRequestUsernameEmpty() throws Exception {
        LoginRequest request=new LoginRequest();
        request.setUsername("");
        request.setPassword("123");
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Validation failed"));
    }
}
