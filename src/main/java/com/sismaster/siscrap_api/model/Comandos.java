package com.sismaster.siscrap_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Comandos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comandoId;

    private Boolean roboAtivo;
    private Boolean fezDownloadRobo;



    
    @OneToOne
    @JoinColumn(name = "usuarioId")
    private Usuario usuario;

    
}
