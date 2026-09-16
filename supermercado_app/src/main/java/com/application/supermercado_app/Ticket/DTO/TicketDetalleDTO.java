package com.application.supermercado_app.Ticket.DTO;

import com.application.supermercado_app.Promocion.Model.TipoPromocion;
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
public class TicketDetalleDTO {

    private Long idProducto;
    private String productoNombre;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subTotal;
    private Double precioFinal;

    private TipoPromocion promoAplicada;
    private Double descuentoAplicado;

}
