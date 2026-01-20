package com.sismaster.siscrap_api.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sismaster.siscrap_api.config.StorageConfig;

@Service
public class StorageService {

    private final Path basePath;
    private final Path baseExe;

    public StorageService(StorageConfig storageConfig) {
        this.basePath = Paths.get(storageConfig.getBasePath());
        this.baseExe = Paths.get(storageConfig.getBaseExe());
    }

    public void criarPastaUsuario(Long userId) {
        try {
            Path userPath = basePath
                    .resolve(userId.toString());

            Files.createDirectories(userPath);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao criar pasta do usuário", e);
        }
    }

    public String salvarArquivo(Long usuarioId, MultipartFile arquivo) {
        try {
            if (arquivo.isEmpty()) {
                throw new RuntimeException("Arquivo vazio");
            }

            String nomeArquivo = Paths
                    .get(arquivo.getOriginalFilename())
                    .getFileName()
                    .toString();

            // storage/users/{usuarioId}
            Path userDir = basePath.resolve(usuarioId.toString());
            Files.createDirectories(userDir);

            // Caminho final
            Path destino = userDir.resolve(nomeArquivo);

            // Salva o arquivo
            Files.copy(
                    arquivo.getInputStream(),
                    destino,
                    StandardCopyOption.REPLACE_EXISTING);

            return nomeArquivo; // salvar no banco

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo", e);
        }
    }

    public Path getArquivoPath(Long usuarioId, String nomeArquivo) {
        return basePath
                .resolve(usuarioId.toString())
                .resolve(nomeArquivo);
    }


    public Resource baixarExe(String nomeArquivo) {
        try {
            Path arquivo = baseExe.resolve(nomeArquivo).normalize();

            if (!Files.exists(arquivo) || !Files.isReadable(arquivo)) {
                throw new RuntimeException("Arquivo não encontrado ou inacessível");
            }

            return new UrlResource(arquivo.toUri());

        } catch (MalformedURLException e) {
            throw new RuntimeException("Erro ao carregar arquivo", e);
        }
    }

}
