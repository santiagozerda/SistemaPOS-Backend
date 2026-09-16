package com.application.supermercado_app.Ticket.Mapper;

import com.application.supermercado_app.Ticket.DTO.TicketDetalleDTO;

import com.application.supermercado_app.Venta.Model.DetalleVentas;

public class DetalleTicketMapper {

    public static TicketDetalleDTO detTicketToDTO(DetalleVentas detVentas) {

        if (detVentas == null) {
            return null;
        }

        return TicketDetalleDTO.builder()
                .idProducto(detVentas.getProductos().getIdProductos())
                .productoNombre(detVentas.getProductos().getNombre())
                .cantidad(detVentas.getCantidadVendida())
                .precioUnitario(detVentas.getPrecioUnitario())
                .subTotal(detVentas.getSubTotal())
                .precioFinal(detVentas.getPrecioFinal())
                .promoAplicada(detVentas.getPromo())
                .descuentoAplicado(detVentas.getDescuentoAplicado())
                .build();
    }

}
