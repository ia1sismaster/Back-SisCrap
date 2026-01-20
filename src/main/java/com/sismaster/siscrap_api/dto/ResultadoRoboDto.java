package com.sismaster.siscrap_api.dto;

import com.sismaster.siscrap_api.model.Tarefas.StatusRaspagem;

import lombok.Data;

@Data
public class ResultadoRoboDto {
    private Long id;
    private String termo_busca;
    private String titulo;
    private String preco;
    private String preco_desc;
    private String link;
    private Integer score;
    private StatusRaspagem status;
}
