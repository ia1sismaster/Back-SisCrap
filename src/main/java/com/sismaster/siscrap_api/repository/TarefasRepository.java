package com.sismaster.siscrap_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.sismaster.siscrap_api.model.Arquivo;
import com.sismaster.siscrap_api.model.Produto;
import com.sismaster.siscrap_api.model.Tarefas;
import com.sismaster.siscrap_api.model.Tarefas.StatusRaspagem;

public interface TarefasRepository extends JpaRepository<Tarefas, Long> {
    List<Tarefas> findTop10ByStatusRaspagemOrderByTarefasIdAsc(StatusRaspagem status);

    @Modifying
    @Transactional
    @Query("update Tarefas t set t.statusRaspagem = :novoStatus where t.tarefasId in :ids")
    int atualizarStatusEmLote(@Param("ids") List<Long> ids,
            @Param("novoStatus") StatusRaspagem novoStatus);

    @Modifying
    @Query("""
                update Tarefas t
                   set t.statusRaspagem = :novoStatus
                 where t.statusRaspagem = :statusAntigo
                   and t.arquivo = :arquivo
            """)
    void atualizarStatusPorArquivo(
            @Param("statusAntigo") StatusRaspagem statusAntigo,
            @Param("novoStatus") StatusRaspagem novoStatus,
            @Param("arquivo") Arquivo arquivo);

    long countByArquivo(Arquivo arquivo);

    long countByArquivoAndStatusRaspagemNot(Arquivo arquivo, StatusRaspagem status);

    long countByArquivoAndStatusRaspagem(Arquivo arquivo, StatusRaspagem status);

    List<Tarefas> findByArquivoAndStatusRaspagem(Arquivo arquivo, StatusRaspagem statusRaspagem);

    List<Tarefas> findByArquivo(Arquivo arquivo);
    
    List<Tarefas> findByArquivoArquivoId(Long arquivoId);

    void deleteByArquivo_ArquivoId(Long arquivoId);

    Boolean existsByTermoBuscaAndArquivo(String nomeProduto, Arquivo arquivo);



}
