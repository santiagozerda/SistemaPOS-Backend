package com.application.supermercado_app.Ticket.Mapper;

import com.application.supermercado_app.Sucursal.DTO.SucursalDTO;
import com.application.supermercado_app.Sucursal.Mapper.SucursalMapper;
import com.application.supermercado_app.Ticket.DTO.TicketDTO;
import com.application.supermercado_app.Ticket.DTO.TicketDetalleDTO;
import com.application.supermercado_app.Ticket.Model.Ticket;
import com.application.supermercado_app.Venta.Model.EstadoVenta;
import java.util.List;

import java.util.stream.Collectors;

public class TicketMapper {

    public static TicketDTO toDTO(Ticket t) {
        if (t == null) {
            return null;
        }

        EstadoVenta estadoVenta = null;
        SucursalDTO sucursalDTO = null;

        List<TicketDetalleDTO> detTicketDTO = List.of();
        
        if (t.getVenta() != null) {
            estadoVenta = t.getVenta().getEstado();
            sucursalDTO = SucursalMapper.toDTO(t.getVenta().getSucursal());
            detTicketDTO = t.getVenta().getDetalles()
                    .stream()
                    .map(DetalleTicketMapper::detTicketToDTO)
                    .collect(Collectors.toList());
        }

        return TicketDTO.builder()
                .idTicket(t.getIdTicket())
                .numeroTicket(t.getNumeroTicket())
                .fecha(t.getFechaEmision())
                .sucursal(sucursalDTO)
                .total(t.getTotal())
                .metodoPago(t.getMetodoPago())
                .montoRecibido(t.getMontoRecibido())
                .vuelto(t.getVuelto())
                .nombreCliente("CONSUMIDOR FINAL")
                .estadoVenta(estadoVenta)
                .idVenta(t.getVenta() != null ? t.getVenta().getIdVenta() : null)
                .listDetalle(detTicketDTO)
                .build();
    }

}
