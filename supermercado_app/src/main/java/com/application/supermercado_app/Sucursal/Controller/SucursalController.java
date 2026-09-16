package com.application.supermercado_app.Sucursal.Controller;

import com.application.supermercado_app.Sucursal.DTO.CrearSucursalDTO;
import com.application.supermercado_app.Sucursal.DTO.SucursalDTO;
import com.application.supermercado_app.Sucursal.Service.ISucursalService;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/sucursal")
public class SucursalController {

    @Autowired
    private ISucursalService service;

    @PreAuthorize("hasAnyRole('CAJERO', 'ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<SucursalDTO> findSucursal(@PathVariable Long id) {
        return ResponseEntity.ok(service.findSucursal(id));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<SucursalDTO> createSucursal(@RequestBody CrearSucursalDTO sucuDTO) {
        SucursalDTO crear = service.saveSucursal(sucuDTO);
        return ResponseEntity.created(URI.create("/app/sucursal/" + crear.getId())).body(crear);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<SucursalDTO> editSucursal(@PathVariable Long id, @RequestBody CrearSucursalDTO sucuDTO) {
        return ResponseEntity.ok(service.editSucursal(id, sucuDTO));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSucursal(@PathVariable Long id) {
        service.deleteSucursal(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('CAJERO', 'ADMINISTRADOR')")
    @GetMapping()
    public ResponseEntity<List<SucursalDTO>> getSucursales() {
        return ResponseEntity.ok(service.getSucursales());
    }
}
