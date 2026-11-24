package com.application.supermercado_app.Producto.DTO.Excel;

import java.math.BigDecimal;

public record ProductoCoincidenciaDTO(
        Long idProducto,
        String codigoBarra,
        String codigoCorto,
        String nombre,
        String categoria,
        BigDecimal precioActual,
        Integer stockActual,
        Long stockProyectado) {

}
