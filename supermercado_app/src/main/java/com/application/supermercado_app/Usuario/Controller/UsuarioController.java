package com.application.supermercado_app.Usuario.Controller;

import com.application.supermercado_app.Usuario.DTO.CrearUsuarioDTO;
import com.application.supermercado_app.Usuario.DTO.UsuarioDTO;
import com.application.supermercado_app.Usuario.Service.IUsuarioService;
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
@RequestMapping("/app/usuarios")
public class UsuarioController {

    @Autowired
    private IUsuarioService service;

    @PostMapping
    public ResponseEntity<UsuarioDTO> crearUsuario(@RequestBody CrearUsuarioDTO usuDTO) {
        UsuarioDTO crear = service.crearUsuario(usuDTO);
        return ResponseEntity.created(URI.create("/app/usuarios/" + crear.getId())).body(crear);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getUsuarios() {
        return ResponseEntity.ok(service.getUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> findUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(service.findUsuario(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> editUsuario(@PathVariable Long id, @RequestBody CrearUsuarioDTO usuDTO) {
        return ResponseEntity.ok(service.editUsuario(id, usuDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        service.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
