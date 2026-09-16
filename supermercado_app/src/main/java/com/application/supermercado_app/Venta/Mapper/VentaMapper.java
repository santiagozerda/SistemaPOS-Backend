package com.application.supermercado_app.Venta.Mapper;

import com.application.supermercado_app.Pago.Model.EstadoPago;
import com.application.supermercado_app.Ticket.DTO.TicketReferenciaDTO;
import com.application.supermercado_app.Venta.DTO.DetalleVentaDTO;
import com.application.supermercado_app.Venta.DTO.VentaCompletaDTO;
import com.application.supermercado_app.Venta.DTO.VentaResumenDTO;
import com.application.supermercado_app.Venta.Model.Venta;
import java.util.stream.Collectors;

public class VentaMapper {

    public static VentaResumenDTO toResumenDTO(Venta v) {

        if (v == null) {
            return null;
        }

        return VentaResumenDTO.builder()
                .ventaId(v.getIdVenta())
                .fecha(v.getFechaVenta().toLocalDate())
                .idSucursal(v.getSucursal().getIdSucursal())
                .estadoVenta(v.getEstado())
                .total(v.getTotal())
                .totalDescuento(v.getTotalDescuento())
                .metodoPago(v.getPago() != null ? v.getPago().getMetodoPago() : null)
                .estadoPago(v.getPago() != null ? v.getPago().getEstado() : null)
                .build();
    }

    public static VentaCompletaDTO toResponseDTO(Venta v) {

        if (v == null) {
            return null;
        }

        //Convertimos la lista DetalleVentas a DetallesVentasDTO
        var detalles = v.getDetalles()
                .stream()
                .map(det -> DetalleVentaDTO.builder()
                .idProducto(det.getProductos().getIdProductos())
                .nombreProducto(det.getProductos().getNombre())
                .cantidadVendida(det.getCantidadVendida())
                .precioUnitario(det.getPrecioUnitario())
                .subTotal(det.getSubTotal())
                .descuAplicado(det.getDescuentoAplicado())
                .promo(det.getPromo())
                .build()
                ).collect(Collectors.toList());

        TicketReferenciaDTO ticketReferencia = null;

        if (v.getTicket() != null) {

            ticketReferencia = TicketReferenciaDTO.builder()
                    .idTicket(v.getTicket().getIdTicket())
                    .numeroTicket(v.getTicket().getNumeroTicket())
                    .build();
        }

        return VentaCompletaDTO.builder()
                .id(v.getIdVenta())
                .fecha(v.getFechaVenta().toLocalDate())
                .idSucursal(v.getSucursal().getIdSucursal())
                .estadoVenta(v.getEstado())
                .listaDetalle(detalles)
                .total(v.getTotal())
                .totalDescuento(v.getTotalDescuento())
                .ticket(ticketReferencia)
                .build();

    }

}
