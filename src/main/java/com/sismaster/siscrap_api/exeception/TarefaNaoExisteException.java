package com.sismaster.siscrap_api.exeception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TarefaNaoExisteException extends RuntimeException {

    public TarefaNaoExisteException(){
        super("Tarefa não Existe");
    }
}
