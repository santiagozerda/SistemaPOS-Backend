package com.application.supermercado_app.Promocion.Model;

import com.application.supermercado_app.Producto.Model.Producto;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPromo;
    private String descripcion;

    private boolean activa;

    //Utilizamos Enumerated para facilitar la lectura en las tablas
    @Enumerated(EnumType.STRING)
    private TipoPromocion tipo;

    @OneToMany(mappedBy = "promo", fetch = FetchType.EAGER)
    private List<Producto> productos;

}
