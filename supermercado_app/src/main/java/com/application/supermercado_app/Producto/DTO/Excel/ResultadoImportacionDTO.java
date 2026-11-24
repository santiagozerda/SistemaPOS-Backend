package com.application.supermercado_app.Producto.DTO.Excel;

import com.application.supermercado_app.Producto.Importacion.Model.EstadoLoteImportacion;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ResultadoImportacionDTO(
        UUID loteId,
        EstadoLoteImportacion estado,
        LocalDateTime fechaConfirmacion,
        Integer productosCreados,
        Integer stocksIncrementados,
        Integer preciosActualizados,
        Integer filasOmitidas,
        List<DetalleResultadoImportacionDTO> resultados) {

}
