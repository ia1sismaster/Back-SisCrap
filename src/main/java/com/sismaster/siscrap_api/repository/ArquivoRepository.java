package com.sismaster.siscrap_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sismaster.siscrap_api.model.Arquivo;
import com.sismaster.siscrap_api.model.Usuario;

import java.util.List;


public interface ArquivoRepository extends JpaRepository<Arquivo,Long> {

    Boolean existsByNomeArquivo(String nomeArquivo);
    List<Arquivo> findByUsuario(Usuario usuario);
    
}
