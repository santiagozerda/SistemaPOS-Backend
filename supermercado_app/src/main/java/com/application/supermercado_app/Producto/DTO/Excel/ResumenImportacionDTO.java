package com.application.supermercado_app.Producto.DTO.Excel;

public record ResumenImportacionDTO(
        Integer totalFilas,
        Integer productoNuevos,
        Integer coincidenciaExactas,
        Integer coincidenciaPorNombres,
        Integer conflictosCodigoBarras,
        Integer filaConErrores) {

}
