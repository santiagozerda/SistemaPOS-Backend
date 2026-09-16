
package com.application.supermercado_app.Venta.DTO;


import com.application.supermercado_app.Promocion.Model.TipoPromocion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class DetalleVentaDTO {
    
    private Long idProducto;
    private String nombreProducto;
    private Integer cantidadVendida;
    private Double precioUnitario;
    private Double subTotal;
    
    private TipoPromocion promo;
    private Double descuAplicado;
}
