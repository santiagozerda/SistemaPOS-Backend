package com.application.supermercado_app.Pago.Mapper;

import com.application.supermercado_app.Pago.DTO.PagoRequestDTO;
import com.application.supermercado_app.Pago.DTO.PagoResponseDTO;
import com.application.supermercado_app.Pago.Model.EstadoPago;
import com.application.supermercado_app.Pago.Model.Pago;
import com.application.supermercado_app.Ticket.DTO.TicketReferenciaDTO;
import com.application.supermercado_app.Ticket.Model.Ticket;
import com.application.supermercado_app.Venta.Model.Venta;

public class PagoMapper {

    public static PagoResponseDTO toResponseDTO(Pago p) {

        if (p == null) {
            return null;
        }

        TicketReferenciaDTO ticketReferencia = null;

        if (p.getVenta() != null && p.getVenta().getTicket() != null) {

            Ticket ticket = p.getVenta().getTicket();

            ticketReferencia = TicketReferenciaDTO.builder()
                    .idTicket(ticket.getIdTicket())
                    .numeroTicket(ticket.getNumeroTicket())
                    .build();
        }

        return PagoResponseDTO.builder()
                .idPago(p.getIdPago())
                .idVenta(p.getVenta() != null ? p.getVenta().getIdVenta() : null)
                .metodoPago(p.getMetodoPago())
                .fecha(p.getFechaPago())
                .estadoPago(p.getEstado())
                .monto(p.getMonto())
                .ticket(ticketReferencia)
                .build();

    }

    public static Pago toEntity(PagoRequestDTO dto, Venta venta) {

        if (dto == null) {
            return null;
        }

        return Pago.builder()
                .venta(venta)
                .metodoPago(dto.getMetodoPago())
                .monto(dto.getTotalPagar())
                .estado(EstadoPago.PENDIENTE)
                .fechaPago(venta.getFechaVenta().toLocalDate())
                .build();
    }
}
