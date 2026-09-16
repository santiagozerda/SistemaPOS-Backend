package com.application.supermercado_app.Promocion.Controller;

import com.application.supermercado_app.Promocion.DTO.PromocionDTO;
import com.application.supermercado_app.Promocion.DTO.CrearPromocionDTO;
import com.application.supermercado_app.Promocion.Service.IPromocionService;
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

@PreAuthorize("hasRole('ADMINISTRADOR')")
@RestController
@RequestMapping("/app/promo")
public class PromocionController {

    @Autowired
    private IPromocionService service;

    @PostMapping
    public ResponseEntity<PromocionDTO> createdPromocion(@RequestBody CrearPromocionDTO promoDTO) {
        PromocionDTO crear = service.createPromocion(promoDTO);
        return ResponseEntity.created(URI.create("/app/promo/" + "" + crear.getIdPromocion())).body(crear);
    }

    @GetMapping
    public ResponseEntity<List<PromocionDTO>> getPromociones() {
        return ResponseEntity.ok(service.getPromociones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromocionDTO> findPromocion(@PathVariable Long id) {
        return ResponseEntity.ok(service.findPromocion(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromocionDTO> editPromocion(@PathVariable Long id, @RequestBody CrearPromocionDTO promoDTO) {
        return ResponseEntity.ok(service.editPromo(id, promoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromocion(@PathVariable Long id) {
        service.deletePromo(id);
        return ResponseEntity.noContent().build();
    }
}
