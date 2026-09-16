package com.application.supermercado_app.Sucursal.Service;

import com.application.supermercado_app.Sucursal.DTO.CrearSucursalDTO;
import com.application.supermercado_app.Sucursal.DTO.SucursalDTO;
import java.util.List;

public interface ISucursalService {

    public SucursalDTO findSucursal(Long id);

    public SucursalDTO saveSucursal(CrearSucursalDTO crearSucursalDTO);

    public void deleteSucursal(Long id);

    public SucursalDTO editSucursal(Long id, CrearSucursalDTO crearSucursalDTO);
    
    public List<SucursalDTO> getSucursales();
}
