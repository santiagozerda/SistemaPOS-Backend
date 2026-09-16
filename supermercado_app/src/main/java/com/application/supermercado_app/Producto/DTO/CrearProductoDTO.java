package com.application.supermercado_app.Producto.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CrearProductoDTO {

    private String nombre;
    private String categoria;
    private Double precio;
    private int cantidad;
    private Long idPromo;
    private String codigoBarra;
}
