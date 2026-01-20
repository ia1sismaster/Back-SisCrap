package com.sismaster.siscrap_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioResponseDto {
    private String msg;
    private String erro;
    private Long usuarioId;
    private String status;
    private String token;
    private String nome;
    private String email;



    public UsuarioResponseDto(String msg, Long usuarioId, String status, String token, String nome, String email){
        this.msg = msg;
        this.usuarioId = usuarioId;
        this.status = status;
        this.token = token;
        this.nome = nome;
        this.email = email;

        
    }

    public UsuarioResponseDto(String erro, String status){
        this.erro = erro;
        this.status = status;
    }
}
