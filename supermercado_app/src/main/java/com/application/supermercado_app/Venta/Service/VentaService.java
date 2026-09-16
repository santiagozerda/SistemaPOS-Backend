package com.application.supermercado_app.Venta.Service;

import com.application.supermercado_app.Exception.NotFoundException;
import com.application.supermercado_app.Exception.ProductoException;
import com.application.supermercado_app.Exception.StockInsuficienteException;
import com.application.supermercado_app.Exception.VentaException;
import com.application.supermercado_app.Pago.Model.EstadoPago;
import com.application.supermercado_app.Pago.Model.Pago;
import com.application.supermercado_app.Pago.Repository.PagoRepository;
import com.application.supermercado_app.Promocion.Service.PromocionService;

import com.application.supermercado_app.Venta.DTO.VentaCompletaDTO;
import com.application.supermercado_app.Venta.Model.DetalleVentas;
import com.application.supermercado_app.Venta.Model.EstadoVenta;
import com.application.supermercado_app.Producto.Model.Producto;
import com.application.supermercado_app.Sucursal.Model.Sucursal;
import com.application.supermercado_app.Promocion.Model.TipoPromocion;
import com.application.supermercado_app.Venta.Model.Venta;
import com.application.supermercado_app.Producto.Repository.ProductoRepository;
import com.application.supermercado_app.Sucursal.Repository.SucursalRepository;
import com.application.supermercado_app.Venta.DTO.CrearVentaRequestDTO;
import com.application.supermercado_app.Venta.DTO.DetalleVentaDTO;
import com.application.supermercado_app.Venta.DTO.DetalleVentaRequestDTO;
import com.application.supermercado_app.Venta.DTO.VentaPreviewDTO;
import com.application.supermercado_app.Venta.DTO.VentaPreviewRequestDTO;
import com.application.supermercado_app.Venta.DTO.VentaResumenDTO;
import com.application.supermercado_app.Venta.Mapper.VentaMapper;

