package com.application.supermercado_app.Producto.DTO.Excel;

import com.application.supermercado_app.Producto.Importacion.Model.AccionImportacion;
import java.math.BigDecimal;

public record DetalleResultadoImportacionDTO(
        Integer numeroFila,
        AccionImportacion accionAplicada,
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
