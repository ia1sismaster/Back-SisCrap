package com.sismaster.siscrap_api.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sismaster.siscrap_api.dto.ArquivoRequestDto;
import com.sismaster.siscrap_api.dto.ArquivoStatusResponseDto;
import com.sismaster.siscrap_api.dto.infoSaveProduto;
import com.sismaster.siscrap_api.exeception.UsuarioNaoExisteExeception;
import com.sismaster.siscrap_api.exeception.ArquivoNaoExisteException;
import com.sismaster.siscrap_api.model.Arquivo;
import com.sismaster.siscrap_api.model.Tarefas;
import com.sismaster.siscrap_api.model.Tarefas.StatusRaspagem;
import com.sismaster.siscrap_api.model.Usuario;
import com.sismaster.siscrap_api.model.Produto;
import com.sismaster.siscrap_api.repository.ArquivoRepository;
import com.sismaster.siscrap_api.repository.TarefasRepository;
import com.sismaster.siscrap_api.repository.UsuarioRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellReference;

@Service
public class ArquivoService {

    @Autowired
    private ArquivoRepository arquivoRepository;
    @Autowired
    private TarefasRepository tarefasRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    private final StorageService storageService;
    private final ProdutoService produtoService;

    public ArquivoService(StorageService storageService, ProdutoService produtoService) {
        this.storageService = storageService;
        this.produtoService = produtoService;
    }

    public void cadastroArquivo(ArquivoRequestDto arquivoDto) {

        Usuario usuario = usuarioRepository.findById(arquivoDto.getUsuarioId())
                .orElseThrow(() -> {
                    return new UsuarioNaoExisteExeception();
                });

        String nomeArquivo = storageService.salvarArquivo(usuario.getUsuarioId(), arquivoDto.getFile());

        Arquivo arquivo = new Arquivo(nomeArquivo, arquivoDto.getColNome(), arquivoDto.getColPreco(),
                arquivoDto.getColQtd(),
                arquivoDto.getColFim(), arquivoDto.getLinhaInicio(), arquivoDto.getPercentualCusto(),
                arquivoDto.getPercentualLucro(), usuario);

        arquivoRepository.save(arquivo);

        salvandoTarefas(arquivo, arquivoDto);

    }

