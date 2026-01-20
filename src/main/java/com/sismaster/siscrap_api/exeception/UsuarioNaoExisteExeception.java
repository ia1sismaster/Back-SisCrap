package com.sismaster.siscrap_api.exeception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UsuarioNaoExisteExeception extends RuntimeException{

    public UsuarioNaoExisteExeception(){
        super("Usuário não existe");
    }


    
}
