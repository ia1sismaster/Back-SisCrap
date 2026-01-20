package com.sismaster.siscrap_api.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sismaster.siscrap_api.dto.NovoPrecoProdutoDto;
import com.sismaster.siscrap_api.dto.ResponseProduto;
import com.sismaster.siscrap_api.dto.infoSaveProduto;
import com.sismaster.siscrap_api.model.Arquivo;
import com.sismaster.siscrap_api.model.Produto;
import com.sismaster.siscrap_api.model.Tarefas;
import com.sismaster.siscrap_api.model.Tarefas.StatusRaspagem;
import com.sismaster.siscrap_api.repository.ArquivoRepository;
import com.sismaster.siscrap_api.repository.ProdutoRepository;
import com.sismaster.siscrap_api.repository.TarefasRepository;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private TarefasRepository tarefasRepository;

    public Produto salvarProduto(infoSaveProduto dto, Arquivo arquivo) {

        BigDecimal custoProduto = obterCusto(dto.getCustoPlanilha(), arquivo.getPercentualCusto());

        BigDecimal precoVenda = obterValorVenda(custoProduto, arquivo.getPercentualLucro());

        Produto produto = new Produto(dto.getNomeProduto(), custoProduto, precoVenda, dto.getQtdProduto());
        produtoRepository.save(produto);

        String codigo = String.format("PROD-%06d", produto.getProdutoId());

        produto.setCodigoBarras(codigo);
        produtoRepository.save(produto);

        return produto;

    }

    public BigDecimal obterValorVenda(BigDecimal custo, int percentual) {

        BigDecimal percentualVenda = BigDecimal.valueOf(percentual).divide(BigDecimal.valueOf(100));
        BigDecimal lucro = custo.multiply(percentualVenda);
        return custo.add(lucro);
    }

    public BigDecimal obterCusto(String valor, int percentual) {

        BigDecimal custoPlanilha = stringParaBigDecimal(valor);
        BigDecimal percentualCusto = BigDecimal.valueOf(percentual).divide(BigDecimal.valueOf(100));

        return custoPlanilha.multiply(percentualCusto);
    }

    public BigDecimal stringParaBigDecimal(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Valor nulo");
        }

        // Mantém apenas dígitos, ponto, vírgula e sinal
        String filtrado = valor.replaceAll("[^0-9.,-]", "");

        if (filtrado.isBlank()) {
            throw new IllegalArgumentException("Valor inválido: " + valor);
        }

        // Se houver vírgula, assume que ela é separador decimal
        if (filtrado.contains(",")) {
            // Remove TODOS os pontos (milhar ou lixo)
            filtrado = filtrado.replace(".", "");
            // Converte vírgula em ponto (decimal canônico)
            filtrado = filtrado.replace(",", ".");
        }

        // Validação final: só um ponto e no máximo um sinal
        if (!filtrado.matches("-?\\d+(\\.\\d+)?")) {
            throw new IllegalArgumentException("Formato inválido: " + valor);
        }

        return new BigDecimal(filtrado);
    }

    public List<ResponseProduto> listarProduto(Long idArquivo) {

        List<Tarefas> tarefas = tarefasRepository.findByArquivoArquivoId(idArquivo);

        if (tarefas.isEmpty()) {
            return List.of();
        }

        return tarefas.stream()
                .filter(t -> t.getProduto() != null) // segurança
                .map(t -> {
                    Produto p = t.getProduto();

                    String precoMercadoLivre;

                    
                         if ("Sem preco".equalsIgnoreCase(t.getPrecoDesconto())) {
                        precoMercadoLivre = t.getPreco();
                    } else {
                        precoMercadoLivre = t.getPrecoDesconto();
                    }

                    return new ResponseProduto(
                            p.getProdutoId(),
                            p.getNomeProduto(),
                            p.getCodigoBarras(),
                            p.getCusto(),
                            p.getPrecoVenda(),
                            p.getQtd(),
                            precoMercadoLivre,
                            t.getStatusRaspagem().toString()
                    );
                })
                .toList();

    }

    
    @Transactional
    public void editarPreco(List<NovoPrecoProdutoDto> novosPrecos){

        for (NovoPrecoProdutoDto dto : novosPrecos) {

            produtoRepository.atualizarPreco(dto.getProdutoId(), dto.getNovoValor());
            
        }



    }
}
