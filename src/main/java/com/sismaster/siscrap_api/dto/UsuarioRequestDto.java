package com.sismaster.siscrap_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequestDto {
    private String email;
    private String senha;
    private String hash;   
}
