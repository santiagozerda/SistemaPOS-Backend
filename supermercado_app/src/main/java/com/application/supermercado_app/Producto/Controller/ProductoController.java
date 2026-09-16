package com.application.supermercado_app.Producto.Controller;

import com.application.supermercado_app.Producto.DTO.AjustarStockDTO;
import com.application.supermercado_app.Producto.DTO.CrearProductoDTO;
import com.application.supermercado_app.Promocion.DTO.AsignarPromocionDTO;
import com.application.supermercado_app.Producto.DTO.ProductoDTO;
import com.application.supermercado_app.Producto.Service.IProductoService;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/productos")
public class ProductoController {

    @Autowired
    private IProductoService service;

    @PreAuthorize("hasAnyRole('CAJERO', 'ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> getProductos() {
        return ResponseEntity.ok(service.getProductos());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<ProductoDTO> createdProducto(@RequestBody CrearProductoDTO prodDTO) {
        ProductoDTO crear = service.saveProducto(prodDTO);
        return ResponseEntity.created(URI.create("app/productos/" + crear.getId())).body(crear);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> editProducto(@PathVariable Long id, @RequestBody CrearProductoDTO prodDTO) {
        return ResponseEntity.ok(service.editProducto(id, prodDTO));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        service.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{productoId}/promo/{promocionId}")
    public ResponseEntity<ProductoDTO> asignarPromocion(@PathVariable Long productoId, @PathVariable Long promocionId) {
        ProductoDTO actualizar = service.asignarPromocion(new AsignarPromocionDTO(productoId, promocionId));
        return ResponseEntity.ok(actualizar);
    }

    @PreAuthorize("hasAnyRole('CAJERO', 'ADMINISTRADOR')")
    @GetMapping("/{idProducto}")
    public ResponseEntity<ProductoDTO> findProducto(@PathVariable Long idProducto) {
        return ResponseEntity.ok(service.findProducto(idProducto));
    }

    @PreAuthorize("hasAnyRole('CAJERO', 'ADMINISTRADOR')")
    @GetMapping("/bajo-stock")
    public ResponseEntity<List<ProductoDTO>> getProductosBajoStock() {
        return ResponseEntity.ok(service.productosConBajoStock());
    }

    @PreAuthorize("hasAnyRole('CAJERO', 'ADMINISTRADOR')")
    @PatchMapping("/ajustar-stock")
    public ResponseEntity<ProductoDTO> ajustarStock(@RequestBody AjustarStockDTO stock) {
        return ResponseEntity.ok(service.ajustaStock(stock));
    }

    @PreAuthorize("hasAnyRole('CAJERO', 'ADMINISTRADOR')")
    @GetMapping("/codigo/{codigoBarra}")
    public ResponseEntity<ProductoDTO> findByCodigo(@PathVariable String codigoBarra) {
        return ResponseEntity.ok(service.buscarCodigoBarra(codigoBarra));
    }
    
    @PreAuthorize("hasAnyRole('CAJERO', 'ADMINISTRADOR')")
    @GetMapping("/codigo-corto/{prefijo}")
    public ResponseEntity<List<ProductoDTO>> buscarPorCodigoCorto(@PathVariable String prefijo){
        return ResponseEntity.ok(service.buscarPorCodigoCorto(prefijo));
    }
}
