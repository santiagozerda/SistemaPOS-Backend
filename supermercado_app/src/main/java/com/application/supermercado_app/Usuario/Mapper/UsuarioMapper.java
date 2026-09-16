package com.application.supermercado_app.Usuario.Mapper;

import com.application.supermercado_app.Usuario.DTO.UsuarioDTO;
import com.application.supermercado_app.Usuario.Model.Usuario;

public class UsuarioMapper {

    public static UsuarioDTO toDTO(Usuario usuario) {

        return UsuarioDTO.builder()
                .id(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .activo(usuario.isActivo())
                .build();
    }

}
