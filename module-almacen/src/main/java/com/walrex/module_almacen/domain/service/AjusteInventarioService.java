package com.walrex.module_almacen.domain.service;

import com.walrex.module_almacen.application.ports.input.AjusteInventarioUseCase;
import com.walrex.module_almacen.application.ports.output.OrdenIngresoLogisticaPort;
import com.walrex.module_almacen.domain.model.OrdenIngreso;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AjusteInventarioService implements AjusteInventarioUseCase {
    private final OrdenIngresoLogisticaPort ajusteInventarioAdapter;

    public AjusteInventarioService(@Qualifier("ajusteInventario") OrdenIngresoLogisticaPort ajusteInventarioAdapter) {
        this.ajusteInventarioAdapter = ajusteInventarioAdapter;
    }

    @Override
    public Mono<OrdenIngreso> crearAjusteInventario(OrdenIngreso ordenIngreso) {
        log.info("✅ [AJUSTE-INVENTARIO] Iniciando creación de ajuste de inventario");

        validarAjuste(ordenIngreso);

        return ajusteInventarioAdapter.guardarOrdenIngresoLogistica(ordenIngreso)
                .doOnSuccess(resultado ->
                    log.info("✅ [AJUSTE-INVENTARIO] Ajuste creado exitosamente: {}", resultado.getCod_ingreso()))
                .doOnError(error ->
                    log.error("❌ [AJUSTE-INVENTARIO] Error al crear ajuste: {}", error.getMessage()));
    }

    private void validarAjuste(OrdenIngreso ordenIngreso) {
        if (ordenIngreso.getDetalles() == null || ordenIngreso.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("El ajuste debe contener al menos un artículo");
        }

        if (ordenIngreso.getMotivo() == null || ordenIngreso.getMotivo().getIdMotivo() != 15) {
            throw new IllegalArgumentException("Motivo inválido para ajuste de inventario");
        }

        if (ordenIngreso.getAlmacen() == null) {
            throw new IllegalArgumentException("Almacén es obligatorio");
        }

        log.debug("✅ Validaciones de ajuste completadas");
    }
}
