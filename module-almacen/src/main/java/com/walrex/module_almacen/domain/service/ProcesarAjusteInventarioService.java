package com.walrex.module_almacen.domain.service;

import com.walrex.module_almacen.application.ports.input.ProcesarAjusteInventarioUseCase;
import com.walrex.module_almacen.application.ports.output.OrdenIngresoLogisticaPort;
import com.walrex.module_almacen.domain.model.dto.RequestAjusteInventoryDTO;
import com.walrex.module_almacen.domain.model.dto.ResponseAjusteInventoryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcesarAjusteInventarioService implements ProcesarAjusteInventarioUseCase {
    @Qualifier("ajusteInventario")
    private final OrdenIngresoLogisticaPort ajusteInventarioAdapter;

    @Override
    public Mono<ResponseAjusteInventoryDTO> procesarAjusteInventario(RequestAjusteInventoryDTO inventario, String correlationId) {
        log.info("✅ [PROCESAR-AJUSTE-KAFKA] Procesando ajuste para correlationId: {}", correlationId);

        validarAjuste(inventario);

        return Mono.just(inventario)
                .doOnSuccess(inv -> log.info("✅ [PROCESAR-AJUSTE-KAFKA] Ajuste procesado para correlationId: {}", correlationId))
                .map(inv -> ResponseAjusteInventoryDTO.builder()
                    .transactionId(correlationId)
                    .message("Ajuste procesado correctamente")
                    .result_ingresos(null)
                    .result_egresos(null)
                    .build())
                .doOnError(error ->
                    log.error("❌ [PROCESAR-AJUSTE-KAFKA] Error al procesar ajuste (correlationId: {}): {}", correlationId, error.getMessage()));
    }

    private void validarAjuste(RequestAjusteInventoryDTO inventario) {
        if (inventario == null) {
            throw new IllegalArgumentException("Datos de ajuste son requeridos");
        }

        log.debug("✅ Validaciones de ajuste Kafka completadas");
    }
}
