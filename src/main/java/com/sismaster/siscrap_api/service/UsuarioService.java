package com.sismaster.siscrap_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.micrometer.observation.autoconfigure.ObservationProperties.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sismaster.siscrap_api.component.TokenGenerator;
import com.sismaster.siscrap_api.dto.UsuarioRequestCadDto;
import com.sismaster.siscrap_api.dto.UsuarioRequestDto;
import com.sismaster.siscrap_api.dto.UsuarioRequestSiteDto;
import com.sismaster.siscrap_api.dto.UsuarioResponseDto;
import com.sismaster.siscrap_api.exeception.UsuarioNaoExisteExeception;
import com.sismaster.siscrap_api.model.Usuario;
import com.sismaster.siscrap_api.repository.UsuarioRepository;

import io.micrometer.core.ipc.http.HttpSender.Response;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder encoder;

    private final StorageService storageService;
    private final TokenGenerator tokenGenerator;
    public UsuarioService(StorageService storageService, TokenGenerator tokenGenerator){
        this.tokenGenerator = tokenGenerator;
        this.storageService = storageService;
    }

    public ResponseEntity<UsuarioResponseDto> cadastro(UsuarioRequestCadDto usuarioDto){
    
        if(usuarioRepository.existsByEmail(usuarioDto.getEmail())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        String senha_hash = encoder.encode(usuarioDto.getSenha());

        Usuario usuario = new Usuario(usuarioDto.getEmail(), senha_hash, usuarioDto.getNome());
        
        usuarioRepository.save(usuario);

        String token = tokenGenerator.gerarToken(usuario.getEmail());
        String nome = doisPrimeirosNomes(usuario.getNome());
        UsuarioResponseDto usuarioResponse = new UsuarioResponseDto("Usuario Cadastrado", usuario.getUsuarioId(), "sucesso", token, nome, usuario.getEmail());

        storageService.criarPastaUsuario(usuario.getUsuarioId());

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioResponse);
     
    }

    public ResponseEntity<UsuarioResponseDto> loginSite(UsuarioRequestSiteDto usuarioDto){

        Usuario usuario = usuarioRepository.findByEmail(usuarioDto.getEmail())
            .orElseThrow(() -> {
                return new UsuarioNaoExisteExeception();
            });

        if(!encoder.matches(usuarioDto.getSenha(), usuario.getSenha())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String token = tokenGenerator.gerarToken(usuario.getEmail());
        String nome = doisPrimeirosNomes(usuario.getNome());

        UsuarioResponseDto usuarioResponse = new UsuarioResponseDto("Logado", usuario.getUsuarioId(), "sucesso", token, nome, usuario.getEmail());

        return ResponseEntity.ok(usuarioResponse);

    }

    public ResponseEntity<UsuarioResponseDto> loginRobo(UsuarioRequestDto usuarioDto){

         Usuario usuario = usuarioRepository.findByEmail(usuarioDto.getEmail())
            .orElseThrow(() -> {
                return new UsuarioNaoExisteExeception();
            });

        if(!encoder.matches(usuarioDto.getSenha(), usuario.getSenha())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        UsuarioResponseDto usuarioResponse;
        System.out.println( "HASH: "+usuarioDto.getHash());
        if(usuario.getHash() != null){

            if(usuario.getHash() == usuarioDto.getHash()){
                usuarioResponse = new UsuarioResponseDto("Logado", usuario.getUsuarioId(), "sucesso", null,null,null);
            }else{
                usuarioResponse = new UsuarioResponseDto("Não autorizado, maquina diferente do cadastro", "erro");
            }

        }else{

            usuario.setHash(usuarioDto.getHash());
            usuarioRepository.save(usuario);

            usuarioResponse = new UsuarioResponseDto("Logado, maquina cadastrada", usuario.getUsuarioId(), "sucesso", null,null,null);
        }

        return ResponseEntity.ok(usuarioResponse);

    }

    public String doisPrimeirosNomes(String nome){

        if(nome.isBlank() || nome == null){
            return "";
        }


        String[] partesNome = nome.trim().split("\\s+");

        if(partesNome.length == 1){
            return partesNome[0];
        }

        return partesNome[0]+" "+partesNome[1];

    }
    
}
