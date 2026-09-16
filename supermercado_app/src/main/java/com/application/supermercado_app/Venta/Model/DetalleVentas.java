package com.application.supermercado_app.Venta.Model;

import com.application.supermercado_app.Producto.Model.Producto;
import com.application.supermercado_app.Promocion.Model.TipoPromocion;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class DetalleVentas {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long idDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idVenta")
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idProducto")
    private Producto productos;
    private Integer cantidadVendida;
    private Double precioUnitario;
    private Double subTotal;
    private Double precioFinal;

    @Enumerated(EnumType.STRING)
    private TipoPromocion promo;
    private Double descuentoAplicado;

}