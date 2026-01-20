package com.sismaster.siscrap_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sismaster.siscrap_api.model.Usuario;
import java.util.List;


public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    Boolean existsByEmail(String email);
    Optional<Usuario> findByEmail(String email);
    
}
