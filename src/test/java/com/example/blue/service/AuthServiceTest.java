package com.example.blue.service;

import com.example.blue.DTO.AuthResponse;
import com.example.blue.DTO.LoginRequest;
import com.example.blue.DTO.RegisterRequest;
import com.example.blue.model.Role;
import com.example.blue.model.User;
import com.example.blue.repository.UserRepository;

import com.example.blue.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthService authService;

    @Test
    void testRegisterRequest() {
        RegisterRequest request=new RegisterRequest();
        request.setUsername("Harini");
        request.setEmail("harini123");
        request.setPassword("123");
        User user=new User();
        user.setUsername("Harini");
        user.setEmail("harini123");
        user.setPassword("encoded123");
        user.setRole(Role.ADMIN);
        when(userRepository.existsByUsername("Harini"))
                .thenReturn(false);

        when(userRepository.existsByEmail("harini123"))
                .thenReturn(false);

        when(passwordEncoder.encode("123"))
                .thenReturn("encoded123");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        when(jwtService.generateToken("Harini"))
                .thenReturn("dummy-token");

        AuthResponse response=authService.register(request);
        assertEquals("dummy-token",response.getToken());
        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(request.getUsername());
    }
    @Test
    void testLoginRequest() {
        LoginRequest request=new LoginRequest();
        request.setUsername("Harini");
        request.setPassword("123");
        User user=new User();
        user.setUsername("Harini");
        user.setEmail("harini123");
        user.setPassword("encoded123");
        user.setRole(Role.ADMIN);
        when(userRepository.findByUsername(request.getUsername()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123","encoded123"))
                .thenReturn(true);

        when(jwtService.generateToken("Harini"))
                .thenReturn("dummy-token");

        AuthResponse response=authService.login(request);
        assertEquals("dummy-token",response.getToken());

        verify(jwtService).generateToken(request.getUsername());
    }
    @Test
    void testRegisterRequestUserAlreadyExists() {
        RegisterRequest request=new RegisterRequest();
        request.setUsername("Harini");
        request.setEmail("harini123");
        request.setPassword("123");
        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);
        when(userRepository.existsByUsername(request.getUsername()))
                .thenReturn(true);
        RuntimeException runtimeException=assertThrows
                (RuntimeException.class,()-> authService.register(request));
        assertEquals("Username already Exists.",runtimeException.getMessage());

    }
    @Test
    void testRegisterRequestEmailAlreadyExists() {
        RegisterRequest request=new RegisterRequest();
        request.setUsername("Harini");
        request.setEmail("harini123");
        request.setPassword("123");
        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);
        RuntimeException runtimeException=assertThrows
                (RuntimeException.class,()-> authService.register(request));
        assertEquals("Email already Exists.",runtimeException.getMessage());

    }
    @Test
    void testLoginRequestUserNotFound() {
        LoginRequest request=new LoginRequest();
        request.setUsername("Harini");
        request.setPassword("123");
        when(userRepository.findByUsername(request.getUsername()))
                .thenReturn(Optional.empty());
        RuntimeException runtimeException=assertThrows
                (RuntimeException.class,()-> authService.login(request));
        assertEquals("User not found",runtimeException.getMessage());
    }
    @Test
    void testLoginRequestInvalidCredentials() {
        LoginRequest request=new LoginRequest();
        request.setUsername("Harini");
        request.setPassword("123");

        User user=new User();
        user.setUsername("Harini");
        user.setEmail("harini123");
        user.setPassword("encoded123");
        when(userRepository.findByUsername(request.getUsername()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(),user.getPassword()))
                .thenReturn(false);
        RuntimeException runtimeException=assertThrows
                (RuntimeException.class,()->authService.login(request));
        assertEquals("Invalid Credentials",runtimeException.getMessage());
    }
}