    @Transactional
    public void salvandoTarefas(Arquivo arquivo, ArquivoRequestDto arquivoDto) {

        MultipartFile file = arquivoDto.getFile();
        int linhaInicio = arquivoDto.getLinhaInicio() - 1;

        int indexNome = CellReference.convertColStringToIndex(arquivoDto.getColNome());
        int indexPreco = CellReference.convertColStringToIndex(arquivoDto.getColPreco());

        // Se não existe coluna de qtd no arquivo inteiro, fica null
        Integer indexQtd = (arquivoDto.getColQtd() != null)
                ? CellReference.convertColStringToIndex(arquivoDto.getColQtd())
                : null;

        try (InputStream input = file.getInputStream();
                XSSFWorkbook workbook = new XSSFWorkbook(input)) {

            XSSFSheet sheet = workbook.getSheetAt(0);

            // 1) LER TUDO e guardar "cru"
            List<infoSaveProduto> lidos = new ArrayList<>();

            for (int rowIndex = linhaInicio; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null)
                    continue;

                String nomeProduto = getCellValueAsString(row.getCell(indexNome));
                String custoProduto = getCellValueAsString(row.getCell(indexPreco));

                String qtdProduto = null;
                if (indexQtd != null) {
                    qtdProduto = getCellValueAsString(row.getCell(indexQtd));
                }

                if (nomeProduto.isEmpty())
                    continue;
                int qtd = parseQtd(qtdProduto);
                lidos.add(new infoSaveProduto(nomeProduto, custoProduto, qtd));
            }

            if (lidos.isEmpty())
                return;

            // 2) LIMPAR / AGRUPAR (nome + custo iguais => soma qtd)
            // Vou usar Map aqui porque é o jeito mais correto e eficiente de "limpar" com
            // soma.
            // Se qtd não existe no arquivo, a soma não importa (mantemos null).
            Map<String, infoSaveProduto> agrupados = new LinkedHashMap<>();

            for (infoSaveProduto item : lidos) {
                String nomeNorm = normalizar(item.getNomeProduto());
                String custoNorm = normalizar(item.getCustoPlanilha());

                String chave = nomeNorm + "||" + custoNorm;

                infoSaveProduto existente = agrupados.get(chave);
                if (existente == null) {
                    // primeira vez: entra no agrupado
                    agrupados.put(chave, item);
                } else {
                    // já existe: soma qtd (se houver coluna de qtd)
                    if (indexQtd != null) {
                        int qtdExistente = existente.getQtdProduto();
                        int qtdNova = item.getQtdProduto();
                        existente.setQtdProduto(qtdExistente + qtdNova);
                    }
                }
            }

            // 3) AGORA sim: salvar produtos/tarefas (1 por item único)
            List<Tarefas> tarefasParaSalvar = new ArrayList<>(agrupados.size());

            for (infoSaveProduto dtoInfo : agrupados.values()) {
                Produto produtoSalvo = produtoService.salvarProduto(dtoInfo, arquivo);

                Tarefas tarefa = new Tarefas();
                tarefa.setTermoBusca(dtoInfo.getNomeProduto());
                tarefa.setProduto(produtoSalvo);
                tarefa.setArquivo(arquivo);

                tarefasParaSalvar.add(tarefa);
            }

            tarefasRepository.saveAll(tarefasParaSalvar);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar planilha: " + e.getMessage(), e);
        }
    }

    // Helper para garantir string
    private String getCellValueAsString(Cell cell) {
        if (cell == null)
            return "";
        return cell.toString().trim();
    }

    // Normalização simples (ajuda a reduzir duplicados “invisíveis”)
    private String normalizar(String s) {
        if (s == null)
            return "";
        return s.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    // Quantidade: se vier null/vazio/texto, vira 0 (você pode ajustar)
    private int parseQtd(String qtd) {
        if (qtd == null)
            return 0;
        String limpa = qtd.trim();
        if (limpa.isEmpty())
            return 0;
        // Se vier "10.0" por causa do Excel, pega só a parte inteira
        limpa = limpa.replace(",", ".");
        try {
            double v = Double.parseDouble(limpa);
            return (int) Math.round(v);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public List<ArquivoStatusResponseDto> listarArquivos(Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoExisteExeception());

        List<Arquivo> arquivos = arquivoRepository.findByUsuario(usuario);

        List<ArquivoStatusResponseDto> resposta = new ArrayList<>();

        for (Arquivo arquivo : arquivos) {

            long total = tarefasRepository.countByArquivo(arquivo);

            long concluidos = tarefasRepository.countByArquivoAndStatusRaspagemNot(
                    arquivo,
                    StatusRaspagem.PENDENTE);

            long similar = tarefasRepository.countByArquivoAndStatusRaspagem(
                    arquivo,
                    StatusRaspagem.SIMILAR);

            String status;
            if (concluidos < total) {
                status = "processing";
            } else if (similar > 0) {
                status = "review";
            } else {
                status = "completed";
            }

            resposta.add(new ArquivoStatusResponseDto(
                    arquivo.getArquivoId(),
                    arquivo.getNomeArquivo(),
                    arquivo.getDataCriacao(),
                    concluidos,
                    total,
                    status));
        }

        return resposta;
    }

    public File gerarArquivoPreenchido(Long idUsuario, Long idArquivo) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(UsuarioNaoExisteExeception::new);

        Arquivo arquivo = arquivoRepository.findById(idArquivo)
                .orElseThrow(ArquivoNaoExisteException::new);

        List<Tarefas> tarefasSucesso = tarefasRepository.findByArquivoAndStatusRaspagem(
                arquivo,
                StatusRaspagem.SUCESSO);

        if (tarefasSucesso.isEmpty()) {
            throw new RuntimeException("Nenhuma tarefa com status SUCESSO encontrada");
        }

        File arquivoFisico = storageService
                .getArquivoPath(idUsuario, arquivo.getNomeArquivo())
                .toFile();

        preencherResultadosExcel(
                arquivoFisico,
                arquivo,
                tarefasSucesso);

        return arquivoFisico;
    }

    public void preencherResultadosExcel(
            File file,
            Arquivo arquivo,
            List<Tarefas> tarefas) {

        int linhaInicio = arquivo.getLinhaInicio() - 1;

        int colNome = CellReference.convertColStringToIndex(arquivo.getCelNome());
        int colPrecoProduto = CellReference.convertColStringToIndex(arquivo.getCelPreco());

        int colBase = CellReference.convertColStringToIndex(arquivo.getCelFim()) + 1;
        int colPreco = colBase;
        int colPrecoDesconto = colBase + 1;
        int colCusto = colBase + 2;
        int colVenda = colBase + 3;
        int colLink = colBase + 4;

        // CHAVE = (nome normalizado + custo canônico)
        Map<String, Tarefas> tarefasPorChave = tarefas.stream()
                .collect(Collectors.toMap(
                        t -> chaveBanco(t.getTermoBusca(), t.getProduto().getCusto()),
                        t -> t,
                        (a, b) -> a));

        try (InputStream input = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(input)) {

            XSSFSheet sheet = workbook.getSheetAt(0);

            criarCabecalhoPadrao(sheet, linhaInicio - 1, colPreco, colPrecoDesconto, colCusto, colVenda, colLink);

            for (int rowIndex = linhaInicio; rowIndex <= sheet.getLastRowNum(); rowIndex++) {

                Row row = sheet.getRow(rowIndex);
                if (row == null)
                    continue;

                Cell cellNome = row.getCell(colNome);
                if (cellNome == null)
                    continue;

                String nomeProduto = cellNome.toString().trim();
                if (nomeProduto.isEmpty())
                    continue;

                // LER custo/preço da própria planilha (mesma linha)
                String custoPlanilha = "";
                Cell cellCustoPlanilha = row.getCell(colPrecoProduto);
                if (cellCustoPlanilha != null) {
                    custoPlanilha = cellCustoPlanilha.toString().trim();
                }

                // ✅ usa a mesma lógica de chave (nome normalizado + custo canônico)
                String chave = chavePlanilha(nomeProduto, custoPlanilha, arquivo);

                Tarefas tarefa = tarefasPorChave.get(chave);
                System.out.println("Chave: " + chave);
                System.out.println("TAREFA: " + tarefa);
                if (tarefa == null)
                    continue;

                // PRECO
                if (tarefa.getPreco() != null) {
                    Cell cellPreco = row.getCell(colPreco, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cellPreco.setCellValue(tarefa.getPreco());
                }

                // PREÇO DESCONTO
                if (tarefa.getPrecoDesconto() != null) {
                    Cell cellDesc = row.getCell(colPrecoDesconto, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cellDesc.setCellValue(tarefa.getPrecoDesconto());
                }

                // CUSTO
                if (tarefa.getProduto().getCusto() != null) {
                    Cell cellDesc = row.getCell(colCusto, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cellDesc.setCellValue(tarefa.getProduto().getCusto().toString());
                }

                // VENDA
                if (tarefa.getProduto().getPrecoVenda() != null) {
                    Cell cellDesc = row.getCell(colVenda, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cellDesc.setCellValue(tarefa.getProduto().getPrecoVenda().toString());
                }

                // LINK
                if (tarefa.getLink() != null) {
                    Cell cellLink = row.getCell(colLink, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cellLink.setCellValue(tarefa.getLink());
                }
            }

            try (OutputStream out = new FileOutputStream(file)) {
                workbook.write(out);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao preencher Excel", e);
        }
    }

    private String chaveBanco(String nome, BigDecimal custoBanco) {
        String nomeNorm = normalizar(nome);

        String custoNorm = "";
        if (custoBanco != null) {
            custoNorm = custoBanco
                    .setScale(2, RoundingMode.HALF_UP)
                    .toPlainString();
        }

        return nomeNorm + "||" + custoNorm;
    }

    private String chavePlanilha(String nome, String custoPlanilhaTexto, Arquivo arquivo) {
        String nomeNorm = normalizar(nome);

        String custoNorm = "";
        if (custoPlanilhaTexto != null && !custoPlanilhaTexto.isBlank()) {
            BigDecimal custoBanco = produtoService.obterCusto(custoPlanilhaTexto, arquivo.getPercentualCusto());
            custoBanco = custoBanco.setScale(2, RoundingMode.HALF_UP);

            custoNorm = custoBanco.toPlainString();
        }

        return nomeNorm + "||" + custoNorm;
    }

    private void criarCabecalhoPadrao(Sheet sheet, int linhaCabecalho, int colPreco, int colPrecoDesconto,
            int colCusto, int colVenda, int colLink) {

        Row headerRow = sheet.getRow(linhaCabecalho);
        if (headerRow == null) {
            headerRow = sheet.createRow(linhaCabecalho);
        }

        Cell cellPreco = headerRow.getCell(colPreco, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cellPreco.setCellValue("Valor Original");

        Cell cellDesc = headerRow.getCell(colPrecoDesconto, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cellDesc.setCellValue("Valor Desconto");

        Cell cellCusto = headerRow.getCell(colCusto, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cellCusto.setCellValue("Custo");

        Cell cellVenda = headerRow.getCell(colVenda, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cellVenda.setCellValue("Valor Venda");

        Cell cellLink = headerRow.getCell(colLink, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cellLink.setCellValue("Url Produto");
    }

    @Transactional
    public void excluirArquivo(Long idArquivo) {

        tarefasRepository.deleteByArquivo_ArquivoId(idArquivo);

        arquivoRepository.deleteById(idArquivo);
    }

}
