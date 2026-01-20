package com.sismaster.siscrap_api.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Arquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long arquivoId;
    @NotNull
    private String nomeArquivo;
    @NotNull
    private String celNome;
    @NotNull
    private String celFim;
    @NotNull
    private String celPreco;
    @NotNull
    private String celQtd;
    @NotNull
    private int linhaInicio;
    @NotNull
    private int percentualCusto;
    @NotNull
    private int percentualLucro;

    private LocalDateTime dataCriacao;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "usuarioId")
    private Usuario usuario;

    @PrePersist
    public void aoInserir(){
        this.dataCriacao = LocalDateTime.ofInstant(Instant.now(), ZoneId.of("America/Sao_Paulo"));
        this.status = Status.PROCESSANDO;
    }

    public Arquivo(String nomeArquivo, String celNome, String celPreco, String celQtd, String celFim, int linhaInicio, int percentualCusto, int percentualLucro, Usuario usuario){
        this.nomeArquivo = nomeArquivo;
        this.celNome = celNome;
        this.celPreco = celPreco;
        this.celQtd = celQtd;
        this.celFim = celFim;
        this.linhaInicio = linhaInicio;
        this.percentualCusto = percentualCusto;
        this.percentualLucro = percentualLucro;
        this.usuario = usuario;
    }

    public enum Status{
        PROCESSANDO, CONCLUIDO

    }
    
}
