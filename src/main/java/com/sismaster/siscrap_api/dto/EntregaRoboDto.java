package com.sismaster.siscrap_api.dto;

import java.util.List;

import lombok.Data;

@Data
public class EntregaRoboDto {
    private String email;
    private String hash;
    private List<ResultadoRoboDto> resultados;
}
