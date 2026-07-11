package com.example.blue.config;

import com.example.blue.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtFilter jwtFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf-> csrf.disable())
                .authorizeHttpRequests
                        (auth->auth.requestMatchers
                                        ("/auth/register","/auth/login")
                                .permitAll().
                                requestMatchers(HttpMethod.GET,"/products/**").hasAnyRole("USER","ADMIN")
                                .requestMatchers(HttpMethod.POST,"/products").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT,"/products/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE,"/products/**").hasRole("ADMIN")
                                .anyRequest().authenticated())
                .sessionManagement(session-> session.
                        sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
