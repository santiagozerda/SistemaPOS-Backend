package com.application.supermercado_app.Producto.DTO.Excel;

public record ErrorFilaExcelDTO(
        Integer numeroFila,
        String columna,
        String codigo,
        String valor,
        String mensaje) {

}
