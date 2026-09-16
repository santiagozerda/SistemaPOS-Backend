
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
public class CrearUsuarioDTO {
    
    private String nombre;
    private String email;
    private String password;
    private Rol rol;
}
