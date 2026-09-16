package com.application.supermercado_app.Sucursal.Service;

import com.application.supermercado_app.Exception.NotFoundException;
import com.application.supermercado_app.Sucursal.DTO.CrearSucursalDTO;
import com.application.supermercado_app.Sucursal.DTO.SucursalDTO;
import com.application.supermercado_app.Sucursal.Mapper.SucursalMapper;
import com.application.supermercado_app.Sucursal.Model.Sucursal;
import com.application.supermercado_app.Sucursal.Repository.SucursalRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SucursalService implements ISucursalService {

    private final SucursalRepository sucursalRepository;

    public SucursalService(SucursalRepository sucursalRepository) {
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public SucursalDTO findSucursal(Long id) {
        return SucursalMapper.toDTO(obtenerSucursal(id));
    }

    @Override
    public SucursalDTO saveSucursal(CrearSucursalDTO crearSucursalDTO) {

        Sucursal sucursal = Sucursal.builder()
                .nombre(crearSucursalDTO.getNombre())
                .provincia(crearSucursalDTO.getProvincia())
                .localidad(crearSucursalDTO.getLocalidad())
                .direccion(crearSucursalDTO.getDireccion())
                .telefono(crearSucursalDTO.getTelefono())
                .build();

        sucursal = sucursalRepository.save(sucursal);

        return SucursalMapper.toDTO(sucursal);
    }

    @Override
    public SucursalDTO editSucursal(Long id, CrearSucursalDTO crearSucursalDTO) {

        Sucursal sucursal = obtenerSucursal(id);

        sucursal.setNombre(crearSucursalDTO.getNombre());
        sucursal.setProvincia(crearSucursalDTO.getProvincia());
        sucursal.setLocalidad(crearSucursalDTO.getLocalidad());
        sucursal.setDireccion(crearSucursalDTO.getDireccion());
        sucursal.setTelefono(crearSucursalDTO.getTelefono());

        sucursal = sucursalRepository.save(sucursal);

        return SucursalMapper.toDTO(sucursal);
    }

    @Override
    public void deleteSucursal(Long id) {

        Sucursal sucursal = obtenerSucursal(id);

        sucursalRepository.delete(sucursal);
    }

    private Sucursal obtenerSucursal(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sucursal no encontrada"));
    }

    @Override
    public List<SucursalDTO> getSucursales() {
        return sucursalRepository.findAll()
                .stream()
                .map(SucursalMapper::toDTO)
                .toList();
    }

}