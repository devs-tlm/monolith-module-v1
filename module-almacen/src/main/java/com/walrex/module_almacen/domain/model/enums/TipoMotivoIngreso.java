package com.walrex.module_almacen.domain.model.enums;

public enum TipoMotivoIngreso {
    AJUSTE_INVENTARIO(15, "AJUSTE DE INVENTARIO", TipoOrdenIngreso.AJUSTE_INVENTARIO);

    private final Integer id;
    private final String descripcion;
    private final TipoOrdenIngreso tipoOrden;

    TipoMotivoIngreso(Integer id, String descripcion, TipoOrdenIngreso tipoOrden) {
        this.id = id;
        this.descripcion = descripcion;
        this.tipoOrden = tipoOrden;
    }

    public Integer getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public TipoOrdenIngreso getTipoOrden() {
        return tipoOrden;
    }

    public static TipoMotivoIngreso fromId(Integer id) {
        if (id == null) return null;
        for (TipoMotivoIngreso tipo : TipoMotivoIngreso.values()) {
            if (tipo.id.equals(id)) {
                return tipo;
            }
        }
        return null;
    }
}
