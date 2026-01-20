package com.sismaster.siscrap_api.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArquivoRequestDto {
    private MultipartFile file;
    private String colNome;
    private String colPreco;
    private String colFim;
    private String colQtd;
    private int linhaInicio;
    private int percentualCusto;
    private int percentualLucro;
    private Long usuarioId;
}
