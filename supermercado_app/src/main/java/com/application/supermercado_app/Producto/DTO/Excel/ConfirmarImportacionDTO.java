package com.application.supermercado_app.Producto.DTO.Excel;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record ConfirmarImportacionDTO(
        
        @NotNull(message = "Debe indicar el lote de importación.")
        UUID loteId,
        
        @NotEmpty(message = "Debe indicar las decisiones de importación.")
        List<@Valid DecisionFilaImportacionDTO> decisiones
        
        ) {

}
