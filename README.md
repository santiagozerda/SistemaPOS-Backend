# 🛒 Sistema de Gestión POS — Backend

API REST del **Sistema de Gestión POS**, desarrollada con **Java y Spring Boot**.

El backend centraliza la lógica de negocio, persistencia de datos, autenticación, autorización y gestión de las operaciones principales de un sistema de punto de venta.

El proyecto fue desarrollado como una aplicación **monolítica**, organizada por capas y orientada a mantener separadas las responsabilidades entre controladores, servicios, repositorios, entidades y objetos de transferencia de datos.

---

## 📋 Descripción

El sistema permite gestionar las operaciones principales de un punto de venta, desde la administración de productos y promociones hasta la ejecución de ventas, procesamiento de pagos, generación de tickets y elaboración de reportes.

El backend expone una **API REST** que puede ser consumida por diferentes clientes frontend.

Entre sus principales responsabilidades se encuentran:

* 👤 Gestión de usuarios y roles.
* 🔐 Autenticación y autorización.
* 🏪 Gestión de sucursales.
* 📦 Gestión de productos.
* 🏷️ Gestión de promociones.
* 🛒 Gestión de ventas.
* 💳 Gestión de pagos.
* 🧾 Generación de tickets.
* 📊 Generación de reportes.
* 📦 Control y restauración de stock.
* 🔄 Control del estado de las ventas.

---

# 🛠️ Tecnologías

* ☕ **Java**
* 🌱 **Spring Boot**
* 🔐 **Spring Security**
* 🎟️ **JWT**
* 🗄️ **Spring Data JPA**
* 🧩 **Hibernate**
* 🐬 **MySQL**
* 📦 **Maven**
* 🔄 **API REST**

---

# 🏗️ Arquitectura

El proyecto utiliza una arquitectura monolítica organizada por capas.

```text
┌──────────────────────────────┐
│          REST API            │
│        Controllers           │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│           Services           │
│        Lógica de negocio     │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│         Repositories         │
│       Acceso a datos         │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│            MySQL             │
│       Persistencia           │
└──────────────────────────────┘
```

Además, el proyecto utiliza **DTOs y Mappers** para evitar exponer directamente las entidades de persistencia mediante la API.

```text
Request
   ↓
DTO
   ↓
Controller
   ↓
Service
   ↓
Entity
   ↓
Repository
   ↓
Database
```

Para las respuestas:

```text
Database
   ↓
Entity
   ↓
Mapper
   ↓
DTO
   ↓
JSON
   ↓
Frontend
```

---

# 🔐 Autenticación y autorización

La seguridad de la aplicación está implementada mediante **Spring Security + JWT**.

El sistema utiliza autenticación basada en credenciales de usuario y genera un token JWT después de validar correctamente el acceso.

### 🔑 Flujo de autenticación

```text
┌───────────────┐
│    Cliente    │
└───────┬───────┘
        │
        │ email + password
        ▼
┌─────────────────────┐
│   Auth Controller   │
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│    Auth Service     │
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│ Spring Security     │
│ Authentication      │
└─────────┬───────────┘
          │
          ▼
      Credenciales
       válidas
          │
          ▼
     Generación JWT
          │
          ▼
    HttpOnly Cookie
```

El JWT se almacena mediante una **cookie `HttpOnly`**, evitando que el token pueda ser accedido directamente mediante JavaScript del navegador.

### 🍪 Cookie de autenticación

La cookie utilizada para la sesión contiene:

* 🔒 `HttpOnly`
* 🌐 `Path=/`
* ⏱️ Tiempo de expiración configurado.
* 🛡️ Configuración `SameSite`.
* 🔐 `Secure` configurable según el entorno.

En desarrollo local se permite la configuración necesaria para trabajar mediante HTTP, mientras que en producción debe utilizarse una configuración segura mediante HTTPS.

---

# 👥 Roles y autorización

El sistema contempla dos roles principales:

```text
ADMINISTRADOR
CAJERO
```

La autoridad de Spring Security se construye utilizando el prefijo:

```text
ROLE_ADMINISTRADOR
ROLE_CAJERO
```

La autorización se realiza a nivel de método mediante `@PreAuthorize`.

Por ejemplo:

```java
@PreAuthorize("hasRole('ADMINISTRADOR')")
```

o:

```java
@PreAuthorize("hasAnyRole('CAJERO', 'ADMINISTRADOR')")
```

