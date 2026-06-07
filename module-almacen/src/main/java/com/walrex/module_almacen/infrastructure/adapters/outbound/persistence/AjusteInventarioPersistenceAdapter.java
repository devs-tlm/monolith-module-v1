package com.walrex.module_almacen.infrastructure.adapters.outbound.persistence;

import com.walrex.module_almacen.domain.model.DetalleOrdenIngreso;
import com.walrex.module_almacen.domain.model.OrdenIngreso;
import com.walrex.module_almacen.domain.model.enums.TypeMovimiento;
import com.walrex.module_almacen.infrastructure.adapters.outbound.persistence.entity.DetailsIngresoEntity;
import com.walrex.module_almacen.infrastructure.adapters.outbound.persistence.entity.DetalleInventaryEntity;
import com.walrex.module_almacen.infrastructure.adapters.outbound.persistence.entity.KardexEntity;
import com.walrex.module_almacen.infrastructure.adapters.outbound.persistence.repository.DetalleInventoryRespository;
import com.walrex.module_almacen.infrastructure.adapters.outbound.persistence.repository.KardexRepository;
import io.r2dbc.spi.R2dbcException;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@SuperBuilder
@Slf4j
public class AjusteInventarioPersistenceAdapter extends BaseOrdenIngresoPersistenceAdapter {
    protected final KardexRepository kardexRepository;
    protected final DetalleInventoryRespository detalleInventoryRespository;

    @Override
    protected Mono<DetalleOrdenIngreso> procesarDetalleGuardado(
            DetalleOrdenIngreso detalle,
            DetailsIngresoEntity savedDetalleEntity,
            OrdenIngreso ordenIngreso) {

        return consultarLoteInventario(savedDetalleEntity.getId())
                .flatMap(inventario -> {
                    detalle.setIdLoteInventario(inventario.getIdLote().intValue());

                    KardexEntity kardexEntity = crearKardexEntity(savedDetalleEntity, detalle, ordenIngreso);

                    return kardexRepository.save(kardexEntity)
                            .doOnSuccess(info -> log.info("✅ Kardex de ajuste guardado con lote: {}", inventario.getIdLote()))
                            .onErrorResume(ex -> manejarErroresGuardadoKardex(ex))
                            .then(actualizarIdDetalle(detalle, savedDetalleEntity));
                });
    }

    /**
     * Consulta el lote de inventario asociado al detalle de ingreso
     */
    private Mono<DetalleInventaryEntity> consultarLoteInventario(Long idDetalleIngreso) {
        log.debug("🔍 Consultando lote de inventario para ajuste: {}", idDetalleIngreso);

        return detalleInventoryRespository.getInventarioByDetailIngreso(idDetalleIngreso.intValue())
                .doOnNext(inventario ->
                        log.info("✅ Lote encontrado: {} para ajuste: {}",
                                inventario.getIdLote(), idDetalleIngreso))
                .switchIfEmpty(Mono.error(new RuntimeException(
                        String.format("No se encontró inventario asociado al ajuste: %d", idDetalleIngreso)
                )));
    }

    /**
     * Crear entidad kardex para movimiento de ajuste
     */
    protected KardexEntity crearKardexEntity(DetailsIngresoEntity detalleEntity,
                                            DetalleOrdenIngreso detalle,
                                            OrdenIngreso ordenIngreso) {
        String str_detalle = String.format("%s - (%s)",
            ordenIngreso.getMotivo().getDescMotivo(),
            ordenIngreso.getCod_ingreso());

        BigDecimal mto_total = BigDecimal.valueOf(detalleEntity.getCosto_compra() * detalleEntity.getCantidad());
        BigDecimal cantidadConvertida;
        BigDecimal total_stock;

        // Cálculos de conversión
        if (!detalle.getIdUnidad().equals(detalle.getIdUnidadSalida())) {
            BigDecimal factorConversion = BigDecimal.valueOf(Math.pow(10, detalle.getArticulo().getValor_conv()));
            cantidadConvertida = detalle.getCantidad().multiply(factorConversion).setScale(6, RoundingMode.HALF_UP);
        } else {
            cantidadConvertida = detalle.getCantidad();
        }

        BigDecimal stockActual = Optional.ofNullable(detalle.getArticulo().getStock())
                .orElse(BigDecimal.ZERO);

        total_stock = cantidadConvertida.add(stockActual).setScale(6, RoundingMode.HALF_UP);
        log.info("Stock Disponible: {} Total Stock {}", stockActual, total_stock);

        int tipoMovimiento = TypeMovimiento.AJUSTE_INVENTARIO.getId();
        log.info("✅ [AJUSTE-KARDEX] Creando kardex con tipo_movimiento={} (AJUSTE_INVENTARIO)", tipoMovimiento);

        return KardexEntity.builder()
                .tipo_movimiento(tipoMovimiento)
                .detalle(str_detalle)
                .cantidad(BigDecimal.valueOf(detalleEntity.getCantidad()))
                .costo(BigDecimal.valueOf(detalleEntity.getCosto_compra()))
                .valorTotal(mto_total)
                .fecha_movimiento(ordenIngreso.getFechaIngreso())
                .id_articulo(detalleEntity.getId_articulo())
                .id_unidad(detalleEntity.getId_unidad())
                .id_unidad_salida(detalle.getIdUnidadSalida())
                .id_almacen(ordenIngreso.getAlmacen().getIdAlmacen())
                .id_documento(ordenIngreso.getId())
                .id_detalle_documento(detalleEntity.getId().intValue())
                .id_lote(detalle.getIdLoteInventario())
                .saldo_actual(stockActual)
                .saldoLote(cantidadConvertida.setScale(6, RoundingMode.HALF_UP))
                .build();
    }

    protected Mono<KardexEntity> manejarErroresGuardadoKardex(Throwable ex) {
        if (ex instanceof R2dbcException) {
            String errorMsg = "Error de base de datos al guardar kardex de ajuste: " + ex.getMessage();
            log.error(errorMsg, ex);
            return Mono.error(new RuntimeException(errorMsg, ex));
        } else {
            String errorMsg = "Error no esperado al guardar kardex de ajuste: " + ex.getMessage();
            log.error(errorMsg, ex);
            return Mono.error(new RuntimeException(errorMsg, ex));
        }
    }
}
