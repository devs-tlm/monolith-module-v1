package com.walrex.module_almacen.infrastructure.adapters.inbound.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private String error;
    private String message;
    private String code;
}
