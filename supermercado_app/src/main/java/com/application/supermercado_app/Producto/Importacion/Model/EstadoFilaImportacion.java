package com.application.supermercado_app.Producto.Importacion.Model;

public enum EstadoFilaImportacion {

    NUEVO,
    COINCIDENCIA_EXACTA,
    COINCIDENCIA_POR_NOMBRE,
    CONFLICTO_CODIGO_BARRAS,
    ERROR
}
