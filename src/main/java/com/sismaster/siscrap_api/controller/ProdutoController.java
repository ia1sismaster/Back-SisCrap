package com.sismaster.siscrap_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sismaster.siscrap_api.dto.NovoPrecoProdutoDto;
import com.sismaster.siscrap_api.dto.ResponseProduto;
import com.sismaster.siscrap_api.model.Tarefas;
import com.sismaster.siscrap_api.service.ProdutoService;

@RestController
@RequestMapping("/api/produto")
public class ProdutoController {
    
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService){
        this.produtoService = produtoService;
    }

    @GetMapping()
    public List<ResponseProduto> listarProduto(@RequestParam Long arquivoId){
       
        return produtoService.listarProduto(arquivoId);

    }

    @PutMapping("/editar")
    public void novoPreco(@RequestBody List<NovoPrecoProdutoDto> dto){

        produtoService.editarPreco(dto);

    }
    
}
