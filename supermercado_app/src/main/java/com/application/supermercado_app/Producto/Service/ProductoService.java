package com.application.supermercado_app.Producto.Service;

import com.application.supermercado_app.Exception.NotFoundException;
import com.application.supermercado_app.Exception.ProductoException;
import com.application.supermercado_app.Exception.PromocionException;
import com.application.supermercado_app.Producto.DTO.AjustarStockDTO;
import com.application.supermercado_app.Producto.DTO.CrearProductoDTO;
import com.application.supermercado_app.Promocion.DTO.AsignarPromocionDTO;
import com.application.supermercado_app.Producto.DTO.ProductoDTO;
import com.application.supermercado_app.Producto.Mapper.ProductoMapper;
import com.application.supermercado_app.Producto.Model.Producto;
import com.application.supermercado_app.Producto.Repository.ProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import com.application.supermercado_app.Promocion.Model.Promocion;
import com.application.supermercado_app.Promocion.Model.TipoPromocion;
import com.application.supermercado_app.Promocion.Repository.PromocionRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductoService implements IProductoService {

    private final ProductoRepository productoRepository;
    private final PromocionRepository promocionRepository;

    public ProductoService(ProductoRepository productoRepository,
            PromocionRepository promocionRepository) {
        this.productoRepository = productoRepository;
        this.promocionRepository = promocionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> getProductos() {
        return productoRepository.findAllConPromo()
                .stream()
                .map(ProductoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public ProductoDTO saveProducto(CrearProductoDTO crearProductoDTO) {

        validarCodigoBarra(crearProductoDTO.getCodigoBarra());

        String codigoCorto = extraerCodigoCorto(crearProductoDTO.getCodigoBarra());
        validarCodigoCorto(codigoCorto);

        Promocion promocion = crearProductoDTO.getIdPromo() != null
                ? obtenerPromocion(crearProductoDTO.getIdPromo())
                : obtenerSinPromocion();

        Producto producto = Producto.builder()
                .nombre(crearProductoDTO.getNombre())
                .precio(crearProductoDTO.getPrecio())
                .categoria(crearProductoDTO.getCategoria())
                .cantidad(crearProductoDTO.getCantidad())
                .codigoBarra(crearProductoDTO.getCodigoBarra())
                .codigoCorto(codigoCorto)
                .promo(promocion)
                .build();

        producto = productoRepository.save(producto);

        return ProductoMapper.toResponseDTO(producto);
    }

    @Override
    public ProductoDTO editProducto(Long id, CrearProductoDTO crearProductoDTO) {

        Producto producto = obtenerProducto(id);

        producto.setNombre(crearProductoDTO.getNombre());
        producto.setCategoria(crearProductoDTO.getCategoria());
        producto.setCantidad(crearProductoDTO.getCantidad());
        producto.setPrecio(crearProductoDTO.getPrecio());

        if (crearProductoDTO.getIdPromo() != null) {
            producto.setPromo(obtenerPromocion(crearProductoDTO.getIdPromo()));
        } else {
            producto.setPromo(obtenerSinPromocion());
        }

        return ProductoMapper.toResponseDTO(producto);
    }

    @Override
    public void deleteProducto(Long id) {

        Producto producto = obtenerProducto(id);

        if (producto.getDetalles() != null && !producto.getDetalles().isEmpty()) {
            throw new ProductoException("No se puede eliminar este producto porque esta registrado en una venta");
        }

        productoRepository.delete(producto);
    }

    @Override
    public ProductoDTO asignarPromocion(AsignarPromocionDTO promocionDTO) {

        Producto producto = obtenerProducto(promocionDTO.getProductoId());

        producto.setPromo(obtenerPromocion(promocionDTO.getPromocionId()));

        return ProductoMapper.toResponseDTO(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDTO findProducto(Long id) {

        return ProductoMapper.toResponseDTO(obtenerProducto(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> productosConBajoStock() {

        return productoRepository.findByCantidadLessThanEqual(10)
                .stream()
                .map(ProductoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public ProductoDTO ajustaStock(AjustarStockDTO stockDTO) {

        if (stockDTO.getCantidadIngresada() == null) {
            throw new IllegalArgumentException("Debe indicar la cantidad que desea ingresar.");
        }

        Producto producto = obtenerProducto(stockDTO.getIdProducto());

        producto.setCantidad(
                producto.getCantidad() + stockDTO.getCantidadIngresada()
        );

        return ProductoMapper.toResponseDTO(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDTO buscarCodigoBarra(String codigoBarra) {

        Producto producto = productoRepository.findByCodigoBarra(codigoBarra)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado."));

        return ProductoMapper.toResponseDTO(producto);
    }

    private Producto obtenerProducto(Long id) {

        return productoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado."));
    }

    private Promocion obtenerPromocion(Long id) {

        return promocionRepository.findById(id)
                .orElseThrow(() -> new PromocionException("Promoción no encontrada."));
    }

    private Promocion obtenerSinPromocion() {
        return promocionRepository.findByTipo(TipoPromocion.SIN_PROMOCION)
                .orElseThrow(() -> new PromocionException("No se encontro la promocion. Debe crearla primero."));
    }

    private void validarCodigoBarra(String codigoBarra) {

        if (productoRepository.existsByCodigoBarra(codigoBarra)) {
            throw new ProductoException(
                    "Ya existe un producto con el código de barras: " + codigoBarra
            );
        }
    }

    private void validarCodigoCorto(String codigoCorto) {
        if (productoRepository.existsByCodigoCorto(codigoCorto)) {
            throw new ProductoException("Ya existe un producto cuyo ultimos 6 digitos coinciden: " + codigoCorto);
        }
    }

    private static final int LARGO_CODIGO_CORTO = 6;

    private String extraerCodigoCorto(String codigoBarra) {

        if (codigoBarra == null || codigoBarra.length() < LARGO_CODIGO_CORTO) {
            throw new ProductoException("El codigo de barra debe tener al menos " + LARGO_CODIGO_CORTO);
        }

        return codigoBarra.substring(codigoBarra.length() - LARGO_CODIGO_CORTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarPorCodigoCorto(String prefijo) {
        if (prefijo == null || prefijo.isBlank() || prefijo.length() > LARGO_CODIGO_CORTO) {
            return List.of();
        }

        return productoRepository.findByCodigoCortoStartingWith(prefijo)
                .stream()
                .map(ProductoMapper::toResponseDTO)
                .toList();
    }
}
