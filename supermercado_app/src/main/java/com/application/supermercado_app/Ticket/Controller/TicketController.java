package com.application.supermercado_app.Ticket.Controller;

import com.application.supermercado_app.Ticket.DTO.TicketDTO;
import com.application.supermercado_app.Ticket.Service.ITicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAnyRole('ADMINISTRADOR','CAJERO')")
@RestController
@RequestMapping("/app/ticket")
public class TicketController {

    @Autowired
    private ITicketService service;

    @GetMapping("/numero/{numeroTicket}")
    public ResponseEntity<TicketDTO> findNumeroTicket(@PathVariable String numeroTicket) {
        return ResponseEntity.ok(service.findByNumeroTicket(numeroTicket));
    }

    @GetMapping("/{idTicket}")
    public ResponseEntity<TicketDTO> findTicket(@PathVariable Long idTicket) {
        return ResponseEntity.ok(service.findTicket(idTicket));
    }
    
    @GetMapping("/venta/{idVenta}")
    public ResponseEntity<TicketDTO> findVenta(@PathVariable Long idVenta) {
        return ResponseEntity.ok(service.findByVenta(idVenta));
    }
}
