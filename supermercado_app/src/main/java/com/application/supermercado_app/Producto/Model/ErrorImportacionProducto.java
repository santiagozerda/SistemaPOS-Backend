package com.application.supermercado_app.Producto.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "errores_importacion_producto",
        indexes = {
            @Index(
                    name = "idx_error_detalle",
                    columnList = "detalle_id"
            ),
            @Index(
                    name = "idx_error_codigo",
                    columnList = "codigo"
            )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorImportacionProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Columna del Excel donde se encontró el error.
    // Ejemplo: precio, stockAIngresar, codigoBarra.
    @Column(length = 100)
    private String columna;

    // Código interno del error.
    // Ejemplo: PRECIO_INVALIDO.
    @Column(length = 100)
    private String codigo;

    // Valor original encontrado en la celda.
    @Column(length = 2000)
    private String valor;

    // Descripción del problema encontrado.
    @Column(length = 1000)
    private String mensaje;

    // Fila a la que pertenece el error.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "detalle_id",
            nullable = false
    )
    private DetalleImportacionProducto detalle;
}
