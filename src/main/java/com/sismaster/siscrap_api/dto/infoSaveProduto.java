package com.sismaster.siscrap_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class infoSaveProduto {
    private String nomeProduto;
    private String custoPlanilha;
    private int qtdProduto;
}
