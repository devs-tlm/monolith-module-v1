package com.walrex.module_almacen.domain.model.enums;

public enum TypeMovimiento {
    INGRESO_LOGISTICA(1),
    APROBACION_SALIDA_REQUERIMIENTO(2),
    AJUSTE_INVENTARIO(3),
    INTERNO_TRANSFORMACION(4);

    private final int id;

    TypeMovimiento(int id){ this.id = id;}

    public int getId(){return id;}

    public static TypeMovimiento fromId(int id){
        for(TypeMovimiento movimiento: values()){
            if(movimiento.id==id){
                return movimiento;
            }
        }
        throw  new IllegalArgumentException("Tipo Movimiento no válido " + id);
    }
}
