package com.walrex.module_almacen.infrastructure.adapters.inbound.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AjusteInventarioItemRequestDto {

    @NotNull(message = "ID de artículo es obligatorio")
    private Integer id_articulo;

    @NotNull(message = "Cantidad es obligatoria")
    @Positive(message = "Cantidad debe ser mayor a cero")
    private BigDecimal cantidad;

    @NotNull(message = "Costo es obligatorio")
    @Positive(message = "Costo debe ser mayor a cero")
    private BigDecimal costo;

    @NotNull(message = "Unidad es obligatoria")
    private Integer id_unidad;

    @NotNull(message = "Moneda es obligatoria")
    private Integer id_moneda;

    private String observacion;
}
