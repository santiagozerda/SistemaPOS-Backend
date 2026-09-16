package com.application.supermercado_app.Auth.DTO;

import com.application.supermercado_app.Usuario.Model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResult {

    private String token;
    private Usuario usuario;
}
