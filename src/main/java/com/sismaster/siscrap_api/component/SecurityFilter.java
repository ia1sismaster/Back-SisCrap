package com.sismaster.siscrap_api.component;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sismaster.siscrap_api.component.TokenValidador;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenValidador tokenValidador; 

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var token = recuperarToken(request);

        // 1. O Token é válido? (Assinatura e Data batem?)
        if (token != null && tokenValidador.isTokenValid(token)) {
            
            // 2. Extrai o email de dentro do token
            var emailUsuario = tokenValidador.extractSubject(token);

            // 3. Cria a autenticação APENAS com os dados do token
            // - Principal: passamos o email (String)
            // - Credentials: null (não temos senha)
            // - Authorities: lista vazia (se precisar de roles, teria que extrair do token também)
            var authentication = new UsernamePasswordAuthenticationToken(emailUsuario, null, Collections.emptyList());
            
            // 4. Libera o acesso no Spring
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}