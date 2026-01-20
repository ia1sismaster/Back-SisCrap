package com.sismaster.siscrap_api.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sismaster.siscrap_api.dto.AuthRoboDto;
import com.sismaster.siscrap_api.service.ComandoRoboService;

@RestController
@RequestMapping("/api/comandos")
public class ComandosController {

    private final ComandoRoboService comandoRoboService;

    public ComandosController(ComandoRoboService comandoRoboService){
        this.comandoRoboService  = comandoRoboService;
    }

    @GetMapping("/ativo")
    public ResponseEntity<?> roboAtivo(@RequestParam String email, @RequestParam String hash){

        Boolean ativo = comandoRoboService.roboAtivo(email, hash);

        return  ResponseEntity.ok(
        Map.of("ativo", ativo)
    );
    }

    
}
