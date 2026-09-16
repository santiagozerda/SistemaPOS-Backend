
package com.application.supermercado_app.Sucursal.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class SucursalDTO {
    
    private Long id;
    private String nombre;
    private String provincia;
    private String localidad;
    private String direccion;
    private String telefono;

}
