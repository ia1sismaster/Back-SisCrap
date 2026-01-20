package com.sismaster.siscrap_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sismaster.siscrap_api.model.Comandos;
import com.sismaster.siscrap_api.model.Usuario;

public interface ComandosRespository extends JpaRepository<Comandos,Long> {

    Comandos findByUsuario(Usuario usuario);
    
}
