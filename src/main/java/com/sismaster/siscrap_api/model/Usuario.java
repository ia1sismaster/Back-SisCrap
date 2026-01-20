package com.sismaster.siscrap_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usuarioId;
    
    @NotNull
    private String email;
    @NotNull
    private String senha;
    private String hash;
    private String nome;

    public Usuario(String email, String senha, String nome){
        this.email = email;
        this.senha = senha;
        this.nome = nome;
    }
}


