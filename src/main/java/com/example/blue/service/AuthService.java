package com.example.blue.service;

import com.example.blue.DTO.AuthResponse;
import com.example.blue.DTO.LoginRequest;
import com.example.blue.DTO.RegisterRequest;
import com.example.blue.model.Role;
import com.example.blue.model.User;
import com.example.blue.repository.UserRepository;
import com.example.blue.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public AuthResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already Exists.");
        }
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already Exists.");
        }
        User user=new User();

        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);

        userRepository.save(user);
        String token= jwtService.generateToken(user.getUsername());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        User user=userRepository.findByUsername(request.getUsername())
                .orElseThrow(()->new RuntimeException("User not found"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid Credentials");
        }
        String token= jwtService.generateToken(user.getUsername());
        return new AuthResponse(token);
    }
}
