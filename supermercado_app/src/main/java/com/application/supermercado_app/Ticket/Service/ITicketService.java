
package com.application.supermercado_app.Ticket.Service;

import com.application.supermercado_app.Ticket.DTO.TicketDTO;
import com.application.supermercado_app.Venta.Model.Venta;



public interface ITicketService {
    
    public TicketDTO createTicket(Venta venta);
    
    public TicketDTO findTicket(Long ticketId);
    
    public TicketDTO findByVenta(Long idVenta);
    
    public TicketDTO findByNumeroTicket(String numeroTicket);
    
}