Esto permite que determinadas operaciones sean compartidas entre ambos roles mientras que otras permanezcan restringidas.

### 🔒 Principio de autorización

La seguridad no depende únicamente del frontend.

El frontend puede ocultar determinadas funcionalidades según el usuario, pero la autorización real se realiza en el backend.

```text
Frontend
   │
   │ Request
   ▼
Spring Security
   │
   ├── Autenticado
   │
   └── Autorizado
          │
          ▼
      Controller
          │
          ▼
       Service
```

---

# 👤 Gestión de usuarios

Los usuarios del sistema poseen información básica de identificación y autenticación, además de un rol asociado.

Entre los datos gestionados se encuentran:

* 🆔 Identificador.
* 👤 Nombre.
* 📧 Email.
* 🔑 Contraseña.
* 🔘 Estado activo/inactivo.
* 👥 Rol.

La contraseña no debe almacenarse en texto plano y debe ser gestionada mediante los mecanismos de seguridad proporcionados por Spring Security.

---

# 🏪 Gestión de sucursales

El sistema permite administrar las sucursales donde se realizan las operaciones comerciales.

Las sucursales pueden contener información como:

* 🏪 Nombre.
* 📍 Dirección.
* 📞 Teléfono.
* 📧 Email.
* 🔘 Estado.

Las operaciones de creación, modificación, consulta y eliminación se gestionan mediante la capa de servicios y repositorios correspondientes.

---

# 📦 Gestión de productos

Los productos representan los artículos comercializados por el supermercado.

El sistema permite gestionar:

* 🆔 Identificación del producto.
* 🏷️ Nombre.
* 💰 Precio.
* 📦 Stock.
* 🔘 Estado.
* 🏷️ Promoción asociada.

El stock participa directamente en el flujo de ventas.

Antes de realizar una venta se valida que exista disponibilidad suficiente del producto.

---

# 🏷️ Sistema de promociones

El backend implementa reglas de promociones aplicables a los productos vendidos.

Entre las promociones contempladas se encuentran:

* `TRES_POR_DOS`
* `SEGUNDA_UNIDAD_50`
* `DOS_POR_UNO`
* `SIN_PROMOCION`

Las promociones se procesan dentro de la lógica de negocio de la venta.

El cálculo considera:

```text
Precio unitario
      ↓
Cantidad
      ↓
Promoción
      ↓
Descuento
      ↓
Precio final
```

La promoción modifica el cálculo económico de la operación, pero mantiene separado el concepto de producto.

Por lo tanto, eliminar o modificar una promoción **no implica eliminar el producto asociado**.

---

# 🛒 Flujo de venta

La venta constituye una de las operaciones centrales del sistema.

El proceso general es:

```text
1. Crear venta
       ↓
2. Validar productos
       ↓
3. Validar stock
       ↓
4. Aplicar promociones
       ↓
5. Calcular subtotales
       ↓
6. Calcular descuentos
       ↓
7. Calcular total
       ↓
8. Descontar stock
       ↓
9. Registrar venta
       ↓
10. Procesar pago
       ↓
11. Generar ticket
```

La lógica de negocio se mantiene dentro de los servicios y no en los controladores.

---

# 🧮 Cálculo de una venta

Cada detalle de venta conserva información relacionada con:

* 📦 Producto.
* 🔢 Cantidad vendida.
* 💰 Precio unitario.
* 💵 Subtotal.
* 🏷️ Promoción aplicada.
* 💸 Descuento.
* 💰 Precio final.

Conceptualmente:

```text
Subtotal = cantidad × precio unitario

Descuento = según promoción

Precio final = subtotal - descuento

Total venta = Σ precios finales
```

Esto permite conservar información suficiente para reconstruir posteriormente cómo se obtuvo el importe final de una operación.

---

# 📦 Control de stock

El stock es validado durante la creación de una venta.

Antes de descontar unidades se verifica que:

```text
stock disponible >= cantidad solicitada
```

Si no existe stock suficiente, la operación no debe completarse.

Cuando una venta aprobada es anulada, el sistema contempla la **restauración del stock correspondiente**.

```text
Venta aprobada
      │
      ▼
Descuento de stock
      │
      ▼
Anulación
      │
      ▼
Restauración de stock
```

Esto evita que una venta anulada continúe afectando el inventario.

---

# 💳 Pagos

El sistema contempla el procesamiento y registro del pago asociado a una venta.

Los métodos de pago son representados mediante una enumeración específica.

