package com.application.supermercado_app.Producto.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoCoincidencia {

    @Column(name = "producto_id", nullable = false)
    private Long idProducto;

    @Column(name = "codigo_barra", length = 100)
    private String codigoBarra;

    @Column(name = "nombre", length = 255)
    private String nombre;

    @Column(name = "categoria", length = 255)
    private String categoria;

    @Column(name = "precio_actual", precision = 15, scale = 2)
    private BigDecimal precioActual;

    @Column(name = "stock_actual")
    private Integer stockActual;
}
