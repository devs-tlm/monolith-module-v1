package com.walrex.module_almacen.infrastructure.adapters.inbound.reactiveweb.validator;

import com.walrex.module_almacen.infrastructure.adapters.inbound.rest.dto.AjusteInventarioItemRequestDto;
import com.walrex.module_almacen.infrastructure.adapters.inbound.rest.dto.AjusteInventarioRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AjusteInventarioValidator {

    public void validarRequest(AjusteInventarioRequestDto dto) {
        if (dto.getId_tipo_almacen() == null || dto.getId_tipo_almacen().getId_almacen() == null) {
            throw new IllegalArgumentException("Tipo de almacén es obligatorio");
        }

        if (dto.getMotivo() == null || dto.getMotivo().getId() == null) {
            throw new IllegalArgumentException("Motivo es obligatorio");
        }

        if (dto.getMotivo().getId() != 15) {
            throw new IllegalArgumentException("Motivo inválido. Solo se acepta AJUSTE DE INVENTARIO (15)");
        }

        if (dto.getFec_ingreso() == null) {
            throw new IllegalArgumentException("Fecha de ingreso es obligatoria");
        }

        if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("Detalles del ajuste son obligatorios");
        }

        dto.getDetalles().forEach(this::validarItem);

        log.debug("✅ Validaciones de ajuste completadas correctamente");
    }

    private void validarItem(AjusteInventarioItemRequestDto item) {
        if (item.getId_articulo() == null) {
            throw new IllegalArgumentException("ID de artículo es obligatorio");
        }

        if (item.getCantidad() == null || item.getCantidad().signum() <= 0) {
            throw new IllegalArgumentException("Cantidad debe ser mayor a cero");
        }

        if (item.getCosto() == null || item.getCosto().signum() <= 0) {
            throw new IllegalArgumentException("Costo debe ser mayor a cero");
        }

        if (item.getId_unidad() == null) {
            throw new IllegalArgumentException("Unidad es obligatoria");
        }

        if (item.getId_moneda() == null) {
            throw new IllegalArgumentException("Moneda es obligatoria");
        }
    }
}
