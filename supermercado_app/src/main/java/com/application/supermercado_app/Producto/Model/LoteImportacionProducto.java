package com.application.supermercado_app.Producto.Model;

import com.application.supermercado_app.Producto.Importacion.Model.EstadoLoteImportacion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "lotes_importacion_producto",
        indexes = {
            @Index(
                    name = "idx_lote_estado",
                    columnList = "estado"
            ),
            @Index(
                    name = "idx_lote_fecha_analisis",
                    columnList = "fecha_analisis"
            )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoteImportacionProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identificador público y único del lote.
    // Se utiliza para consultar y confirmar una importación.
    @Column(
            name = "lote_uuid",
            nullable = false,
            unique = true,
            updatable = false
    )
    private UUID loteUuid;

    // Nombre original del archivo Excel.
    @Column(
            name = "nombre_archivo",
            nullable = false,
            length = 255
    )
    private String nombreArchivo;

    // Estado actual de la importación.
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private EstadoLoteImportacion estado
            = EstadoLoteImportacion.ANALIZADA;

    // Fecha en la que se analizó el archivo.
    @Column(
            name = "fecha_analisis",
            nullable = false
    )
    private LocalDateTime fechaAnalisis;

    // Fecha límite para confirmar la importación.
    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    // Fecha en la que se confirmó la importación.
    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;

    // Cantidad total de filas analizadas.
    @Column(name = "total_filas", nullable = false)
    @Builder.Default
    private Integer totalFilas = 0;

    // Cantidad de productos nuevos creados.
    @Column(name = "productos_creados", nullable = false)
    @Builder.Default
    private Integer productosCreados = 0;

    // Cantidad de productos existentes cuyo stock fue incrementado.
    @Column(name = "stocks_incrementados", nullable = false)
    @Builder.Default
    private Integer stocksIncrementados = 0;

    // Cantidad de productos cuyo precio fue actualizado.
    @Column(name = "precios_actualizados", nullable = false)
    @Builder.Default
    private Integer preciosActualizados = 0;

    // Cantidad de filas omitidas.
    @Column(name = "filas_omitidas", nullable = false)
    @Builder.Default
    private Integer filasOmitidas = 0;

    // Detalles correspondientes a cada fila del Excel.
    @OneToMany(
            mappedBy = "lote",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<DetalleImportacionProducto> filas
            = new ArrayList<>();

    // Control de concurrencia optimista.
    @Version
    private Long version;

    // Mantiene sincronizadas ambas partes de la relación.
    public void agregarFila(DetalleImportacionProducto detalle) {

        if (detalle == null) {
            return;
        }

        filas.add(detalle);
        detalle.setLote(this);
    }

    public void eliminarFila(DetalleImportacionProducto detalle) {

        if (detalle == null) {
            return;
        }

        filas.remove(detalle);
        detalle.setLote(null);
    }
}
