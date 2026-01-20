package com.sismaster.siscrap_api.dto;

import java.math.BigDecimal;

import com.sismaster.siscrap_api.model.Tarefas.StatusRaspagem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseProduto {
    private Long idProduto;
    private String nome;
    private String codBarras;
    private BigDecimal custo;
    private BigDecimal precoVenda;
    private int qtdProduto;
    private String precoMercadoLivre;
    private String status;




}
