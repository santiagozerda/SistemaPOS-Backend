package com.application.supermercado_app.Producto.DTO.Excel;

import com.application.supermercado_app.Producto.Importacion.Model.EstadoFilaImportacion;
import java.math.BigDecimal;
import java.util.List;

public record ProductoImportacionDTO(
        Integer numeroFila,
        String codigoBarra,
        String nombre,
        BigDecimal precioPlanilla,
        Integer StockIngresado,
        String categoria,
        EstadoFilaImportacion estado,
        ProductoCoincidenciaDTO productoEncontrado,
        List<ProductoCoincidenciaDTO> posiblesCoincidencias,
        List<ErrorFilaExcelDTO> errores) {

}
