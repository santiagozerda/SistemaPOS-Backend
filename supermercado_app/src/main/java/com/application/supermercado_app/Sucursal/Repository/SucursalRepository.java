
package com.application.supermercado_app.Sucursal.Repository;

import com.application.supermercado_app.Sucursal.Model.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long>{
    
}
