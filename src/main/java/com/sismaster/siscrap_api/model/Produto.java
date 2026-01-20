package com.sismaster.siscrap_api.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long produtoId;

    private String nomeProduto;
    private BigDecimal custo;
    private BigDecimal precoVenda;
    private int qtd;
    private String codigoBarras;


    public Produto(String nomeProduto, BigDecimal custo, BigDecimal precoVenda, int qtd){
        this.nomeProduto = nomeProduto;
        this.custo = custo;
        this.precoVenda = precoVenda;
        this.qtd = qtd;
    }
    
}
