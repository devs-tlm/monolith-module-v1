package com.walrex.module_almacen.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAjusteInventoryDTO {
    private String transactionId;
    private String message;
    private ResultAjustIngresoDTO result_ingresos;
    private ResultAjustEgresoDTO result_egresos;

    public boolean isSuccess() {
        return message != null && !message.isEmpty();
    }
}
