# 🛒 Sistema de Gestión Retail & Ventas

Backend desarrollado con Java y Spring Boot orientado a la gestión de ventas retail, aplicación de promociones y procesamiento de pagos.  
La aplicación permite administrar productos, sucursales y promociones, además de gestionar el flujo completo de una venta desde su creación hasta la generación automática de tickets.

---

# 🚀 Características Principales

- Gestión de productos y sucursales
- Administración de promociones comerciales
- Generación de ventas con cálculo automático de descuentos
- Procesamiento de pagos en efectivo y tarjeta
- Validación del estado de pago
- Generación automática de tickets
- Arquitectura REST escalable
- Integración con MySQL y Docker

---

# 🧩 Reglas de Negocio Implementadas

## 🏷️ Promociones Automáticas
El sistema permite aplicar promociones sobre productos:

- `3x2`
- `50% en la segunda unidad`

Durante la generación de una venta, los descuentos se calculan automáticamente en función de los productos y cantidades seleccionadas.

---

## 💳 Procesamiento de Pago
La aplicación permite procesar pagos utilizando:

- Efectivo
- Tarjeta de Crédito
- Tarjeta de Débito

Cuando el pago es aprobado:

- el estado de la venta cambia a `APROBADA`
- se genera automáticamente un ticket

Si el pago es rechazado:

- la venta pasa al estado `RECHAZADA`
- no se genera ticket

---

# 🏗️ Arquitectura del Proyecto

La aplicación utiliza arquitectura multicapa basada en el patrón MVC:

```text
Controller → Service → Repository → Database
```

Además, implementa:

- DTO Pattern
- Mapper Pattern
- Separación por capas
- Manejo de excepciones personalizadas

---

# 📦 Tecnologías Utilizadas

- Java 17
- Spring Boot
- Maven
- MySQL
- H2 Database
- Docker
- Docker Compose

---

# 📁 Estructura del Proyecto

```text
controller/
 ├── PagoController
 ├── ProductoController
 ├── PromocionController
 ├── SucursalController
 ├── TicketController
 └── VentaController

dto/
 ├── AsignarPromocionDTO
 ├── DetalleVentaDTO
 ├── PagoRequestDTO
 ├── PagoResponseDTO
 ├── ProductoDTO
 ├── ProductoMasVendidoDTO
 ├── PromocionDTO
 ├── SucursalDTO
 ├── TicketDetalleDTO
 ├── TicketDTO
 └── VentaDTO

exceptions/
 └── NotFoundException

mapper/
 └── Mapper

model/
 ├── DetalleTicket
 ├── DetalleVentas
 ├── EstadoPago
 ├── EstadoVenta
 ├── MetodoPago
 ├── Pago
 ├── Producto
 ├── Promocion
 ├── Sucursal
 ├── Ticket
 ├── TipoPromocion
 └── Venta

repository/
 ├── PagoRepository
 ├── ProductoRepository
 ├── VentaRepository
 ├── PromocionRepository
 ├── SucursalRepository
 └── TicketRepository

service/
 ├── PagoService
 ├── IPagoService
 ├── ProductoService
 ├── IProductoService
 ├── PromocionService
 ├── IPromocionService
 ├── SucursalService
 ├── ISucursalService
 ├── TicketService
 ├── ITicketService
 ├── VentaService
 └── IVentaService
```

---

# ⚙️ Variables de Entorno

El proyecto utiliza variables de entorno para la conexión a base de datos:

```env
DB_URL=your_url
DB_USER_NAME=your_username
DB_PASSWORD=your_password
```

---

# 🐳 Ejecución con Docker

El proyecto cuenta con:

- `Dockerfile`
- `docker-compose.yml`

para levantar automáticamente la API junto con MySQL.

## Ejecutar el proyecto

```bash
docker compose up --build
```

---

# ▶️ Ejecución con Maven

## Requisitos

- Java 17
- Maven
- MySQL

## Clonar el repositorio

```bash
git clone https://github.com/santiagozerda/SistemaRetail.git
```

## Ingresar al proyecto

```bash
cd sistema-retail-ventas
```

## Compilar el proyecto

```bash
mvn clean install
```

## Ejecutar la aplicación

```bash
mvn spring-boot:run
```

La aplicación se ejecutará en:

```text
http://localhost:8080
```

---

# 🧾 Flujo Principal del Sistema

```text
Producto → Promoción → Venta → Pago → Ticket
```

---

## 🛍️ Creación de Venta

El sistema recibe una solicitud de venta con los productos y cantidades seleccionadas.

---

## 🧮 Procesamiento Automático de Promociones

La aplicación calcula automáticamente descuentos y promociones activas sobre cada producto.

---

## 💳 Procesamiento de Pago

El sistema valida el método de pago y actualiza el estado de la venta.

---

## 🎟️ Generación Automática de Ticket

Una vez aprobada la venta, se genera automáticamente un ticket con el detalle completo de la operación.

---

# 📌 Endpoints Principales

## Productos
- CRUD de productos
- Asignación de promociones

## Ventas
- Generación de ventas
- Consulta de detalle de ventas

## Pagos
- Procesamiento de pagos
- Validación de estados

## Tickets
- Generación automática de comprobantes

---

# 📚 Base de Datos

La aplicación está preparada para trabajar con:

- MySQL
- H2 Database (entorno temporal/testing)

## Entidades principales

- Venta
- Ticket
- Producto
- Promoción
- Pago
- Sucursal

---

# 🔥 Objetivos del Proyecto

Este proyecto fue desarrollado con el objetivo de simular un sistema retail real, implementando lógica de negocio relacionada con:

- promociones comerciales
- procesamiento transaccional
- estados de venta
- generación de tickets
- cálculo automático de descuentos

---

# 👨‍💻 Autor

Desarrollado por Santiago Zerda.
