
package com.application.supermercado_app.Usuario.DTO;

import com.application.supermercado_app.Usuario.Model.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class UsuarioDTO {
    
    private Long id;
    private String nombre;
    private String email;
    private Rol rol;
    private boolean activo;
}
