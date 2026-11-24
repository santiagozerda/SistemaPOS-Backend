package com.application.supermercado_app.Producto.DTO.Excel;

import com.application.supermercado_app.Producto.Importacion.Model.EstadoLoteImportacion;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AnalisisImportacionDTO(
        UUID loteId,
        String nombreArchivo,
        LocalDateTime fechaAnalisis,
        LocalDateTime fechaExpiracion,
        EstadoLoteImportacion estado,
        ResumenImportacionDTO resumen,
        List<ProductoImportacionDTO> filas) {

}
