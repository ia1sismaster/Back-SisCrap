package com.sismaster.siscrap_api.exeception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ArquivoNaoExisteException extends RuntimeException{
    
    public ArquivoNaoExisteException(){
        super("Arquivo não existe");
    }
}
