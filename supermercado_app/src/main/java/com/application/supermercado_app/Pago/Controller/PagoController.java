package com.application.supermercado_app.Pago.Controller;

import com.application.supermercado_app.Pago.DTO.PagoRequestDTO;
import com.application.supermercado_app.Pago.DTO.PagoResponseDTO;
import com.application.supermercado_app.Pago.DTO.PagoTransferenciaRequestDTO;
import com.application.supermercado_app.Pago.DTO.QRResponseDTO;
import com.application.supermercado_app.Pago.Model.EstadoPago;
import com.application.supermercado_app.Pago.Service.IPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/pagos")
public class PagoController {

    @Autowired
    private IPagoService service;

    @PreAuthorize("hasRole('CAJERO')")
    @PostMapping
    public ResponseEntity<PagoResponseDTO> registrarPago(@RequestBody PagoRequestDTO pagoDTO) {
        PagoResponseDTO pago = service.procesarPago(pagoDTO);

        if (pago.getEstadoPago() == EstadoPago.APROBADO) {
            return ResponseEntity.ok(pago);
        } else {
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(pago);
        }
    }

    //Pagos
    @PreAuthorize("hasAnyRole('CAJERO', 'ADMINISTRADOR')")
    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<PagoResponseDTO> getPagoDeVenta(@PathVariable Long ventaId) {
        return ResponseEntity.ok(service.getPago(ventaId));
    }

    @PreAuthorize("hasRole('CAJERO')")
    @PostMapping("/transferencia")
    public ResponseEntity<QRResponseDTO> generarQR(@RequestBody PagoTransferenciaRequestDTO pagoQR) {
        return ResponseEntity.ok(service.generarQR(pagoQR));
    }

}
