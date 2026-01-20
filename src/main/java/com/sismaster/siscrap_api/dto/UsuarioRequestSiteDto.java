package com.sismaster.siscrap_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequestSiteDto {
    private String email;
    private String senha; 
}
