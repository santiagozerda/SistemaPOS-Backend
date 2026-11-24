package com.application.supermercado_app.Producto.DTO;

import java.math.BigDecimal;

public record ResultadoActualizacionProductoDTO(
        Long idProducto,
        String nombreProducto,
        String codigoBarra,
        Integer stockAnterior,
        Integer unidadesIngresadas,
        Integer stockFinal,
        BigDecimal precioAnterior,
        BigDecimal precioFinal,
        boolean precioActualizado) {

}
