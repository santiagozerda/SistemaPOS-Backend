package com.application.supermercado_app.Producto.DTO.Excel;

import com.application.supermercado_app.Producto.Importacion.Model.AccionImportacion;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DecisionFilaImportacionDTO(
        
        @NotNull(message ="Debe indicar el numero de la fila")
        @Positive(message= "El numero de la fila debe ser positiva")
        Integer numeroFila,
        
        @NotNull(message= "Debe seleccionar una accion")
        AccionImportacion accion,
        
        @Positive(message= "Debe seleccionar una accion")
        Long idProductoDestino,
        
        boolean actualizarPrecio
        ) {

}
