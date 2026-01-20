package com.sismaster.siscrap_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sismaster.siscrap_api.dto.AuthRoboDto;
import com.sismaster.siscrap_api.model.Comandos;
import com.sismaster.siscrap_api.model.Usuario;
import com.sismaster.siscrap_api.repository.ComandosRespository;

@Service
public class ComandoRoboService {

    @Autowired
    private ComandosRespository comandosRespository;

    private final AuthService authService;

    public ComandoRoboService(AuthService authService){
        this.authService = authService;
    }


    public boolean roboAtivo(String email, String hash){

        Usuario usuario = authService.authUsuarioRobo(email, hash);

        Comandos comando = comandosRespository.findByUsuario(usuario);

        return comando.getRoboAtivo();



    }
    
}
