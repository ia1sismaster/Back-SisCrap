package com.sismaster.siscrap_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sismaster.siscrap_api.dto.ArquivoRequestDto;
import com.sismaster.siscrap_api.dto.ArquivoStatusResponseDto;
import com.sismaster.siscrap_api.service.ArquivoService;

import java.io.File;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;




@RestController
@RequestMapping("/api/arquivo")
public class ArquivoController {
    
    private final ArquivoService arquivoService;

    public ArquivoController(ArquivoService arquivoService){
        this.arquivoService = arquivoService;
    }

    @PostMapping(value = "/upload" ,consumes = "multipart/form-data")
    public ResponseEntity<String> upload(@ModelAttribute ArquivoRequestDto arquivoRequest) {
       
        try {
           arquivoService.cadastroArquivo(arquivoRequest);
           
           return ResponseEntity.status(HttpStatus.ACCEPTED).body("Upload sucesso");
        } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Upload erro: "+e);
        }
    }

    @GetMapping("/listar")
    public List<ArquivoStatusResponseDto> listarSite(@RequestParam Long usuarioId) {
        
        
        return arquivoService.listarArquivos(usuarioId);
    }

    // @GetMapping("/download")
    // public File downloadArquivo(@RequestParam Long idUsuario, @RequestParam Long idArquivo){

    //     return arquivoService.gerarArquivoPreenchido(idUsuario, idArquivo);
        
    // }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadArquivo(@RequestParam Long idUsuario, @RequestParam Long idArquivo){

        File file = arquivoService.gerarArquivoPreenchido(idUsuario, idArquivo);

        Resource resource = new FileSystemResource(file);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
    }


    @DeleteMapping()
    public void deletarArquivo(@RequestParam Long idArquivo){
        arquivoService.excluirArquivo(idArquivo);

    }
    
    
}
