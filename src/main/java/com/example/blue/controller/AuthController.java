package com.example.blue.controller;

import com.example.blue.DTO.AuthResponse;
import com.example.blue.DTO.LoginRequest;
import com.example.blue.DTO.RegisterRequest;
import com.example.blue.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid
            @RequestBody RegisterRequest request) {
                return authService.register(request);
    }
    @PostMapping("/login")
    public AuthResponse login(@Valid
            @RequestBody LoginRequest request) {
                return authService.login(request);
    }
}
