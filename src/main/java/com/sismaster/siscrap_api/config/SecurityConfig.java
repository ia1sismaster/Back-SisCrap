package com.sismaster.siscrap_api.config;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; 
import java.util.Arrays;
import com.sismaster.siscrap_api.component.SecurityFilter;



@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Libera o Login e Cadastro para todo mundo (ajuste a rota se for diferente)
                        .requestMatchers(HttpMethod.POST, "/api/usuario/login", "/api/usuario/cadastro").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuario/robo","/api/tarefa/entregar","/api/tarefa/pegar","/api/tarefa/liberar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/comandos/ativo").permitAll()
                        // Qualquer outra rota precisa estar logado (ter token)
                        .anyRequest().authenticated())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class) 
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Libera o seu Frontend
        configuration.setAllowedOrigins(Arrays.asList("https://front-sis-crap.vercel.app"));
        
        // Libera os métodos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Libera todos os headers (Inclusive Authorization)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Permite credenciais se necessário
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica para todas as rotas
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}