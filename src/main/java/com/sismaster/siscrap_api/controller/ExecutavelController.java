package com.sismaster.siscrap_api.controller;

import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sismaster.siscrap_api.service.StorageService;

@RestController
@RequestMapping("/api/exe")
public class ExecutavelController {

    private final StorageService storageService;

    public ExecutavelController(StorageService storageService){
        this.storageService = storageService;
    }

    @GetMapping()
    public Resource baixarExe(@RequestParam String nomeArquivo){

        return storageService.baixarExe(nomeArquivo);

    }
    
}
