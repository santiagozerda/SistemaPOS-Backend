package com.application.supermercado_app.Venta.Controller;

import com.application.supermercado_app.Venta.DTO.CrearVentaRequestDTO;
import com.application.supermercado_app.Venta.DTO.VentaCompletaDTO;
import com.application.supermercado_app.Venta.DTO.VentaPreviewDTO;
import com.application.supermercado_app.Venta.DTO.VentaPreviewRequestDTO;
import com.application.supermercado_app.Venta.DTO.VentaResumenDTO;
import com.application.supermercado_app.Venta.Service.IVentaService;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/venta")
public class VentaController {

    @Autowired
    private IVentaService service;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CAJERO')")
    @GetMapping
    public ResponseEntity<List<VentaResumenDTO>> getVentas() {
        return ResponseEntity.ok(service.getVentas());
    }

    @PreAuthorize("hasRole('CAJERO')")
    @PostMapping
    public ResponseEntity<VentaCompletaDTO> createdVenta(@RequestBody CrearVentaRequestDTO venDTO) {
        VentaCompletaDTO crear = service.saveVenta(venDTO);
        return ResponseEntity.created(URI.create("/app/venta/" + crear.getId())).body(crear);
    }

    @PreAuthorize("hasRole('CAJERO')")
    @PatchMapping("/cancelar/{id}")
    public ResponseEntity<VentaCompletaDTO> cancelarVenta(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelarVenta(id));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PatchMapping("/anular/{id}")
    public ResponseEntity<VentaCompletaDTO> anularVenta(@PathVariable Long id) {
        return ResponseEntity.ok(service.anularVenta(id));
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CAJERO')")
    @GetMapping("/{idVenta}")
    public ResponseEntity<VentaCompletaDTO> findVenta(@PathVariable Long idVenta) {
        return ResponseEntity.ok(service.findVenta(idVenta));
    }

    @PreAuthorize("hasRole('CAJERO')")
    @PostMapping("/preview")
    public ResponseEntity<VentaPreviewDTO> previewVenta(@RequestBody VentaPreviewRequestDTO previewDTO) {
        return ResponseEntity.ok(service.previewVenta(previewDTO));
    }
}
