package com.sismaster.siscrap_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "storage")
@Data
public class StorageConfig {
    private String basePath;
    private String baseExe;
}
