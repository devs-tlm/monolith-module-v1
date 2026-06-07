package com.walrex.module_almacen.infrastructure.adapters.inbound.reactiveweb;

import com.walrex.module_almacen.application.ports.input.AjusteInventarioUseCase;
import com.walrex.module_almacen.domain.model.OrdenIngreso;
import com.walrex.module_almacen.infrastructure.adapters.inbound.rest.dto.AjusteInventarioRequestDto;
import com.walrex.module_almacen.infrastructure.adapters.inbound.rest.dto.ErrorResponseDto;
import com.walrex.module_almacen.infrastructure.adapters.inbound.rest.dto.ResponseCreateOrdenIngresoLogisticaDto;
import com.walrex.module_almacen.infrastructure.adapters.inbound.reactiveweb.mapper.AjusteInventarioRequestMapper;
import com.walrex.module_almacen.infrastructure.adapters.inbound.reactiveweb.validator.AjusteInventarioValidator;
import com.walrex.module_security_commons.domain.model.JwtUserInfo;
import com.walrex.module_security_commons.infrastructure.adapters.JwtUserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class AjusteInventarioHandler {
    private final AjusteInventarioUseCase ajusteInventarioUseCase;
    private final AjusteInventarioValidator validator;
    private final AjusteInventarioRequestMapper mapper;
    private final JwtUserContextService jwtService;

    public Mono<ServerResponse> crearAjuste(ServerRequest request) {
        log.info("✅ [AJUSTE-INVENTARIO-HANDLER] Recibiendo solicitud de ajuste de inventario");

        JwtUserInfo user = jwtService.getCurrentUser(request);
        log.info("✅ [AJUSTE-INVENTARIO-HANDLER] Usuario: {} ({})", user.getUsername(), user.getUserId());

        return request.bodyToMono(AjusteInventarioRequestDto.class)
                .doOnNext(dto -> log.debug("📋 [AJUSTE-INVENTARIO-HANDLER] Request body recibido: {}", dto))
                .flatMap(dto -> {
                    try {
                        validator.validarRequest(dto);
                        return Mono.just(dto);
                    } catch (IllegalArgumentException e) {
                        log.error("❌ [AJUSTE-INVENTARIO-HANDLER] Error de validación: {}", e.getMessage());
                        return Mono.error(new ServerWebInputException(e.getMessage()));
                    }
                })
                .map(mapper::toOrdenIngreso)
                .doOnNext(orden -> orden.setIdUser(Integer.valueOf(user.getUserId())))
                .flatMap(ajusteInventarioUseCase::crearAjusteInventario)
                .map(this::mapToResponse)
                .flatMap(response -> ServerResponse.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(response))
                .onErrorResume(throwable -> {
                    log.error("❌ [AJUSTE-INVENTARIO-HANDLER] Error: {}", throwable.getMessage());
                    return ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(mapErrorResponse(throwable));
                });
    }

    private ResponseCreateOrdenIngresoLogisticaDto mapToResponse(OrdenIngreso ordenIngreso) {
        return ResponseCreateOrdenIngresoLogisticaDto.builder()
                .success(true)
                .affected_rows(1)
                .message("Ajuste de inventario creado exitosamente: " + ordenIngreso.getCod_ingreso())
                .build();
    }

    private ErrorResponseDto mapErrorResponse(Throwable throwable) {
        return new ErrorResponseDto(
            "Error al crear ajuste",
            throwable.getMessage(),
            "AJUSTE_INVENTARIO_ERROR"
        );
    }
}
