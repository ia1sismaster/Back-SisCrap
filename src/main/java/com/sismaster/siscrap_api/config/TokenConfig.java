package com.sismaster.siscrap_api.config;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.jsonwebtoken.security.Keys;

@Configuration
public class TokenConfig {
    
    @Value("${JWT_SECRET}")
    private String token;
    
    @Bean(name = "tokenFixo")
    public Key tokenGenerate(){
    
        return Keys.hmacShaKeyFor(token.getBytes(StandardCharsets.UTF_8));
    }
}