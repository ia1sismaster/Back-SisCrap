package com.sismaster.siscrap_api.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Tarefas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tarefasId;
    private String termoBusca;
    private String tituloEncontrado;
    private String preco;
    private String precoDesconto;
    @Column(columnDefinition = "TEXT")
    private String link;
    private Integer matchScore;
    @NotNull
    @Enumerated(EnumType.STRING)
    private StatusRaspagem statusRaspagem;
    @NotNull
    @Enumerated(EnumType.STRING)
    private StatusRevisao statusRevisao;


    @NotNull
    @ManyToOne
    @JoinColumn(name = "arquivoId")
    private Arquivo arquivo;
    
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "produtoId")
    private Produto produto;

    @PrePersist
    public void aoInserir(){
        this.statusRaspagem = StatusRaspagem.PENDENTE;
        this.statusRevisao = StatusRevisao.NA;
    }

    public Tarefas(String termoBusca, Arquivo arquivo){
        this.termoBusca = termoBusca;
        this.arquivo = arquivo;

    }



    public enum StatusRaspagem{
        EM_PROCESSAMENTO, PENDENTE, SUCESSO, SIMILAR, NAO_ENCONTRADO, BLOQUEADO

    }
    public enum StatusRevisao{
        NA, APROVADO, REPROVADO
    }
}
