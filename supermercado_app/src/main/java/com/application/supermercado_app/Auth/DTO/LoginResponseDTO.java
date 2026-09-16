package com.application.supermercado_app.Auth.DTO;

import com.application.supermercado_app.Usuario.Model.Rol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    private Long idUsuario;
    private String nombre;
    private String email;
    private Rol rol;
}
