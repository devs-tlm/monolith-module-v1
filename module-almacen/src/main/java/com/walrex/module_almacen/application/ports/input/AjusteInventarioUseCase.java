package com.walrex.module_almacen.application.ports.input;

import com.walrex.module_almacen.domain.model.OrdenIngreso;
import reactor.core.publisher.Mono;

public interface AjusteInventarioUseCase {
    /**
     * Crea un ajuste de inventario
     * @param ordenIngreso datos del ajuste de inventario
     * @return orden de ingreso creada
     */
    Mono<OrdenIngreso> crearAjusteInventario(OrdenIngreso ordenIngreso);
}
