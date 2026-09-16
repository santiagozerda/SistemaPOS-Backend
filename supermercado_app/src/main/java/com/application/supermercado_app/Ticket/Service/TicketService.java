package com.application.supermercado_app.Ticket.Service;

import com.application.supermercado_app.Exception.NotFoundException;
import com.application.supermercado_app.Exception.VentaException;
import com.application.supermercado_app.Ticket.DTO.TicketDTO;
import com.application.supermercado_app.Ticket.Mapper.TicketMapper;

import com.application.supermercado_app.Venta.Model.EstadoVenta;
import com.application.supermercado_app.Ticket.Model.Ticket;
import com.application.supermercado_app.Venta.Model.Venta;
import com.application.supermercado_app.Ticket.Repository.TicketRepository;
import com.application.supermercado_app.Venta.Repository.VentaRepository;
import java.time.LocalDateTime;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TicketService implements ITicketService {

    private final VentaRepository ventaRepository;
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository, VentaRepository ventaRepository) {
        this.ticketRepository = ticketRepository;
        this.ventaRepository = ventaRepository;
    }

    @Override
    @Transactional
    public TicketDTO createTicket(Venta venta) {

        validarVenta(venta);

        Optional<Ticket> ticketExistente
                = ticketRepository.findByVentaIdVentaConDetalle(venta.getIdVenta());

        if (ticketExistente.isPresent()) {
            return TicketMapper.toDTO(ticketExistente.get());
        }

        Venta ventaCompleta = ventaRepository.findVentaConDetalleYPago(venta.getIdVenta())
                .orElseThrow(() -> new VentaException("Venta no encontrada"));

        validarVentaCompleta(ventaCompleta);

        if (ventaCompleta.getPago() == null) {
            throw new VentaException("No se puede generar un ticket " + " para esta venta");
        }

        Ticket ticket = Ticket.builder()
                .fechaEmision(LocalDateTime.now())
                .total(ventaCompleta.getTotal())
                .metodoPago(ventaCompleta.getPago().getMetodoPago())
                .montoRecibido(ventaCompleta.getPago().getMonto())
                .vuelto(ventaCompleta.getPago().getVuelto())
                .venta(ventaCompleta)
                .build();

        ticket = ticketRepository.save(ticket);

        ticket.setNumeroTicket(generarNumeroTicket(ticket.getIdTicket()));

        ticket = ticketRepository.save(ticket);

        return TicketMapper.toDTO(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketDTO findTicket(Long ticketId) {

        return TicketMapper.toDTO(
                obtenerTicket(ticketId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TicketDTO findByNumeroTicket(String numeroTicket) {

        if (numeroTicket == null || numeroTicket.isBlank()) {
            throw new NotFoundException("Debe indicar un numero de ticket");
        }

        Ticket ticket = ticketRepository.findByNumeroTicketConDetalle(numeroTicket)
                .orElseThrow(() -> new NotFoundException("Numero de ticket inexistente"));

        return TicketMapper.toDTO(ticket);
    }

    private void validarVenta(Venta venta) {

        if (venta == null) {
            throw new VentaException("La venta no puede ser nula");
        }

        if (venta.getEstado() != EstadoVenta.APROBADA) {
            throw new VentaException(
                    "No se puede generar un ticket de una venta no pagada."
            );
        }

    }

    private void validarVentaCompleta(Venta venta) {

        if (venta.getEstado() != EstadoVenta.APROBADA) {
            throw new VentaException("No se puede generar el ticket " + " de una venta que no esta APROBADA");
        }

        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new VentaException("No se puede generar el ticket " + " en una venta que no tiene productos");
        }

    }

    private Ticket obtenerTicket(Long id) {

        if (id == null) {
            throw new NotFoundException("El ID del ticket no puede ser nulo");
        }

        return ticketRepository.findById(id)
                .orElseThrow(()
                        -> new NotFoundException("Ticket no encontrado."));
    }

    private String generarNumeroTicket(Long idTicket) {

        return String.format(
                "TCK-%08d",
                idTicket
        );

    }

    @Override
    @Transactional(readOnly = true)
    public TicketDTO findByVenta(Long idVenta) {

        if (idVenta == null) {
            throw new NotFoundException("El ID de la venta no puede ser nulo");
        }

        Ticket ticket = ticketRepository.findByVentaIdVentaConDetalle(idVenta)
                .orElseThrow(() -> new NotFoundException("La venta no posee un ticket asociado"));

        return TicketMapper.toDTO(ticket);
    }

}
