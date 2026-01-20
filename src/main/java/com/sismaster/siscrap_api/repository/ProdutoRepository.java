package com.sismaster.siscrap_api.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sismaster.siscrap_api.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Modifying
    @Query("""
                update Produto p
                   set p.precoVenda = :preco
                 where p.produtoId = :id
            """)
    int atualizarPreco(Long id, BigDecimal preco);

}
