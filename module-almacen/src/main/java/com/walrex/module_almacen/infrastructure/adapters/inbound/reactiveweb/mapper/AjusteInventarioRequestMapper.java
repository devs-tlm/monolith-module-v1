package com.walrex.module_almacen.infrastructure.adapters.inbound.reactiveweb.mapper;

import com.walrex.module_almacen.domain.model.Almacen;
import com.walrex.module_almacen.domain.model.Articulo;
import com.walrex.module_almacen.domain.model.DetalleOrdenIngreso;
import com.walrex.module_almacen.domain.model.Motivo;
import com.walrex.module_almacen.domain.model.OrdenIngreso;
import com.walrex.module_almacen.infrastructure.adapters.inbound.rest.dto.AjusteInventarioItemRequestDto;
import com.walrex.module_almacen.infrastructure.adapters.inbound.rest.dto.AjusteInventarioRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AjusteInventarioRequestMapper {

    public OrdenIngreso toOrdenIngreso(AjusteInventarioRequestDto dto) {
        log.debug("✅ Mapeando AjusteInventarioRequestDto a OrdenIngreso");

        return OrdenIngreso.builder()
                .almacen(Almacen.builder()
                    .idAlmacen(dto.getId_tipo_almacen().getId_almacen())
                    .build())
                .motivo(Motivo.builder()
                    .idMotivo(dto.getMotivo().getId())
                    .descMotivo(dto.getMotivo().getDescripcion())
                    .build())
                .fechaIngreso(dto.getFec_ingreso())
                .observacion(dto.getObservacion() != null ? dto.getObservacion() : "")
                .detalles(dto.getDetalles().stream()
                    .map(this::toDetalleOrdenIngreso)
                    .collect(Collectors.toList()))
                .build();
    }

    private DetalleOrdenIngreso toDetalleOrdenIngreso(AjusteInventarioItemRequestDto item) {
        log.debug("Mapeando item de ajuste: artículo {} - cantidad {}", item.getId_articulo(), item.getCantidad());

        return DetalleOrdenIngreso.builder()
                .articulo(Articulo.builder()
                    .id(item.getId_articulo())
                    .build())
                .cantidad(item.getCantidad())
                .costo(item.getCosto())
                .idUnidad(item.getId_unidad())
                .idUnidadSalida(item.getId_unidad())
                .idMoneda(item.getId_moneda())
                .build();
    }
}
