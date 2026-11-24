package com.application.supermercado_app.Producto.Service;

import com.application.supermercado_app.Exception.ImportacionException;
import com.application.supermercado_app.Exception.NotFoundException;
import com.application.supermercado_app.Exception.ProductoException;
import com.application.supermercado_app.Exception.PromocionException;
import com.application.supermercado_app.Producto.DTO.AjustarStockDTO;
import com.application.supermercado_app.Producto.DTO.CrearProductoDTO;
import com.application.supermercado_app.Promocion.DTO.AsignarPromocionDTO;
import com.application.supermercado_app.Producto.DTO.ProductoDTO;
import com.application.supermercado_app.Producto.DTO.ResultadoActualizacionProductoDTO;
import com.application.supermercado_app.Producto.Mapper.ProductoMapper;
import com.application.supermercado_app.Producto.Model.Producto;
import com.application.supermercado_app.Producto.Repository.ProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import com.application.supermercado_app.Promocion.Model.Promocion;
import com.application.supermercado_app.Promocion.Model.TipoPromocion;
import com.application.supermercado_app.Promocion.Repository.PromocionRepository;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;
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

    @Override
    public ProductoDTO crearProductoDesdeImportacion(String nombre, String categoria, BigDecimal precio, Integer cantidad, String codigoBarra) {

        if (precio == null || precio.signum() < 0) {
            throw new ProductoException("El precio del producto no es válido.");
        }

        if (cantidad == null || cantidad <= 0) {
            throw new ProductoException("El stock inicial debe ser mayor a cero.");
        }

        return crearProducto(
                nombre,
                categoria,
                precio.doubleValue(),
                cantidad,
                codigoBarra,
                null
        );
    }

    @Override
    public ResultadoActualizacionProductoDTO actualizarDesdeImportacion(Long idProducto, String nombreExcel, String codigoBarraExcel, String codigoBarraDectectado, boolean coincidenciaPorNombre, Integer unidadesIngresadas, BigDecimal precioExcel, BigDecimal precioActualDetectado, boolean actualizarPrecio) {

        if (unidadesIngresadas == null || unidadesIngresadas <= 0) {
            throw new ProductoException(
                    "La cantidad de unidades debe ser mayor a cero."
            );
        }

        // Bloquear el registro para evitar modificaciones simultáneas.
        Producto producto = productoRepository.findByIdForUpdate(idProducto)
                .orElseThrow(()
                        -> new NotFoundException("Producto no encontrado.")
                );

        String nombreActual = normalizarNombre(producto.getNombre());
        String nombreImportado = normalizarNombre(nombreExcel);

        if (!Objects.equals(
                producto.getCodigoBarra(),
                codigoBarraDectectado)) {

            throw new ImportacionException(
                    "El código de barras del producto cambió desde el análisis. "
                    + "Debe revisar nuevamente la importación."
            );
        }

        if (coincidenciaPorNombre
                && Objects.equals(codigoBarraDectectado, codigoBarraExcel)) {

            throw new ImportacionException(
                    "La coincidencia por nombre ya no es válida."
            );
        }

        if (!coincidenciaPorNombre
                && !Objects.equals(codigoBarraDectectado, codigoBarraExcel)) {

            throw new ImportacionException(
                    "La coincidencia exacta ya no es válida."
            );
        }

        // Verificar que el producto siga siendo el mismo que se analizó.
        if (!nombreActual.equals(nombreImportado)) {
            throw new ImportacionException(
                    "El nombre del producto cambió desde el análisis. "
                    + "Debe analizar nuevamente la planilla."
            );
        }

        boolean mismoCodigo = Objects.equals(
                producto.getCodigoBarra(),
                codigoBarraExcel
        );

        if (coincidenciaPorNombre && mismoCodigo) {
            throw new ImportacionException(
                    "El código de barras del producto cambió desde el análisis. "
                    + "Debe analizar nuevamente la planilla."
            );
        }

        if (!coincidenciaPorNombre && !mismoCodigo) {
            throw new ImportacionException(
                    "El código de barras del producto cambió desde el análisis."
            );
        }

        // Guardar valores anteriores.
        Integer stockAnterior = producto.getCantidad();

        BigDecimal precioAnterior = producto.getPrecio() == null
                ? null
                : BigDecimal.valueOf(producto.getPrecio());

        boolean precioModificado = false;

        // Actualizar el precio solamente con autorización.
        if (actualizarPrecio) {

            if (precioExcel == null || precioExcel.signum() < 0) {
                throw new ProductoException("El precio importado no es válido.");
            }

            if (!Objects.equals(precioAnterior, precioActualDetectado)) {

                throw new ImportacionException(
                        "El precio del producto cambió después del análisis. "
                        + "Debe revisar nuevamente la importación."
                );
            }

            if (precioAnterior == null && precioActualDetectado != null) {
                throw new ImportacionException(
                        "El precio actual del producto cambió desde el análisis."
                );
            }

            precioModificado = precioAnterior == null
                    || precioAnterior.compareTo(precioExcel) != 0;

            if (precioModificado) {
                producto.setPrecio(precioExcel.doubleValue());
            }
        }

        // Incrementar el stock existente.
        Integer stockFinal;

        try {
            stockFinal = Math.addExact(
                    stockAnterior,
                    unidadesIngresadas
            );
        } catch (ArithmeticException ex) {
            throw new ImportacionException(
                    "El stock final supera la cantidad máxima permitida."
            );
        }

        producto.setCantidad(stockFinal);

        BigDecimal precioFinal = producto.getPrecio() == null
                ? null
                : BigDecimal.valueOf(producto.getPrecio());

        return new ResultadoActualizacionProductoDTO(
                producto.getIdProductos(),
                producto.getNombre(),
                producto.getCodigoBarra(),
                stockAnterior,
                unidadesIngresadas,
                stockFinal,
                precioAnterior,
                precioFinal,
                precioModificado
        );
    }

    private ProductoDTO crearProducto(
            String nombre,
            String categoria,
            Double precio,
            int cantidad,
            String codigoBarra,
            Long idPromo) {

        validarCodigoBarra(codigoBarra);

        String codigoCorto = extraerCodigoCorto(codigoBarra);

        validarCodigoCorto(codigoCorto);

        Promocion promocion = idPromo != null
                ? obtenerPromocion(idPromo)
                : obtenerSinPromocion();

        Producto producto = Producto.builder()
                .nombre(nombre)
                .precio(precio)
                .categoria(categoria)
                .cantidad(cantidad)
                .codigoBarra(codigoBarra)
                .codigoCorto(codigoCorto)
                .promo(promocion)
                .build();

        producto = productoRepository.save(producto);

        return ProductoMapper.toResponseDTO(producto);
    }

    private String normalizarNombre(String nombre) {

        if (nombre == null) {
            return "";
        }

        return nombre.trim().toLowerCase(Locale.ROOT);
    }
}
