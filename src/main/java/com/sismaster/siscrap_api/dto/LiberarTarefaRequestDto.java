package com.sismaster.siscrap_api.dto;

import java.util.List;

import lombok.Data;

@Data
public class LiberarTarefaRequestDto {
    private String email;
    private String hash;
    private List<Long> ids;
}
