package com.application.supermercado_app.Producto.Model;

import com.application.supermercado_app.Promocion.Model.Promocion;
import com.application.supermercado_app.Venta.Model.DetalleVentas;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProductos;
    private String nombre;
    private Double precio;
    private String categoria;
    private int cantidad;

    @Column(unique = true)
    private String codigoBarra;
    
    @Column(unique = true, length = 6)
    private String codigoCorto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idPromo")
    private Promocion promo;

    @OneToMany(mappedBy = "productos", fetch = FetchType.LAZY)
    private List<DetalleVentas> detalles;

}