import com.application.supermercado_app.Venta.Repository.VentaRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VentaService implements IVentaService {

    private final VentaRepository ventaRepository;
    private final SucursalRepository sucursalRepository;
    private final ProductoRepository productoRepository;
    private final PromocionService promocionService;
    private final PagoRepository pagoRepository;

    public VentaService(VentaRepository ventaRepository,
            SucursalRepository sucursalRepository,
            ProductoRepository productoRepository,
            PromocionService promocionService,
            PagoRepository pagoRepository) {

        this.ventaRepository = ventaRepository;
        this.sucursalRepository = sucursalRepository;
        this.productoRepository = productoRepository;
        this.promocionService = promocionService;
        this.pagoRepository = pagoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResumenDTO> getVentas() {
        return ventaRepository.findAllConPago()
                .stream()
                .map(VentaMapper::toResumenDTO)
                .toList();
    }

    @Override
    @Transactional
    public VentaCompletaDTO saveVenta(CrearVentaRequestDTO crearVentaDTO) {

        //Validar Request
        validarVenta(crearVentaDTO);

        //Obtener Sucursal
        Sucursal sucursal = obtenerSucursal(crearVentaDTO.getIdSucursal());

        //Crear Venta
        Venta venta = Venta.builder()
                .fechaVenta(LocalDateTime.now())
                .estado(EstadoVenta.PENDIENTE)
                .sucursal(sucursal)
                .build();

        //Calcular Detalles
        List<DetalleVentas> detalles = calcularDetalles(
                crearVentaDTO.getListaDetalle(),
                venta
        );

        venta.setDetalles(detalles);

        //Calcular Total
        venta.setTotal(calcularTotal(detalles));
        venta.setTotalDescuento(calcularTotalDescuento(detalles));

        venta = ventaRepository.save(venta);

        return VentaMapper.toResponseDTO(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaCompletaDTO findVenta(Long idVenta) {

        Venta venta = ventaRepository.findVentaConDetalleYPago(idVenta)
                .orElseThrow(() -> new VentaException("Venta no encontrada"));

        ventaRepository.findVentaConTicket(idVenta)
                .ifPresent(v -> venta.setTicket(v.getTicket()));

        return VentaMapper.toResponseDTO(venta);
    }

    @Transactional
    public void aprobarVenta(Venta venta) {

        if (venta == null || venta.getIdVenta() == null) {
            throw new VentaException("Esta venta no puede ser nula");
        }

        ventaAprobada(venta.getIdVenta());
    }

    @Transactional
    public void ventaAprobada(Long idVenta) {

        Venta venta = obtenerVentaParaAprobacion(idVenta);

        if (venta.getEstado() == EstadoVenta.APROBADA) {
            return;
        }

        if (venta.getEstado() != EstadoVenta.PENDIENTE) {
            throw new VentaException("Solo se pueden aprobar ventas en estado PENDIENTE");
        }

        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new VentaException("No se puede aprobar una venta que no tiene productos cargados");
        }

        descontarStock(venta);

        venta.setEstado(EstadoVenta.APROBADA);

        ventaRepository.save(venta);
    }

    private List<DetalleVentas> calcularDetalles(
            List<DetalleVentaRequestDTO> detallesDTO,
            Venta venta) {

        List<DetalleVentas> detalles = new ArrayList<>();

        for (DetalleVentaRequestDTO detalleDTO : detallesDTO) {

            if (detalleDTO == null) {
                throw new VentaException("El detalle de venta no puede ser nulo");
            }

            if (detalleDTO.getIdProducto() == null) {
                throw new VentaException("Cada detalle debe tener al menos un producto");
            }

            if (detalleDTO.getCantidadVendida() == null || detalleDTO.getCantidadVendida() <= 0) {
                throw new VentaException("No puede haber un venta que no tenga productos");
            }

            Producto producto = obtenerProducto(detalleDTO.getIdProducto());

            if (producto.getCantidad() < detalleDTO.getCantidadVendida()) {
                throw new StockInsuficienteException(
                        "Producto sin stock: " + producto.getNombre()
                );
            }

            int cantidad = detalleDTO.getCantidadVendida();

            double precioUnitario = producto.getPrecio();

            double subtotal = precioUnitario * cantidad;

            double precioFinal = promocionService.calcularTotal(producto, cantidad);

            double descuento = subtotal - precioFinal;

            TipoPromocion promocionAplicada = TipoPromocion.SIN_PROMOCION;

            if (producto.getPromo() != null && descuento > 0) {
                promocionAplicada = producto.getPromo().getTipo();
            }

            DetalleVentas detalle = new DetalleVentas();

            detalle.setVenta(venta);
            detalle.setProductos(producto);
            detalle.setCantidadVendida(cantidad);
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubTotal(subtotal);
            detalle.setPrecioFinal(precioFinal);
            detalle.setDescuentoAplicado(descuento);
            detalle.setPromo(promocionAplicada);

            detalles.add(detalle);
        }

        return detalles;
    }

    private double calcularTotal(List<DetalleVentas> detalles) {

        return detalles.stream()
                .mapToDouble(DetalleVentas::getPrecioFinal)
                .sum();
    }

    private void descontarStock(Venta venta) {

        for (DetalleVentas detalle : venta.getDetalles()) {

            Long idProducto = detalle.getProductos().getIdProductos();

            Producto producto = productoRepository.findByIdForUpdate(idProducto)
                    .orElseThrow(() -> new ProductoException("Producto no encontrado"));

            int cantidadVendida = detalle.getCantidadVendida();

            if (producto.getCantidad() < cantidadVendida) {
                throw new StockInsuficienteException(
                        "Stock insuficiente para el producto: "
                        + producto.getNombre()
                );
            }

            producto.setCantidad(producto.getCantidad() - cantidadVendida);

            detalle.setProductos(producto);
        }
    }

    private void validarVenta(CrearVentaRequestDTO ventaDTO) {

        if (ventaDTO == null) {
            throw new VentaException("La venta no puede ser nula.");
        }

        if (ventaDTO.getIdSucursal() == null) {
            throw new IllegalArgumentException(
                    "Debe indicar una sucursal."
            );
        }

        if (ventaDTO.getListaDetalle() == null
                || ventaDTO.getListaDetalle().isEmpty()) {

            throw new VentaException(
                    "Debe agregar al menos un producto."
            );
        }
    }

    private Venta obtenerVenta(Long id) {

        if (id == null) {
            throw new VentaException("El ID de la venta no puede ser nulo");
        }

        return ventaRepository.findById(id)
                .orElseThrow(()
                        -> new VentaException("Venta no encontrada."));
    }

    private Sucursal obtenerSucursal(Long id) {

        if (id == null) {
            throw new NotFoundException("El ID de la sucursal no puede ser nulo");
        }

        return sucursalRepository.findById(id)
                .orElseThrow(()
                        -> new NotFoundException("Sucursal no encontrada."));
    }

    private Producto obtenerProducto(Long id) {

        if (id == null) {
            throw new VentaException("El ID del producto no puede ser nulo");
        }

        return productoRepository.findByIdWithPromo(id)
                .orElseThrow(()
                        -> new ProductoException("Producto no encontrado."));
    }

    @Override
    @Transactional
    public VentaCompletaDTO anularVenta(Long idVenta) {

        Venta venta = obtenerVentaParaAnulacion(idVenta);

        if (venta.getEstado() != EstadoVenta.APROBADA) {
            throw new VentaException("Solo se pueden anular ventas que estan APROBADAS");

        }

        Pago pago = venta.getPago();

        if (pago == null) {
            throw new VentaException("La venta aprobada no posee " + " un pago asociado");
        }

        if (pago.getEstado() != EstadoPago.APROBADO) {
            throw new VentaException("El pago asociado a la venta " + " no se encunetra APROBADO");
        }

        devolverStock(venta);

        venta.setEstado(EstadoVenta.ANULADA);

        pago.setEstado(EstadoPago.ANULADO);

        pagoRepository.save(pago);

        ventaRepository.save(venta);

        return VentaMapper.toResponseDTO(venta);

    }

    @Override
    @Transactional
    public VentaCompletaDTO cancelarVenta(Long id) {

        Venta venta = obtenerVentaParaModificacion(id);

        if (venta.getEstado() != EstadoVenta.PENDIENTE) {
            throw new VentaException("Solo se puede cancelar las ventas que estan en estado PENDIENTE");

        }

        venta.setEstado(EstadoVenta.CANCELADA);

        ventaRepository.save(venta);

        return VentaMapper.toResponseDTO(venta);
    }

    private Venta obtenerVentaParaAprobacion(Long idVenta) {
        return obtenerVenta(idVenta);
    }

    private Venta obtenerVentaParaModificacion(Long idVenta) {
        return obtenerVenta(idVenta);
    }

    private void devolverStock(Venta venta) {

        for (DetalleVentas detalle : venta.getDetalles()) {

            Long idProducto = detalle.getProductos().getIdProductos();

            Producto producto = productoRepository.findByIdForUpdate(idProducto)
                    .orElseThrow(() -> new ProductoException("Producto no encontrado"));

            producto.setCantidad(producto.getCantidad() + detalle.getCantidadVendida());

            detalle.setProductos(producto);

        }
    }

    private Venta obtenerVentaParaAnulacion(Long idVenta) {

        if (idVenta == null) {

            throw new VentaException("El Id de la venta no puede ser nulo");
        }

        return ventaRepository.findVentaParaAnulacion(idVenta)
                .orElseThrow(() -> new VentaException("Venta no encontrada"));

    }

    @Override
    @Transactional(readOnly = true)
    public VentaPreviewDTO previewVenta(VentaPreviewRequestDTO previewDTO) {

        if (previewDTO == null || previewDTO.getListDetalle() == null || previewDTO.getListDetalle().isEmpty()) {
            throw new VentaException("Debe agregar al menos un producto");
        }

        List<DetalleVentaDTO> detalles = new ArrayList<>();

        double total = 0;

        for (DetalleVentaRequestDTO detalleDTO : previewDTO.getListDetalle()) {

            if (detalleDTO == null || detalleDTO.getIdProducto() == null) {
                throw new VentaException("Cada detalle debe tener un producto.");
            }

            if (detalleDTO.getCantidadVendida() == null || detalleDTO.getCantidadVendida() <= 0) {
                throw new VentaException("La cantidad debe ser mayor a 0.");
            }

            Producto producto = obtenerProducto(detalleDTO.getIdProducto());

            int cantidad = detalleDTO.getCantidadVendida();

            if (producto.getCantidad() < cantidad) {
                throw new StockInsuficienteException(
                        "Producto sin stock: " + producto.getNombre()
                );
            }

            double precioUnitario = producto.getPrecio();
            double subtotal = precioUnitario * cantidad;
            double precioFinal = promocionService.calcularTotal(producto, cantidad);
            double descuento = subtotal - precioFinal;

            TipoPromocion promocionAplicada = TipoPromocion.SIN_PROMOCION;
            if (producto.getPromo() != null && descuento > 0) {
                promocionAplicada = producto.getPromo().getTipo();
            }

            detalles.add(
                    DetalleVentaDTO.builder()
                            .idProducto(producto.getIdProductos())
                            .nombreProducto(producto.getNombre())
                            .cantidadVendida(cantidad)
                            .precioUnitario(precioUnitario)
                            .subTotal(subtotal)
                            .promo(promocionAplicada)
                            .descuAplicado(descuento)
                            .build()
            );

            total += precioFinal;
        }

        return VentaPreviewDTO.builder()
                .listaDetalle(detalles)
                .total(total)
                .build();

    }

    private double calcularTotalDescuento(List<DetalleVentas> detalles) {
        return detalles.stream()
                .mapToDouble(DetalleVentas::getDescuentoAplicado)
                .sum();
    }
}