El flujo conceptual es:

```text
Venta
  ↓
Pago
  ↓
Validación
  ↓
Estado del pago
```

La venta y el pago mantienen responsabilidades diferenciadas, permitiendo consultar posteriormente el estado de cada operación.

### 💵 Efectivo

En las operaciones pagadas en efectivo, una venta anulada contempla la devolución correspondiente y la restauración del stock.

### 🌐 Mercado Pago

Integración con Mercado Pago para los pago mediante transferencia.

---

# 🧾 Tickets

Una vez que una venta aprobada ha sido pagada, el sistema puede generar el ticket correspondiente.

El ticket contiene información derivada de la venta, incluyendo:

* 🔢 Número de ticket.
* 📅 Fecha de emisión.
* 💰 Total.
* 💳 Método de pago.
* 📦 Detalles de productos.
* 🏷️ Promociones aplicadas.
* 💸 Descuentos.
* 🔄 Estado de la venta.

El ticket mantiene una relación con la venta original.

```text
Venta
  │
  ├── Detalles
  ├── Pago
  └── Ticket
```

El estado de la venta también puede reflejarse en la información del ticket, permitiendo que el cliente conozca si una operación posteriormente fue anulada.

---

# 🔄 Anulación de ventas

Las ventas no se eliminan físicamente cuando deben ser anuladas.

En su lugar, se modifica su estado:

```text
APROBADA
   ↓
ANULADA
```

Esto permite conservar el historial de las operaciones realizadas.

Al anular una venta:

* 🔄 Se modifica el estado de la venta.
* 📦 Se restaura el stock.
* 💵 En ventas en efectivo se contempla la devolución correspondiente.
* 📊 Las ventas anuladas no deben formar parte de los resultados comerciales.
* 🧾 El ticket asociado conserva la referencia de la operación y puede mostrar el nuevo estado.

Este enfoque permite mantener trazabilidad sobre las operaciones realizadas.

---

# 📊 Reportes

El backend incorpora endpoints destinados a obtener información resumida y detallada sobre las ventas.

Los reportes contemplan diferentes períodos:

```text
📅 Día
📅 Semana
📅 Mes
```

La información puede incluir:

* 💰 Total vendido.
* 🛒 Cantidad de ventas.
* 📈 Promedio por venta.
* 🧾 Ventas realizadas.
* 📦 Productos más vendidos.

Las ventas anuladas se excluyen de los resultados comerciales.

El cálculo de períodos utiliza una lógica común para evitar que diferentes endpoints interpreten una semana o período de manera diferente.

---

# 🧩 DTOs y Mappers

El proyecto utiliza **DTOs (Data Transfer Objects)** como contrato entre la API y los clientes.

Esto evita exponer directamente las entidades JPA.

Ejemplo conceptual:

```text
Entity
   │
   ▼
Mapper
   │
   ▼
DTO
   │
   ▼
JSON
```

Entre los beneficios de este enfoque:

* 🔒 Evita exponer directamente la estructura de persistencia.
* 🧩 Permite definir contratos específicos para cada operación.
* 🔄 Facilita la evolución de las entidades.
* 📦 Reduce la cantidad de información enviada al cliente.
* 🧹 Mantiene separada la lógica de persistencia de la representación de la API.

---

# 🗄️ Persistencia

La persistencia se implementa utilizando:

* Spring Data JPA.
* Hibernate.
* MySQL.

Los repositorios encapsulan las operaciones de acceso a datos.

```text
Service
   ↓
Repository
   ↓
JPA / Hibernate
   ↓
MySQL
```

Las consultas específicas se implementan mediante métodos derivados de Spring Data JPA o consultas JPQL cuando la operación requiere un comportamiento más específico.

---

# 🔄 Manejo de transacciones

Las operaciones que modifican múltiples entidades relacionadas se gestionan mediante transacciones.

Esto resulta especialmente importante en operaciones como:

```text
Venta
 ├── Detalles
 ├── Stock
 ├── Pago
 └── Ticket
```

El objetivo es evitar estados parciales cuando una operación compuesta falla durante su ejecución.

---

# ⚠️ Manejo de excepciones

La aplicación utiliza excepciones específicas para representar diferentes situaciones de negocio.

Por ejemplo:

* `NotFoundException`
* `VentaException`

Esto permite diferenciar errores relacionados con:

* Recursos inexistentes.
* Reglas de negocio.
* Operaciones de venta.
* Validaciones.

