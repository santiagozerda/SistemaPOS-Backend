package com.application.supermercado_app.Sucursal.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CrearSucursalDTO {

    private String nombre;
    private String provincia;
    private String localidad;
    private String direccion;
    private String telefono;
}
