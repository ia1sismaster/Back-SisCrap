package com.sismaster.siscrap_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sismaster.siscrap_api.dto.AuthRoboDto;
import com.sismaster.siscrap_api.exeception.UsuarioNaoExisteExeception;
import com.sismaster.siscrap_api.model.Usuario;
import com.sismaster.siscrap_api.repository.UsuarioRepository;

@Service
public class AuthService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    
    public Usuario authUsuarioRobo(String email, String hash){


        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> {
                return new UsuarioNaoExisteExeception();
            });

        if(!usuario.getHash().equals(hash)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Não autorizado");
        }

        return usuario;

    }

}
