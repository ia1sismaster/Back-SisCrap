package com.sismaster.siscrap_api.component;

import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;

@Component
public class TokenGenerator {
    private final Key token;
    
    // Injeta o Bean que criamos na TokenConfig
    public TokenGenerator(@Qualifier("tokenFixo") Key token){
        this.token = token;
    }
    private final long tempoDeExpiracao = 60 * 60 * 1000; 

    public String gerarToken(String emailUsuario){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tempoDeExpiracao);

        return Jwts.builder()
                .setSubject(emailUsuario) // O "dono" do token é o email
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(token)
                .compact();
    }

    public Key getSecretKey() {
        return token;
    }
    
}