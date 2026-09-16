package com.application.supermercado_app.Producto.DTO;

import com.application.supermercado_app.Promocion.DTO.PromocionDTO;
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
public class ProductoDTO {

    private Long id;
    private String nombre;
    private String categoria;
    private Double precio;
    private int cantidad;
    private PromocionDTO promo;
    private String codigoBarra;

}
