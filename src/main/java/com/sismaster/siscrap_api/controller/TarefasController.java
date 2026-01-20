package com.sismaster.siscrap_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sismaster.siscrap_api.dto.AuthRoboDto;
import com.sismaster.siscrap_api.dto.CorrecaoSimilarDto;
import com.sismaster.siscrap_api.dto.EntregaRoboDto;
import com.sismaster.siscrap_api.dto.LiberarTarefaRequestDto;
import com.sismaster.siscrap_api.dto.TarefasDto;
import com.sismaster.siscrap_api.dto.TarefasResponseDto;
import com.sismaster.siscrap_api.service.TarefasService;

import io.micrometer.core.ipc.http.HttpSender.Response;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/tarefa")
public class TarefasController {

    private final TarefasService tarefasService;

    public TarefasController(TarefasService tarefasService){
        this.tarefasService = tarefasService;
    }
    
    @PostMapping("/pegar")
    public ResponseEntity<List<TarefasDto>> pegarTarefas(@RequestBody AuthRoboDto authRoboDto) {
        
        
        return tarefasService.pegarTarefaRobo(authRoboDto);
    }

    @PostMapping("/entregar")
    public ResponseEntity<?> entregarTarefas(@RequestBody EntregaRoboDto entregaRoboDto) {

            tarefasService.entregarTarefas(entregaRoboDto);
            return ResponseEntity.ok(Map.of("msg","Salvo"));
      
       
    }

    @PostMapping("/liberar")
    public ResponseEntity<?> liberarTarefa(@RequestBody LiberarTarefaRequestDto dto) {
        tarefasService.liberarTarefas(dto);
        
        return ResponseEntity.ok(Map.of("msg","Liberado"));
    }

    @GetMapping("/liberar-bloqueado")
    public ResponseEntity<?> liberarTarefaBloqueada(@RequestParam Long arquivoId){
        tarefasService.liberarTarefasBloqueadas(arquivoId);

        return ResponseEntity.ok(Map.of("msg", "Tarefas liberadas"));

    }


    @GetMapping("/listar")
    public List<TarefasResponseDto> listarTarefasPorId(@RequestParam Long arquivoId){

        return tarefasService.listarTarefas(arquivoId);
    }

    @PutMapping("/correcao-similar")
    public ResponseEntity<?> correcaoSimilar(@RequestBody CorrecaoSimilarDto dto){
        tarefasService.correcaoSimilar(dto);

        return ResponseEntity.ok(Map.of("msg", "Itens corrigidos"));
    }

    @PutMapping("/aprovar-similiar")
    public ResponseEntity<?> aprovarSimilar(@RequestParam Long tarefaId){

        tarefasService.similarAprovado(tarefaId);

        return ResponseEntity.ok(Map.of("msg", "Item aprovado"));

    }
    
    
}
