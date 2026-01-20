package com.sismaster.siscrap_api.dto;

import lombok.Data;

@Data
public class CorrecaoSimilarDto {
    private String titulo;
    private String preco;
    private String precoDesc;
    private String link;
    private Long tarefaId;
}
