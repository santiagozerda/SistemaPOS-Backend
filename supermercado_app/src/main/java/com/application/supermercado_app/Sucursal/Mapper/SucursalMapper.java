
package com.application.supermercado_app.Sucursal.Mapper;

import com.application.supermercado_app.Sucursal.DTO.CrearSucursalDTO;
import com.application.supermercado_app.Sucursal.DTO.SucursalDTO;
import com.application.supermercado_app.Sucursal.Model.Sucursal;


public class SucursalMapper {
    
    public static SucursalDTO toDTO(Sucursal s){
    
        if(s == null) return null;
        
        return SucursalDTO.builder()
                .id(s.getIdSucursal())
                .nombre(s.getNombre())
                .provincia(s.getProvincia())
                .localidad(s.getLocalidad())
                .direccion(s.getDireccion())
                .telefono(s.getTelefono())
                .build();
    }
    
    public static Sucursal toEntity(CrearSucursalDTO s){
        if(s==null) return null;
        
        return Sucursal.builder()
                .nombre(s.getNombre())
                .provincia(s.getProvincia())
                .localidad(s.getLocalidad())
                .direccion(s.getDireccion())
                .telefono(s.getTelefono())
                .build();
    }
}
