package com.application.supermercado_app.Producto.Model;

import com.application.supermercado_app.Producto.Importacion.Model.AccionImportacion;
import com.application.supermercado_app.Producto.Importacion.Model.EstadoFilaImportacion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "detalles_importacion_producto",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_lote_numero_fila",
                    columnNames = {"lote_id", "numero_fila"}
            )
        },
        indexes = {
            @Index(
                    name = "idx_detalle_lote",
                    columnList = "lote_id"
            ),
            @Index(
                    name = "idx_detalle_estado",
                    columnList = "estado_fila"
            )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleImportacionProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lote al que pertenece esta fila.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "lote_id",
            nullable = false
    )
    private LoteImportacionProducto lote;

    // Número físico de fila dentro del Excel.
    @Column(
            name = "numero_fila",
            nullable = false
    )
    private Integer numeroFila;

    // ==========================================
    // DATOS ORIGINALES DEL EXCEL
    // ==========================================
    // Código de barras original.
    // Se almacena como String para conservar ceros iniciales.
    @Column(name = "codigo_barra", length = 100)
    private String codigoBarra;

    @Column(length = 255)
    private String nombre;

    // BigDecimal evita problemas de precisión monetaria
    // durante el procesamiento del Excel.
    @Column(
            name = "precio_excel",
            precision = 15,
            scale = 2
    )
    private BigDecimal precioExcel;

    // Cantidad que se desea ingresar.
    @Column(name = "stock_a_ingresar")
    private Integer stockAIngresar;

    // La categoría continúa siendo String.
    @Column(length = 255)
    private String categoria;

    // ==========================================
    // RESULTADO DEL ANÁLISIS
    // ==========================================
    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado_fila",
            nullable = false,
            length = 40
    )
    private EstadoFilaImportacion estadoFila;

    // Identificador del producto encontrado mediante
    // una coincidencia exacta de código de barras.
    private Long idProductoDetectado;

    // Datos del producto detectado en el momento del análisis.
    @Column(name = "codigo_barra_detectado", length = 100)
    private String codigoBarraDetectado;

    @Column(name = "nombre_detectado", length = 255)
    private String nombreDetectado;

    @Column(
            name = "precio_actual_detectado",
            precision = 15,
            scale = 2
    )
    private BigDecimal precioActualDetectado;

    @Column(name = "stock_actual_detectado")
    private Integer stockActualDetectado;

    // IDs de los productos encontrados por coincidencia de nombre.
    // Permite que el ADMINISTRADOR seleccione cuál corresponde.
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "detalle_importacion_coincidencias",
            joinColumns = @JoinColumn(name = "detalle_id")
    )
    @Column(
            name = "producto_id",
            nullable = false
    )
    @OrderColumn(name = "orden")
    @Builder.Default
    private List<Long> idsProductosCoincidentes
            = new ArrayList<>();

    // ==========================================
    // DECISIÓN DEL ADMINISTRADOR
    // ==========================================
    @Enumerated(EnumType.STRING)
    @Column(
            name = "accion_aplicada",
            length = 30
    )
    private AccionImportacion accionAplicada;

    // Producto sobre el que finalmente se aplicó
    // la operación de importación.
    private Long idProductoResultado;

    // ==========================================
    // RESULTADO DEL STOCK
    // ==========================================
    @Column(name = "stock_anterior")
    private Integer stockAnterior;

    @Column(name = "unidades_ingresadas")
    private Integer unidadesIngresadas;

    @Column(name = "stock_final")
    private Integer stockFinal;

    // ==========================================
    // RESULTADO DEL PRECIO
    // ==========================================
    @Column(
            name = "precio_anterior",
            precision = 15,
            scale = 2
    )
    private BigDecimal precioAnterior;

    @Column(
            name = "precio_final",
            precision = 15,
            scale = 2
    )
    private BigDecimal precioFinal;

    @Column(
            name = "precio_actualizado",
            nullable = false
    )
    @Builder.Default
    private boolean precioActualizado = false;

    // ==========================================
    // ERRORES DE LA FILA
    // ==========================================
    @OneToMany(
            mappedBy = "detalle",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<ErrorImportacionProducto> errores
            = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "detalle_importacion_coincidencia_snapshot",
            joinColumns = @JoinColumn(name = "detalle_id")
    )
    @OrderColumn(name = "orden")
    @Builder.Default
    private List<ProductoCoincidencia> coincidenciasDetectadas
            = new ArrayList<>();

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================
    public void agregarError(ErrorImportacionProducto error) {

        if (error == null) {
            return;
        }

        errores.add(error);
        error.setDetalle(this);
    }

    public void eliminarError(ErrorImportacionProducto error) {

        if (error == null) {
            return;
        }

        errores.remove(error);
        error.setDetalle(null);
    }

    public void agregarProductoCoincidente(Long idProducto) {

        if (idProducto == null) {
            return;
        }

        if (!idsProductosCoincidentes.contains(idProducto)) {
            idsProductosCoincidentes.add(idProducto);
        }
    }
}
