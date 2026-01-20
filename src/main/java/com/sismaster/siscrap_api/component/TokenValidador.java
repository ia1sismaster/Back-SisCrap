package com.sismaster.siscrap_api.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenValidador {
    
    @Autowired
    private TokenGenerator tokenGenerator;

    // Verifica se a assinatura bate e se não expirou
    public boolean isTokenValid(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(tokenGenerator.getSecretKey()) // Pega a chave do outro componente
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Retorna true se a data de expiração for depois de AGORA
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            // Se a assinatura for falsa ou token expirado, lança exceção e cai aqui
            return false; 
        }
    }

    // Apenas extrai o email (assume que o token já foi validado antes)
    public String extractSubject(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(tokenGenerator.getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}