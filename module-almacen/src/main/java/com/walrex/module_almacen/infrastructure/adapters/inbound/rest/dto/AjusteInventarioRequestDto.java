package com.walrex.module_almacen.infrastructure.adapters.inbound.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class AjusteInventarioRequestDto {

    @Valid
    @NotNull(message = "Tipo de almacén es obligatorio")
    private AlmacenTipoIngresoLogisticaRequestDto id_tipo_almacen;

    @Valid
    @NotNull(message = "Motivo es obligatorio")
    private MotivoIngresoLogisticaRequestDto motivo;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDate fec_ingreso;

    private String observacion;

    @Valid
    @NotNull(message = "Detalles de ajuste son obligatorios")
    private List<AjusteInventarioItemRequestDto> detalles;
}
