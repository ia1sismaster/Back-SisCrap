package com.sismaster.siscrap_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestCadDto {
    private String email;
    private String senha;
    private String nome;  
}
