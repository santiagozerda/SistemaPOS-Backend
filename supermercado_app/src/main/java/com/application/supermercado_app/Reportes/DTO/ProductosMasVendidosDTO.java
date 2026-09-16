package com.application.supermercado_app.Reportes.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductosMasVendidosDTO {

    private Long idProducto;
    private String nombreProducto;
    private Long cantidadVendida;

}
