package com.sismaster.siscrap_api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sismaster.siscrap_api.dto.AuthRoboDto;
import com.sismaster.siscrap_api.dto.CorrecaoSimilarDto;
import com.sismaster.siscrap_api.dto.EntregaRoboDto;
import com.sismaster.siscrap_api.dto.LiberarTarefaRequestDto;
import com.sismaster.siscrap_api.dto.ResultadoRoboDto;
import com.sismaster.siscrap_api.dto.TarefasDto;
import com.sismaster.siscrap_api.dto.TarefasResponseDto;
import com.sismaster.siscrap_api.model.Arquivo;
import com.sismaster.siscrap_api.model.Tarefas;
import com.sismaster.siscrap_api.model.Usuario;
import com.sismaster.siscrap_api.model.Tarefas.StatusRaspagem;
import com.sismaster.siscrap_api.repository.ArquivoRepository;
import com.sismaster.siscrap_api.repository.TarefasRepository;
import com.sismaster.siscrap_api.repository.UsuarioRepository;
import com.sismaster.siscrap_api.exeception.ArquivoNaoExisteException;
import com.sismaster.siscrap_api.exeception.UsuarioNaoExisteExeception;
import com.sismaster.siscrap_api.exeception.TarefaNaoExisteException;

@Service
public class TarefasService {

    @Autowired
    private TarefasRepository tarefasRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ArquivoRepository arquivoRepository;

    private final AuthService authService;

    public TarefasService(AuthService authService) {
        this.authService = authService;
    }

    @Transactional
    public ResponseEntity<List<TarefasDto>> pegarTarefaRobo(AuthRoboDto authRobo) {

        Usuario usuario = authService.authUsuarioRobo(authRobo.getEmail(), authRobo.getHash());
        System.out.println(usuario);

        List<Tarefas> tarefas = tarefasRepository.findTop10ByStatusRaspagemOrderByTarefasIdAsc(StatusRaspagem.PENDENTE);

        if (tarefas.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<TarefasDto> retorno = tarefas.stream()
                .map(t -> new TarefasDto(
                        t.getTermoBusca(),
                        t.getTarefasId()))
                .toList();

        tarefas.forEach(t -> t.setStatusRaspagem(StatusRaspagem.EM_PROCESSAMENTO));
        tarefasRepository.saveAll(tarefas);

        return ResponseEntity.ok(retorno);

    }

    @Transactional
    public void entregarTarefas(EntregaRoboDto dto) {

        Usuario usuario = authService.authUsuarioRobo(dto.getEmail(), dto.getHash());

        for (ResultadoRoboDto item : dto.getResultados()) {

            Tarefas tarefa = tarefasRepository
                    .findById(item.getId())
                    .orElse(null);

            if (tarefa == null)
                continue;

            tarefa.setTituloEncontrado(item.getTitulo());
            tarefa.setPreco(item.getPreco());
            tarefa.setPrecoDesconto(item.getPreco_desc());
            tarefa.setLink(item.getLink());
            tarefa.setMatchScore(item.getScore());
            tarefa.setStatusRaspagem(item.getStatus());
        }
    }

    public void liberarTarefas(LiberarTarefaRequestDto dto){

        Usuario usuario = authService.authUsuarioRobo(dto.getEmail(), dto.getHash());

        tarefasRepository.atualizarStatusEmLote(dto.getIds(), StatusRaspagem.PENDENTE);
        


    }

    @Transactional
    public void liberarTarefasBloqueadas(Long arquivoId){

        Arquivo arquivo = arquivoRepository.findById(arquivoId)
            .orElseThrow(() -> {
                return new ArquivoNaoExisteException();
            });

        
            tarefasRepository.atualizarStatusPorArquivo(StatusRaspagem.BLOQUEADO, StatusRaspagem.PENDENTE, arquivo);

    }

    public List<TarefasResponseDto> listarTarefas(Long arquivoId){

        Arquivo arquivo = arquivoRepository.findById(arquivoId)
            .orElseThrow(() -> {
                return new ArquivoNaoExisteException();
            });

        List<Tarefas> tarefas = tarefasRepository.findByArquivo(arquivo);

        return tarefas.stream()
        .map(t -> {
            TarefasResponseDto dto = new TarefasResponseDto();

            dto.setId(t.getTarefasId());
            dto.setTermo_busca(t.getTermoBusca());
            dto.setTitulo_encontrado(t.getTituloEncontrado());
            dto.setPreco(t.getPreco());
            dto.setPreco_desc(t.getPrecoDesconto());
            dto.setLink(t.getLink());
            dto.setScore(t.getMatchScore());
            dto.setStatus(t.getStatusRaspagem());

            return dto;
        })
        .toList();


    }

    public void correcaoSimilar(CorrecaoSimilarDto dto){
        
        Tarefas tarefa = tarefasRepository.findById(dto.getTarefaId())
            .orElseThrow(() -> {
                return new TarefaNaoExisteException();
            });

        if(!dto.getTitulo().isEmpty()){
            tarefa.setTituloEncontrado(dto.getTitulo());
        }
        if(!dto.getPreco().isEmpty()){
            tarefa.setPreco(dto.getPreco());
            if(!dto.getPrecoDesc().isEmpty()){
                tarefa.setPrecoDesconto(dto.getPrecoDesc());
            }
        }
        if(!dto.getLink().isEmpty()){
            tarefa.setLink(dto.getLink());
        }

        tarefasRepository.save(tarefa);
    }

    public void similarAprovado(Long tarefaId){
        Tarefas tarefa = tarefasRepository.findById(tarefaId)
            .orElseThrow(() -> {
                return new TarefaNaoExisteException();
            });
        
        tarefa.setStatusRaspagem(StatusRaspagem.SUCESSO);
        tarefa.setMatchScore(100);

        tarefasRepository.save(tarefa);
    }


}
