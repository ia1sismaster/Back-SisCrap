package com.sismaster.siscrap_api.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArquivoStatusResponseDto {
    private Long id;
    private String fileName;
    private LocalDateTime uploadDate;
    private long processed;
    private long total;
    private String status;
}
