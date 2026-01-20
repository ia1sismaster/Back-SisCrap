package com.sismaster.siscrap_api.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data   
@NoArgsConstructor
@AllArgsConstructor
public class NovoPrecoProdutoDto {
    private Long produtoId;
    private BigDecimal novoValor;
}
