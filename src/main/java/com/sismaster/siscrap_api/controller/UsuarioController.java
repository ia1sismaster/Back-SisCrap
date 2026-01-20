package com.sismaster.siscrap_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sismaster.siscrap_api.dto.UsuarioRequestCadDto;
import com.sismaster.siscrap_api.dto.UsuarioRequestDto;
import com.sismaster.siscrap_api.dto.UsuarioRequestSiteDto;
import com.sismaster.siscrap_api.dto.UsuarioResponseDto;
import com.sismaster.siscrap_api.service.UsuarioService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {
    
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }
    

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDto> cadastro(@RequestBody UsuarioRequestCadDto usuarioDto) {
        

        
        return usuarioService.cadastro(usuarioDto);
    }

     @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDto> loginSite(@RequestBody UsuarioRequestSiteDto usuarioDto) {
      
        
        return usuarioService.loginSite(usuarioDto);
    }

     @PostMapping("/robo")
    public ResponseEntity<UsuarioResponseDto> loginRobo(@RequestBody UsuarioRequestDto usuarioDto) {
      
        
        return usuarioService.loginRobo(usuarioDto);
    }

    
}
