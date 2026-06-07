package com.walrex.module_almacen.application.ports.input;

import com.walrex.module_almacen.domain.model.dto.RequestAjusteInventoryDTO;
import com.walrex.module_almacen.domain.model.dto.ResponseAjusteInventoryDTO;
import reactor.core.publisher.Mono;

public interface ProcesarAjusteInventarioUseCase {
    /**
     * Procesa un ajuste de inventario desde mensaje Kafka
     */
    Mono<ResponseAjusteInventoryDTO> procesarAjusteInventario(RequestAjusteInventoryDTO inventario, String correlationId);
}