La API puede transformar estas excepciones en respuestas HTTP apropiadas para que el cliente pueda interpretar correctamente el resultado de una operación.

---

# 📡 API REST

Los recursos del sistema se exponen mediante endpoints REST.

Entre los principales módulos se encuentran:

```text
/app/auth
/app/usuario
/app/sucursal
/app/producto
/app/promocion
/app/venta
/app/pago
/app/ticket
/app/reporte
```

La API utiliza métodos HTTP según la naturaleza de cada operación:

```text
GET     → Consultas
POST    → Creación
PUT     → Actualización
DELETE  → Eliminación
```

---

# ⚙️ Configuración

El proyecto utiliza variables de entorno para evitar almacenar información sensible directamente en el código fuente.

Ejemplo:

```env
DB_URL=jdbc:mysql://localhost:3306/pos
DB_USERNAME=usuario
DB_PASSWORD=contraseña
JWT_SECRET=secret
```
Se recomienda utilizar un archivo:

```text
.env.example
```

como referencia para la configuración del entorno.

---

# 🚀 Ejecución local

### 1️⃣ Clonar el repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
```

### 2️⃣ Ingresar al proyecto

```bash
cd sistema-gestion-pos-backend
```

### 3️⃣ Configurar las variables de entorno

Crear las variables necesarias para la conexión con MySQL y la configuración de seguridad.

### 4️⃣ Ejecutar la aplicación

En Linux/macOS:

```bash
./mvnw spring-boot:run
```

En Windows:

```bash
mvnw.cmd spring-boot:run
```

---

# 🗄️ Base de datos

El sistema utiliza **MySQL** como motor de base de datos.

Antes de iniciar la aplicación es necesario disponer de una instancia de MySQL y configurar correctamente las variables de conexión.

Configuración de ejemplo:

```env
DB_URL=jdbc:mysql://localhost:3306/pos
DB_USERNAME=usuario
DB_PASSWORD=contraseña
```

---

# 🌐 Frontend

El frontend funciona como una aplicación independiente que consume esta API REST.

Actualmente el proyecto cuenta con un frontend desarrollado utilizando tecnologías del ecosistema React/Next.js.

Repositorio:

👉 **Sistema de Gestión POS — Frontend**

https://github.com/santiagozerda/SistemaPOS-Frontend

La arquitectura desacoplada permite evolucionar o reemplazar el frontend sin modificar la lógica principal del backend.

---

# 🔮 Evolución del proyecto

El proyecto está planteado para permitir la evolución independiente de sus componentes.

Una de las líneas de evolución previstas es la migración del frontend actual hacia **Angular**.

```text
                 API REST
                    │
          ┌─────────┴─────────┐
          │                   │
       Frontend             Backend
       Angular            Spring Boot
```

La separación entre frontend y backend permite realizar esta migración sin necesidad de trasladar la lógica de negocio al nuevo cliente.

---

# 📁 Estructura general

La estructura del backend sigue una organización basada en responsabilidades:

```text
src/
└── main/
    ├── java/
    │   └── ...
    │       ├── controller/
    │       ├── service/
    │       ├── repository/
    │       ├── entity/
    │       ├── dto/
    │       ├── mapper/
    │       ├── security/
    │       └── exception/
    │
    └── resources/
        └── application.properties
```

La estructura puede evolucionar a medida que se incorporen nuevos módulos y funcionalidades.

---

# 📌 Principios aplicados

Durante el desarrollo se priorizan los siguientes principios:

* 🧩 Separación de responsabilidades.
* 🔒 Seguridad desde el backend.
* 📦 Uso de DTOs.
* 🔄 Uso de Mappers.
* 🧠 Centralización de reglas de negocio en Services.
* 🗄️ Abstracción del acceso a datos mediante Repositories.
* 🔐 Protección de información sensible.
* 🔄 Uso de transacciones en operaciones complejas.
* 📚 Mantenimiento de trazabilidad sobre las ventas.
* 🧱 Diseño preparado para evolución tecnológica.

---

# 📈 Estado del proyecto

🚧 **Proyecto personal en desarrollo.**

El objetivo es continuar evolucionando el Sistema de Gestión POS incorporando mejoras de arquitectura, funcionalidades y experiencia de usuario.

La API constituye el núcleo de negocio del sistema y está preparada para ser consumida por diferentes clientes frontend.

---

# 👨‍💻 Autor

**Santiago Zerda**
