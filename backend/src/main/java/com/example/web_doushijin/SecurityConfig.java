package com.example.web_doushijin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Tắt chống giả mạo để nạp được dữ liệu (POST)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // Mở toang cửa cho mọi người
        return http.build();
    }
}