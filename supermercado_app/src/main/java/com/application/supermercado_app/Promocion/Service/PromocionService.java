package com.application.supermercado_app.Promocion.Service;

import com.application.supermercado_app.Exception.NotFoundException;
import com.application.supermercado_app.Exception.PromocionException;
import com.application.supermercado_app.Promocion.DTO.PromocionDTO;
import com.application.supermercado_app.Producto.Model.Producto;
import com.application.supermercado_app.Producto.Repository.ProductoRepository;
import com.application.supermercado_app.Promocion.DTO.CrearPromocionDTO;
import com.application.supermercado_app.Promocion.Mapper.PromoMapper;
import com.application.supermercado_app.Promocion.Model.Promocion;
import com.application.supermercado_app.Promocion.Model.TipoPromocion;
import com.application.supermercado_app.Promocion.Repository.PromocionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PromocionService implements IPromocionService {

    private final PromocionRepository promocionRepository;
    private final ProductoRepository productoRepository;

    public PromocionService(PromocionRepository promocionRepository, ProductoRepository productoRepository) {
        this.promocionRepository = promocionRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public Double calcularTotal(Producto producto, int cantidad) {

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        if (producto.getPromo() == null || !producto.getPromo().isActiva()) {
            return producto.getPrecio() * cantidad;
        }

        switch (producto.getPromo().getTipo()) {

            case TRES_POR_DOS:
                return calcular3x2(producto, cantidad);

            case SEGUNDA_UNIDAD_50:
                return calcularSegundaUnidad(producto, cantidad);

            case DOS_POR_UNO:
                return calcular2x1(producto, cantidad);

            default:
                return producto.getPrecio() * cantidad;
        }
    }

    private double calcularSegundaUnidad(Producto producto, int cantidad) {

        double precio = producto.getPrecio();

        int pares = cantidad / 2;
        int resto = cantidad % 2;

        return pares * (precio + precio * 0.5) + resto * precio;
    }

    private double calcular3x2(Producto producto, int cantidad) {

        double precio = producto.getPrecio();

        int grupos = cantidad / 3;
        int resto = cantidad % 3;

        return grupos * 2 * precio + resto * precio;
    }

    private double calcular2x1(Producto producto, int cantidad) {

        double precio = producto.getPrecio();

        int pares = cantidad / 2;
        int resto = cantidad % 2;

        return (pares + resto) * precio;
    }

    @Override
    public PromocionDTO createPromocion(CrearPromocionDTO crearPromocionDTO) {

        if (crearPromocionDTO.getTipo() == null) {
            throw new PromocionException("Tiene que ser una promoción existente");
        }

        Promocion promocion = Promocion.builder()
                .tipo(crearPromocionDTO.getTipo())
                .descripcion(crearPromocionDTO.getDescripcion())
                .activa(crearPromocionDTO.isActiva())
                .build();

        promocion = promocionRepository.save(promocion);

        return PromoMapper.toDTO(promocion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromocionDTO> getPromociones() {

        return promocionRepository.findAll()
                .stream()
                .map(PromoMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PromocionDTO findPromocion(Long id) {

        return PromoMapper.toDTO(obtenerPromocion(id));
    }

    @Override
    public PromocionDTO editPromo(Long id, CrearPromocionDTO crearPromocionDTO) {

        Promocion promocion = obtenerPromocion(id);

        promocion.setTipo(crearPromocionDTO.getTipo());
        promocion.setDescripcion(crearPromocionDTO.getDescripcion());
        promocion.setActiva(crearPromocionDTO.isActiva());

        promocion = promocionRepository.save(promocion);

        return PromoMapper.toDTO(promocion);
    }

    @Override
    @Transactional
    public void deletePromo(Long id) {

        Promocion promocion = obtenerPromocion(id);

        if (promocion.getTipo() == TipoPromocion.SIN_PROMOCION) {
            throw new PromocionException("No se puede eliminar una promocion de un producto que no tiene promocion ");
        }

        Promocion sinPromocion = obtenerSinPromocion();

        List<Producto> productosAsociados = promocion.getProductos();

        if (productosAsociados != null && !productosAsociados.isEmpty()) {
            productosAsociados.forEach(p -> p.setPromo(sinPromocion));
            productoRepository.saveAll(productosAsociados);
        }

        promocionRepository.delete(promocion);
    }

    private Promocion obtenerPromocion(Long id) {

        return promocionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Promoción no encontrada"));
    }

    private Promocion obtenerSinPromocion() {
        return promocionRepository.findByTipo(TipoPromocion.SIN_PROMOCION)
                .orElseThrow(() -> new PromocionException("No se encontro 'Sin Promocion'. Debe crearla primero"));
    }
}
