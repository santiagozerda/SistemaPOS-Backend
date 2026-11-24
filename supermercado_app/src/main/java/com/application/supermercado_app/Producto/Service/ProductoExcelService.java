package com.application.supermercado_app.Producto.Service;

import com.application.supermercado_app.Exception.ArchivoExcelException;
import com.application.supermercado_app.Exception.ImportacionException;
import com.application.supermercado_app.Exception.ImportacionExpiradaException;
import com.application.supermercado_app.Exception.SolicitudImportacionException;
import com.application.supermercado_app.Producto.DTO.Excel.AnalisisImportacionDTO;
import com.application.supermercado_app.Producto.DTO.Excel.ConfirmarImportacionDTO;
import com.application.supermercado_app.Producto.DTO.Excel.DecisionFilaImportacionDTO;
import com.application.supermercado_app.Producto.DTO.Excel.DetalleResultadoImportacionDTO;
import com.application.supermercado_app.Producto.DTO.Excel.ErrorFilaExcelDTO;
import com.application.supermercado_app.Producto.DTO.Excel.ProductoCoincidenciaDTO;
import com.application.supermercado_app.Producto.DTO.Excel.ProductoImportacionDTO;
import com.application.supermercado_app.Producto.DTO.Excel.ResultadoImportacionDTO;
import com.application.supermercado_app.Producto.DTO.Excel.ResumenImportacionDTO;
import com.application.supermercado_app.Producto.DTO.ProductoDTO;
import com.application.supermercado_app.Producto.DTO.ResultadoActualizacionProductoDTO;
import com.application.supermercado_app.Producto.Importacion.Model.AccionImportacion;
import com.application.supermercado_app.Producto.Importacion.Model.EstadoFilaImportacion;
import com.application.supermercado_app.Producto.Importacion.Model.EstadoLoteImportacion;
import com.application.supermercado_app.Producto.Model.DetalleImportacionProducto;
import com.application.supermercado_app.Producto.Model.ErrorImportacionProducto;
import com.application.supermercado_app.Producto.Model.LoteImportacionProducto;
import com.application.supermercado_app.Producto.Model.Producto;
import com.application.supermercado_app.Producto.Model.ProductoCoincidencia;
import com.application.supermercado_app.Producto.Repository.DetalleImportacionProductoRepository;
import com.application.supermercado_app.Producto.Repository.LoteImportacionProductoRepository;
import com.application.supermercado_app.Producto.Repository.ProductoRepository;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import jakarta.transaction.Transactional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductoExcelService implements IProductoExcelService {

    private final ProductoRepository productoRepository;

    private final LoteImportacionProductoRepository loteRepository;

    private final DetalleImportacionProductoRepository detalleRepository;

    private final IProductoService productoService;

    private static final List<String> ENCABEZADOS = List.of(
            "codigoBarra",
            "nombre",
            "precio",
            "stockAIngresar",
            "categoria"
    );

    private static final int LARGO_MINIMO_CODIGO = 6;
    private static final int LARGO_MAXIMO_CODIGO = 100;
    private static final int TAMANO_BLOQUE_CONSULTA = 500;

    @Value("${app.importacion.excel.expiracion-minutos:30}")
    private long expiracionMinutos;

    @Value("${app.importacion.excel.max-filas:10000}")
    private int maxFilas;

    @Value("${app.importacion.excel.max-bytes:10485760}")
    private long maxBytes;

    // =====================================================
    // 1. ANALIZAR ARCHIVO EXCEL
    // =====================================================
    @Override
    @Transactional
    public AnalisisImportacionDTO analizarArchivo(MultipartFile archivo) {

        validarArchivo(archivo);

        List<FilaLeida> filas = leerArchivo(archivo);

        if (filas.isEmpty()) {
            throw new ArchivoExcelException(
                    "El archivo no contiene productos para analizar."
            );
        }

        // Detectar todos los códigos de barras repetidos.
        detectarCodigosDuplicados(filas);

        // Clasificar productos y detectar coincidencias.
        clasificarFilas(filas);

        LocalDateTime ahora = LocalDateTime.now();

        LoteImportacionProducto lote
                = LoteImportacionProducto.builder()
                        .loteUuid(UUID.randomUUID())
                        .nombreArchivo(obtenerNombreArchivo(archivo))
                        .estado(EstadoLoteImportacion.ANALIZADA)
                        .fechaAnalisis(ahora)
                        .fechaExpiracion(
                                ahora.plusMinutes(expiracionMinutos)
                        )
                        .totalFilas(filas.size())
                        .build();

        List<ProductoImportacionDTO> respuestas = new ArrayList<>();

        for (FilaLeida fila : filas) {

            DetalleImportacionProducto detalle
                    = crearDetalleEntidad(fila);

            lote.agregarFila(detalle);

            respuestas.add(convertirRespuesta(fila));
        }

        ResumenImportacionDTO resumen
                = generarResumen(respuestas);

        // La relación CascadeType.ALL persiste detalles,
        // errores y snapshots junto con el lote.
        loteRepository.saveAndFlush(lote);

        return new AnalisisImportacionDTO(
                lote.getLoteUuid(),
                lote.getNombreArchivo(),
                lote.getFechaAnalisis(),
                lote.getFechaExpiracion(),
                lote.getEstado(),
                resumen,
                respuestas
        );
    }

    // =====================================================
    // 2. VALIDACIÓN DEL ARCHIVO
    // =====================================================
    private void validarArchivo(MultipartFile archivo) {

        if (archivo == null || archivo.isEmpty()) {
            throw new ArchivoExcelException(
                    "Debe seleccionar un archivo Excel."
            );
        }

        if (archivo.getSize() > maxBytes) {
            throw new ArchivoExcelException(
                    "El archivo supera el tamaño máximo permitido."
            );
        }

        String nombre = archivo.getOriginalFilename();

        if (nombre == null
                || !nombre.toLowerCase(Locale.ROOT).endsWith(".xlsx")) {

            throw new ArchivoExcelException(
                    "El archivo debe tener extensión .xlsx."
            );
        }
    }

    // =====================================================
    // 3. LECTURA DEL ARCHIVO
    // =====================================================
    private List<FilaLeida> leerArchivo(MultipartFile archivo) {

        List<FilaLeida> filas = new ArrayList<>();

        try (
                InputStream input = archivo.getInputStream(); XSSFWorkbook workbook = new XSSFWorkbook(input)) {

            if (workbook.getNumberOfSheets() == 0) {
                throw new ArchivoExcelException(
                        "El archivo no contiene hojas."
                );
            }

            Sheet sheet = workbook.getSheetAt(0);

            DataFormatter formatter = new DataFormatter(
                    Locale.forLanguageTag("es-AR")
            );

            validarEncabezados(sheet, formatter);

            if (sheet.getLastRowNum() > maxFilas) {
                throw new ArchivoExcelException(
                        "El archivo supera el máximo de "
                        + maxFilas + " filas permitidas."
                );
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (filaVacia(row)) {
                    continue;
                }

                FilaLeida fila = leerFila(
                        row,
                        i + 1,
                        formatter
                );

                filas.add(fila);
            }

        } catch (ArchivoExcelException ex) {

            throw ex;

        } catch (IOException | RuntimeException ex) {

            throw new ArchivoExcelException(
                    "No se pudo procesar el archivo Excel. "
                    + "Verifique que sea un XLSX válido y no esté dañado.",
                    ex
            );
        }

        return filas;
    }

    // =====================================================
    // 4. VALIDAR ENCABEZADOS
    // =====================================================
    private void validarEncabezados(
            Sheet sheet,
            DataFormatter formatter) {

        Row encabezado = sheet.getRow(0);

        if (encabezado == null) {
            throw new ArchivoExcelException(
                    "No se encontraron los encabezados del archivo."
            );
        }

        for (int i = 0; i < ENCABEZADOS.size(); i++) {

            Cell cell = encabezado.getCell(
                    i,
                    Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
            );

            String esperado = ENCABEZADOS.get(i);

            if (cell == null || cell.getCellType() != CellType.STRING) {

                throw new ArchivoExcelException(
                        "Falta el encabezado: " + esperado
                );
            }

            String recibido = cell.getStringCellValue().trim();

            if (!esperado.equals(recibido)) {

                throw new ArchivoExcelException(
                        "Se esperaba el encabezado '"
                        + esperado
                        + "' en la columna "
                        + (i + 1)
                        + "."
                );
            }
        }

        // No permitir columnas adicionales con contenido.
        for (int i = ENCABEZADOS.size();
                i < encabezado.getLastCellNum();
                i++) {

            Cell cell = encabezado.getCell(
                    i,
                    Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
            );

            if (cell != null
                    && cell.getCellType() != CellType.BLANK
                    && !formatter.formatCellValue(cell).isBlank()) {

                throw new ArchivoExcelException(
                        "El archivo contiene columnas adicionales."
                );
            }
        }
    }

    // =====================================================
    // 5. DETECTAR FILAS VACÍAS
    // =====================================================
    private boolean filaVacia(Row row) {

        if (row == null || row.getLastCellNum() < 0) {
            return true;
        }

        for (int i = 0; i < row.getLastCellNum(); i++) {

            Cell cell = row.getCell(
                    i,
                    Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
            );

            if (cell == null || cell.getCellType() == CellType.BLANK) {
                continue;
            }

            if (cell.getCellType() == CellType.STRING
                    && cell.getStringCellValue().isBlank()) {
                continue;
            }

            return false;
        }

        return true;
    }

    // =====================================================
    // 6. LEER UNA FILA
    // =====================================================
    private FilaLeida leerFila(
            Row row,
            int numeroFila,
            DataFormatter formatter) {

        List<ErrorFilaExcelDTO> errores = new ArrayList<>();

        String codigoBarra = leerTexto(
                row, 0, numeroFila, "codigoBarra", errores
        );

        String nombre = leerTexto(
                row, 1, numeroFila, "nombre", errores
        );

        BigDecimal precio = leerPrecio(
                row, 2, numeroFila, errores, formatter
        );

        Integer stock = leerStock(
                row, 3, numeroFila, errores, formatter
        );

        String categoria = leerTexto(
                row, 4, numeroFila, "categoria", errores
        );

        validarLongitudes(
                numeroFila,
                codigoBarra,
                nombre,
                categoria,
                errores
        );

        // Verificar que no existan datos en columnas adicionales.
        for (int i = ENCABEZADOS.size();
                i < row.getLastCellNum();
                i++) {

            Cell cell = row.getCell(
                    i,
                    Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
            );

            if (cell != null
                    && cell.getCellType() != CellType.BLANK
                    && !formatter.formatCellValue(cell).isBlank()) {

                agregarError(
                        errores,
                        numeroFila,
                        "estructura",
                        "COLUMNA_ADICIONAL",
                        formatter.formatCellValue(cell),
                        "La fila contiene datos fuera de las cinco columnas permitidas."
                );

                break;
            }
        }

        return new FilaLeida(
                numeroFila,
                codigoBarra,
                nombre,
                precio,
                stock,
                categoria,
                errores
        );
    }

    // =====================================================
    // 7. LEER TEXTO
    // =====================================================
    private String leerTexto(
            Row row,
            int indice,
            int numeroFila,
            String columna,
            List<ErrorFilaExcelDTO> errores) {

        Cell cell = row.getCell(
                indice,
                Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
        );

        if (cell == null || cell.getCellType() == CellType.BLANK) {

            agregarError(
                    errores,
                    numeroFila,
                    columna,
                    "CAMPO_OBLIGATORIO",
                    "",
                    "El campo es obligatorio."
            );

            return null;
        }

        if (cell.getCellType() != CellType.STRING) {

            agregarError(
                    errores,
                    numeroFila,
                    columna,
                    "TIPO_DATO_INVALIDO",
                    cell.toString(),
                    "El campo debe estar almacenado como texto."
            );

            return null;
        }

        String valor = cell.getStringCellValue().trim();

        if (valor.isBlank()) {

            agregarError(
                    errores,
                    numeroFila,
                    columna,
                    "CAMPO_OBLIGATORIO",
                    valor,
                    "El campo no puede estar vacío."
            );

            return null;
        }

        return valor;
    }

    // =====================================================
    // 8. LEER PRECIO
    // =====================================================
    private BigDecimal leerPrecio(
            Row row,
            int indice,
            int numeroFila,
            List<ErrorFilaExcelDTO> errores,
            DataFormatter formatter) {

        Cell cell = row.getCell(
                indice,
                Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
        );

        if (cell == null || cell.getCellType() == CellType.BLANK) {

            agregarError(
                    errores,
                    numeroFila,
                    "precio",
                    "PRECIO_OBLIGATORIO",
                    "",
                    "Debe indicar el precio del producto."
            );

            return null;
        }

        try {

            BigDecimal precio;

            if (cell.getCellType() == CellType.NUMERIC) {

                if (DateUtil.isCellDateFormatted(cell)) {
                    throw new NumberFormatException();
                }

                double valor = cell.getNumericCellValue();

                if (!Double.isFinite(valor)) {
                    throw new NumberFormatException();
                }

                precio = BigDecimal.valueOf(valor);

            } else if (cell.getCellType() == CellType.STRING) {

                String valor = cell.getStringCellValue().trim();

                precio = new BigDecimal(
                        valor.replace(',', '.')
                );

            } else {

                throw new NumberFormatException();
            }

            if (precio.signum() < 0) {
                throw new IllegalArgumentException(
                        "El precio no puede ser negativo."
                );
            }

            precio = precio.setScale(
                    2,
                    RoundingMode.UNNECESSARY
            );

            if (precio.precision() > 15) {
                throw new IllegalArgumentException(
                        "El precio supera la precisión permitida."
                );
            }

            return precio;

        } catch (NumberFormatException ex) {

            agregarError(
                    errores,
                    numeroFila,
                    "precio",
                    "PRECIO_INVALIDO",
                    formatter.formatCellValue(cell),
                    "El precio debe ser un número decimal válido."
            );

        } catch (ArithmeticException | IllegalArgumentException ex) {

            agregarError(
                    errores,
                    numeroFila,
                    "precio",
                    "PRECIO_INVALIDO",
                    formatter.formatCellValue(cell),
                    ex.getMessage()
            );
        }

        return null;
    }

    // =====================================================
    // 9. LEER STOCK
    // =====================================================
    private Integer leerStock(
            Row row,
            int indice,
            int numeroFila,
            List<ErrorFilaExcelDTO> errores,
            DataFormatter formatter) {

        Cell cell = row.getCell(
                indice,
                Row.MissingCellPolicy.RETURN_BLANK_AS_NULL
        );

        if (cell == null || cell.getCellType() == CellType.BLANK) {

            agregarError(
                    errores,
                    numeroFila,
                    "stockAIngresar",
                    "STOCK_OBLIGATORIO",
                    "",
                    "Debe indicar las unidades que desea ingresar."
            );

            return null;
        }

        try {

            BigDecimal stock;

            if (cell.getCellType() == CellType.NUMERIC) {

                if (DateUtil.isCellDateFormatted(cell)) {
                    throw new NumberFormatException();
                }

                double valor = cell.getNumericCellValue();

                if (!Double.isFinite(valor)) {
                    throw new NumberFormatException();
                }

                stock = BigDecimal.valueOf(valor);

            } else if (cell.getCellType() == CellType.STRING) {

                stock = new BigDecimal(
                        cell.getStringCellValue().trim()
                );

            } else {

                throw new NumberFormatException();
            }

            int cantidad = stock.intValueExact();

            if (cantidad <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad ingresada debe ser mayor a cero."
                );
            }

            return cantidad;

        } catch (NumberFormatException | ArithmeticException ex) {

            agregarError(
                    errores,
                    numeroFila,
                    "stockAIngresar",
                    "STOCK_INVALIDO",
                    formatter.formatCellValue(cell),
                    "El stock debe ser un número entero válido."
            );

        } catch (IllegalArgumentException ex) {

            agregarError(
                    errores,
                    numeroFila,
                    "stockAIngresar",
                    "STOCK_INVALIDO",
                    formatter.formatCellValue(cell),
                    ex.getMessage()
            );
        }

        return null;
    }

    // =====================================================
    // 10. VALIDAR LONGITUDES
    // =====================================================
    private void validarLongitudes(
            int numeroFila,
            String codigo,
            String nombre,
            String categoria,
            List<ErrorFilaExcelDTO> errores) {

        if (codigo != null
                && (codigo.length() < LARGO_MINIMO_CODIGO
                || codigo.length() > LARGO_MAXIMO_CODIGO)) {

            agregarError(
                    errores,
                    numeroFila,
                    "codigoBarra",
                    "CODIGO_BARRA_INVALIDO",
                    codigo,
                    "El código de barras debe tener entre 6 y 100 caracteres."
            );
        }

        if (nombre != null && nombre.length() > 255) {

            agregarError(
                    errores,
                    numeroFila,
                    "nombre",
                    "NOMBRE_DEMASIADO_LARGO",
                    nombre,
                    "El nombre supera los 255 caracteres permitidos."
            );
        }

        if (categoria != null && categoria.length() > 255) {

            agregarError(
                    errores,
                    numeroFila,
                    "categoria",
                    "CATEGORIA_DEMASIADO_LARGA",
                    categoria,
                    "La categoría supera los 255 caracteres permitidos."
            );
        }
    }

    // =====================================================
    // 11. DETECTAR CÓDIGOS DE BARRAS DUPLICADOS
    // =====================================================
    private void detectarCodigosDuplicados(List<FilaLeida> filas) {

        Map<String, List<FilaLeida>> grupos = new HashMap<>();

        for (FilaLeida fila : filas) {

            if (fila.codigoBarra() == null) {
                continue;
            }

            grupos.computeIfAbsent(
                    fila.codigoBarra(),
                    key -> new ArrayList<>()
            ).add(fila);
        }

        for (Map.Entry<String, List<FilaLeida>> entry
                : grupos.entrySet()) {

            List<FilaLeida> repetidas = entry.getValue();

            if (repetidas.size() <= 1) {
                continue;
            }

            String numeros = repetidas.stream()
                    .map(f -> String.valueOf(f.numeroFila()))
                    .collect(Collectors.joining(", "));

            for (FilaLeida fila : repetidas) {

                agregarError(
                        fila.errores(),
                        fila.numeroFila(),
                        "codigoBarra",
                        "CODIGO_BARRA_DUPLICADO_EN_PLANILLA",
                        entry.getKey(),
                        "El código aparece repetido en las filas: "
                        + numeros
                        + "."
                );
            }
        }
    }

    // =====================================================
    // 12. CLASIFICAR TODAS LAS FILAS
    // =====================================================
    private void clasificarFilas(List<FilaLeida> filas) {

        Set<String> codigos = new LinkedHashSet<>();
        Set<String> nombres = new LinkedHashSet<>();

        for (FilaLeida fila : filas) {

            if (!fila.errores().isEmpty()) {
                fila.estado = EstadoFilaImportacion.ERROR;
                continue;
            }

            codigos.add(fila.codigoBarra());
            nombres.add(normalizarNombre(fila.nombre()));
        }

        Map<String, Producto> porCodigo
                = consultarEnBloques(
                        codigos,
                        productoRepository::findByCodigoBarraIn
                ).stream().collect(Collectors.toMap(
                        Producto::getCodigoBarra,
                        Function.identity(),
                        (a, b) -> a
                ));

        Map<String, List<Producto>> porNombre = new HashMap<>();

        consultarEnBloques(
                nombres,
                productoRepository::findByNombresNormalizados
        ).forEach(producto
                -> porNombre.computeIfAbsent(
                        normalizarNombre(producto.getNombre()),
                        key -> new ArrayList<>()
                ).add(producto)
        );

        // Ordenar candidatos para devolver resultados estables.
        porNombre.values().forEach(
                lista -> lista.sort(
                        Comparator.comparing(Producto::getIdProductos)
                )
        );

        for (FilaLeida fila : filas) {

            if (!fila.errores().isEmpty()) {
                continue;
            }

            Producto encontrado = porCodigo.get(fila.codigoBarra());

            if (encontrado != null) {

                fila.productoEncontrado = encontrado;

                if (normalizarNombre(encontrado.getNombre())
                        .equals(normalizarNombre(fila.nombre()))) {

                    fila.estado
                            = EstadoFilaImportacion.COINCIDENCIA_EXACTA;

                } else {

                    fila.estado
                            = EstadoFilaImportacion.CONFLICTO_CODIGO_BARRAS;

                    agregarError(
                            fila.errores(),
                            fila.numeroFila(),
                            "codigoBarra",
                            "CODIGO_BARRAS_CON_NOMBRE_DIFERENTE",
                            fila.codigoBarra(),
                            "El código de barras pertenece al producto: "
                            + encontrado.getNombre()
                    );
                }

                continue;
            }

            List<Producto> coincidencias
                    = porNombre.getOrDefault(
                            normalizarNombre(fila.nombre()),
                            List.of()
                    );

            if (!coincidencias.isEmpty()) {

                fila.coincidencias = coincidencias;

                fila.estado
                        = EstadoFilaImportacion.COINCIDENCIA_POR_NOMBRE;

            } else {

                fila.estado = EstadoFilaImportacion.NUEVO;
            }
        }

        // Validar códigos cortos únicamente para productos nuevos.
        validarCodigosCortosDeNuevos(filas);
    }

    // =====================================================
    // 13. VALIDAR CÓDIGOS CORTOS
    // =====================================================
    private void validarCodigosCortosDeNuevos(
            List<FilaLeida> filas) {

        Set<String> codigosCortos = filas.stream()
                .filter(f -> f.estado == EstadoFilaImportacion.NUEVO)
                .map(f -> extraerCodigoCorto(f.codigoBarra()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<String, Producto> existentes = new HashMap<>();

        consultarEnBloques(
                codigosCortos,
                productoRepository::findByCodigoBarraIn
        ).forEach(producto
                -> existentes.put(producto.getCodigoCorto(), producto)
        );

        Map<String, List<FilaLeida>> nuevosPorCodigoCorto
                = new HashMap<>();

        for (FilaLeida fila : filas) {

            if (fila.estado != EstadoFilaImportacion.NUEVO) {
                continue;
            }

            String codigoCorto = extraerCodigoCorto(
                    fila.codigoBarra()
            );

            Producto existente = existentes.get(codigoCorto);

            if (existente != null) {

                marcarErrorCodigoCorto(
                        fila,
                        codigoCorto,
                        "El código corto ya pertenece al producto: "
                        + existente.getNombre()
                );

                continue;
            }

            nuevosPorCodigoCorto
                    .computeIfAbsent(
                            codigoCorto,
                            key -> new ArrayList<>()
                    )
                    .add(fila);
        }

        // Detectar colisiones entre productos nuevos del mismo Excel.
        for (Map.Entry<String, List<FilaLeida>> entry
                : nuevosPorCodigoCorto.entrySet()) {

            if (entry.getValue().size() <= 1) {
                continue;
            }

            for (FilaLeida fila : entry.getValue()) {

                marcarErrorCodigoCorto(
                        fila,
                        entry.getKey(),
                        "El código corto se repite entre productos "
                        + "de la misma planilla."
                );
            }
        }
    }

    private void marcarErrorCodigoCorto(
            FilaLeida fila,
            String codigoCorto,
            String mensaje) {

        fila.estado = EstadoFilaImportacion.ERROR;

        agregarError(
                fila.errores(),
                fila.numeroFila(),
                "codigoBarra",
                "CODIGO_CORTO_DUPLICADO",
                codigoCorto,
                mensaje
        );
    }

    private String extraerCodigoCorto(String codigoBarra) {

        return codigoBarra.substring(
                codigoBarra.length() - LARGO_MINIMO_CODIGO
        );
    }

    // =====================================================
    // 14. CONSULTAS POR BLOQUES
    // =====================================================
    private <T> List<T> consultarEnBloques(
            Collection<String> valores,
            Function<Collection<String>, List<T>> consulta) {

        if (valores.isEmpty()) {
            return List.of();
        }

        List<String> elementos = new ArrayList<>(valores);

        List<T> resultados = new ArrayList<>();

        for (int i = 0;
                i < elementos.size();
                i += TAMANO_BLOQUE_CONSULTA) {

            int fin = Math.min(
                    i + TAMANO_BLOQUE_CONSULTA,
                    elementos.size()
            );

            resultados.addAll(
                    consulta.apply(elementos.subList(i, fin))
            );
        }

        return resultados;
    }

    // =====================================================
    // 15. CREAR ENTIDAD DETALLE
    // =====================================================
    private DetalleImportacionProducto crearDetalleEntidad(
            FilaLeida fila) {

        Producto detectado = fila.productoEncontrado;

        List<Long> idsCoincidentes = fila.coincidencias.stream()
                .map(Producto::getIdProductos)
                .toList();

        List<ProductoCoincidencia> snapshots
                = fila.coincidencias.stream()
                        .map(this::crearSnapshot)
                        .toList();

        DetalleImportacionProducto detalle
                = DetalleImportacionProducto.builder()
                        .numeroFila(fila.numeroFila())
                        .codigoBarra(fila.codigoBarra())
                        .nombre(fila.nombre())
                        .precioExcel(fila.precio())
                        .stockAIngresar(fila.stockAIngresar())
                        .categoria(fila.categoria())
                        .estadoFila(fila.estado)
                        .idProductoDetectado(
                                detectado != null
                                        ? detectado.getIdProductos()
                                        : null
                        )
                        .codigoBarraDetectado(
                                detectado != null
                                        ? detectado.getCodigoBarra()
                                        : null
                        )
                        .nombreDetectado(
                                detectado != null
                                        ? detectado.getNombre()
                                        : null
                        )
                        .precioActualDetectado(
                                detectado == null
                                        ? null
                                        : convertirPrecioABigDecimal(
                                                detectado.getPrecio()
                                        )
                        )
                        .stockActualDetectado(
                                detectado != null
                                        ? detectado.getCantidad()
                                        : null
                        )
                        .idsProductosCoincidentes(
                                new ArrayList<>(idsCoincidentes)
                        )
                        .coincidenciasDetectadas(
                                new ArrayList<>(snapshots)
                        )
                        .build();

        for (ErrorFilaExcelDTO error : fila.errores()) {

            ErrorImportacionProducto entidad
                    = ErrorImportacionProducto.builder()
                            .columna(error.columna())
                            .codigo(error.codigo())
                            .valor(error.valor())
                            .mensaje(error.mensaje())
                            .build();

            detalle.agregarError(entidad);
        }

        return detalle;
    }

    private ProductoCoincidencia crearSnapshot(
            Producto producto) {

        return ProductoCoincidencia.builder()
                .idProducto(producto.getIdProductos())
                .codigoBarra(producto.getCodigoBarra())
                .nombre(producto.getNombre())
                .categoria(producto.getCategoria())
                .precioActual(
                        convertirPrecioABigDecimal(producto.getPrecio())
                )
                .stockActual(producto.getCantidad())
                .build();
    }

    // =====================================================
    // 16. CONVERTIR A DTO DE RESPUESTA
    // =====================================================
    private ProductoImportacionDTO convertirRespuesta(
            FilaLeida fila) {

        ProductoCoincidenciaDTO encontrado = null;

        if (fila.productoEncontrado != null) {

            encontrado = convertirCoincidencia(
                    fila.productoEncontrado,
                    fila.stockAIngresar()
            );
        }

        List<ProductoCoincidenciaDTO> coincidencias
                = fila.coincidencias.stream()
                        .map(producto
                                -> convertirCoincidencia(
                                        producto,
                                        fila.stockAIngresar()
                                )
                        )
                        .toList();

        return new ProductoImportacionDTO(
                fila.numeroFila(),
                fila.codigoBarra(),
                fila.nombre(),
                fila.precio(),
                fila.stockAIngresar(),
                fila.categoria(),
                fila.estado,
                encontrado,
                coincidencias,
                List.copyOf(fila.errores())
        );
    }

    private ProductoCoincidenciaDTO convertirCoincidencia(
            Producto producto,
            Integer stockIngresado) {

        BigDecimal precio = convertirPrecioABigDecimal(
                producto.getPrecio()
        );

        Long stockProyectado = stockIngresado == null
                ? null
                : (long) producto.getCantidad() + stockIngresado;

        return new ProductoCoincidenciaDTO(
                producto.getIdProductos(),
                producto.getCodigoBarra(),
                producto.getCodigoCorto(),
                producto.getNombre(),
                producto.getCategoria(),
                precio,
                producto.getCantidad(),
                stockProyectado
        );
    }

    // =====================================================
    // 17. GENERAR RESUMEN
    // =====================================================
    private ResumenImportacionDTO generarResumen(
            List<ProductoImportacionDTO> filas) {

        int nuevos = 0;
        int exactas = 0;
        int porNombre = 0;
        int conflictos = 0;
        int errores = 0;

        for (ProductoImportacionDTO fila : filas) {

            switch (fila.estado()) {

                case NUEVO ->
                    nuevos++;

                case COINCIDENCIA_EXACTA ->
                    exactas++;

                case COINCIDENCIA_POR_NOMBRE ->
                    porNombre++;

                case CONFLICTO_CODIGO_BARRAS ->
                    conflictos++;

                case ERROR ->
                    errores++;
            }
        }

        return new ResumenImportacionDTO(
                filas.size(),
                nuevos,
                exactas,
                porNombre,
                conflictos,
                errores
        );
    }

    // =====================================================
    // 18. CONFIRMAR IMPORTACIÓN
    // =====================================================
    @Override
    @Transactional(noRollbackFor = ImportacionExpiradaException.class)
    public ResultadoImportacionDTO confirmarImportacion(
            ConfirmarImportacionDTO confirmacion) {

        if (confirmacion == null || confirmacion.loteId() == null) {

            throw new SolicitudImportacionException(
                    "Debe indicar el lote de importación."
            );
        }

        // Bloquear el lote hasta terminar la transacción.
        LoteImportacionProducto lote
                = loteRepository.findByLoteUuidForUpdate(
                        confirmacion.loteId()
                ).orElseThrow(()
                        -> new ImportacionException(
                                "No se encontró el lote de importación."
                        )
                );

        if (lote.getEstado() != EstadoLoteImportacion.ANALIZADA) {

            throw new ImportacionException(
                    "El lote ya fue procesado o no está disponible."
            );
        }

        LocalDateTime ahora = LocalDateTime.now();

        if (lote.getFechaExpiracion() == null
                || !ahora.isBefore(lote.getFechaExpiracion())) {

            lote.setEstado(EstadoLoteImportacion.EXPIRADA);

            loteRepository.saveAndFlush(lote);

            throw new ImportacionExpiradaException(
                    "El análisis ha expirado. Debe cargar nuevamente la planilla."
            );
        }

        List<DetalleImportacionProducto> detalles
                = detalleRepository.findAllByLoteIdWithErrores(
                        lote.getId()
                );

        Map<Integer, DecisionFilaImportacionDTO> decisiones
                = obtenerDecisiones(confirmacion);

        validarCoberturaDecisiones(detalles, decisiones);

        // Preparar y validar todas las decisiones antes
        // de realizar la primera modificación de productos.
        List<DecisionContext> operaciones = new ArrayList<>();

        List<DetalleResultadoImportacionDTO> resultados
                = new ArrayList<>();

        for (DetalleImportacionProducto detalle : detalles) {

            if (!esFilaAccionable(detalle.getEstadoFila())) {
                continue;
            }

            DecisionFilaImportacionDTO decision
                    = decisiones.get(detalle.getNumeroFila());

            ProductoCoincidencia snapshot
                    = validarDecision(detalle, decision);

            if (decision.accion() == AccionImportacion.OMITIR) {
                continue;
            }

            Long idDestino = null;

            if (decision.accion() == AccionImportacion.INCREMENTAR_STOCK) {

                idDestino = detalle.getEstadoFila()
                        == EstadoFilaImportacion.COINCIDENCIA_EXACTA
                                ? detalle.getIdProductoDetectado()
                                : decision.idProductoDestino();
            }

            operaciones.add(
                    new DecisionContext(
                            detalle,
                            decision,
                            idDestino,
                            snapshot
                    )
            );
        }

        // Orden determinista de bloqueo de productos.
        operaciones.sort(
                Comparator.comparing(
                        DecisionContext::idProductoDestino,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).thenComparing(
                        context -> context.detalle().getNumeroFila()
                )
        );

        int productosCreados = 0;
        int stocksIncrementados = 0;
        int preciosActualizados = 0;
        int filasOmitidas = 0;

        // Procesar todas las filas no accionables.
        for (DetalleImportacionProducto detalle : detalles) {

            if (!esFilaAccionable(detalle.getEstadoFila())) {

                detalle.setAccionAplicada(AccionImportacion.OMITIR);

                filasOmitidas++;

                resultados.add(crearResultadoOmitido(detalle));

                continue;
            }

            DecisionFilaImportacionDTO decision
                    = decisiones.get(detalle.getNumeroFila());

            if (decision.accion() == AccionImportacion.OMITIR) {

                detalle.setAccionAplicada(AccionImportacion.OMITIR);

                filasOmitidas++;

                resultados.add(crearResultadoOmitido(detalle));
            }
        }

        // Aplicar operaciones previamente validadas.
        for (DecisionContext context : operaciones) {

            DetalleImportacionProducto detalle = context.detalle();

            DecisionFilaImportacionDTO decision = context.decision();

            if (decision.accion() == AccionImportacion.CREAR) {

                ProductoDTO producto
                        = productoService.crearProductoDesdeImportacion(
                                detalle.getNombre(),
                                detalle.getCategoria(),
                                detalle.getPrecioExcel(),
                                detalle.getStockAIngresar(),
                                detalle.getCodigoBarra()
                        );

                productosCreados++;

                detalle.setAccionAplicada(AccionImportacion.CREAR);

                detalle.setIdProductoResultado(producto.getId());

                detalle.setStockAnterior(0);
                detalle.setUnidadesIngresadas(detalle.getStockAIngresar());
                detalle.setStockFinal(detalle.getStockAIngresar());

                detalle.setPrecioAnterior(null);
                detalle.setPrecioFinal(detalle.getPrecioExcel());
                detalle.setPrecioActualizado(false);

                resultados.add(
                        new DetalleResultadoImportacionDTO(
                                detalle.getNumeroFila(),
                                AccionImportacion.CREAR,
                                producto.getId(),
                                producto.getNombre(),
                                producto.getCodigoBarra(),
                                0,
                                detalle.getStockAIngresar(),
                                detalle.getStockAIngresar(),
                                null,
                                detalle.getPrecioExcel(),
                                false
                        )
                );

            } else {

                ProductoCoincidencia snapshot
                        = context.snapshot();

                ResultadoActualizacionProductoDTO resultado
                        = productoService.actualizarDesdeImportacion(
                                context.idProductoDestino(),
                                detalle.getNombre(),
                                detalle.getCodigoBarra(),
                                snapshot.getCodigoBarra(),
                                detalle.getEstadoFila()
                                == EstadoFilaImportacion.COINCIDENCIA_POR_NOMBRE,
                                detalle.getStockAIngresar(),
                                detalle.getPrecioExcel(),
                                snapshot.getPrecioActual(),
                                decision.actualizarPrecio()
                        );

                stocksIncrementados++;

                if (resultado.precioActualizado()) {
                    preciosActualizados++;
                }

                detalle.setAccionAplicada(
                        AccionImportacion.INCREMENTAR_STOCK
                );

                detalle.setIdProductoResultado(
                        resultado.idProducto()
                );

                detalle.setStockAnterior(
                        resultado.stockAnterior()
                );

                detalle.setUnidadesIngresadas(
                        resultado.unidadesIngresadas()
                );

                detalle.setStockFinal(
                        resultado.stockFinal()
                );

                detalle.setPrecioAnterior(
                        resultado.precioAnterior()
                );

                detalle.setPrecioFinal(
                        resultado.precioFinal()
                );

                detalle.setPrecioActualizado(
                        resultado.precioActualizado()
                );

                resultados.add(
                        new DetalleResultadoImportacionDTO(
                                detalle.getNumeroFila(),
                                AccionImportacion.INCREMENTAR_STOCK,
                                resultado.idProducto(),
                                resultado.nombreProducto(),
                                resultado.codigoBarra(),
                                resultado.stockAnterior(),
                                resultado.unidadesIngresadas(),
                                resultado.stockFinal(),
                                resultado.precioAnterior(),
                                resultado.precioFinal(),
                                resultado.precioActualizado()
                        )
                );
            }
        }

        resultados.sort(
                Comparator.comparing(
                        DetalleResultadoImportacionDTO::numeroFila
                )
        );

        lote.setProductosCreados(productosCreados);
        lote.setStocksIncrementados(stocksIncrementados);
        lote.setPreciosActualizados(preciosActualizados);
        lote.setFilasOmitidas(filasOmitidas);

        lote.setFechaConfirmacion(LocalDateTime.now());
        lote.setEstado(EstadoLoteImportacion.CONFIRMADA);

        loteRepository.saveAndFlush(lote);

        return new ResultadoImportacionDTO(
                lote.getLoteUuid(),
                lote.getEstado(),
                lote.getFechaConfirmacion(),
                productosCreados,
                stocksIncrementados,
                preciosActualizados,
                filasOmitidas,
                resultados
        );
    }

    // =====================================================
    // 19. RECUPERAR DECISIONES
    // =====================================================
    private Map<Integer, DecisionFilaImportacionDTO> obtenerDecisiones(
            ConfirmarImportacionDTO confirmacion) {

        Map<Integer, DecisionFilaImportacionDTO> resultado
                = new LinkedHashMap<>();

        if (confirmacion.decisiones() == null) {
            return resultado;
        }

        for (DecisionFilaImportacionDTO decision
                : confirmacion.decisiones()) {

            if (decision == null || decision.numeroFila() == null) {

                throw new SolicitudImportacionException(
                        "Se recibió una decisión sin número de fila."
                );
            }

            if (resultado.putIfAbsent(
                    decision.numeroFila(),
                    decision
            ) != null) {

                throw new SolicitudImportacionException(
                        "La fila "
                        + decision.numeroFila()
                        + " tiene decisiones duplicadas."
                );
            }
        }

        return resultado;
    }

    // =====================================================
    // 20. VALIDAR COBERTURA DE DECISIONES
    // =====================================================
    private void validarCoberturaDecisiones(
            List<DetalleImportacionProducto> detalles,
            Map<Integer, DecisionFilaImportacionDTO> decisiones) {

        Set<Integer> filasAccionables = detalles.stream()
                .filter(d -> esFilaAccionable(d.getEstadoFila()))
                .map(DetalleImportacionProducto::getNumeroFila)
                .collect(Collectors.toSet());

        if (!filasAccionables.equals(decisiones.keySet())) {

            throw new SolicitudImportacionException(
                    "Las decisiones no corresponden exactamente "
                    + "a las filas que pueden importarse."
            );
        }
    }

    // =====================================================
    // 21. IDENTIFICAR FILAS ACCIONABLES
    // =====================================================
    private boolean esFilaAccionable(EstadoFilaImportacion estado) {

        return estado == EstadoFilaImportacion.NUEVO
                || estado == EstadoFilaImportacion.COINCIDENCIA_EXACTA
                || estado == EstadoFilaImportacion.COINCIDENCIA_POR_NOMBRE;
    }

    // =====================================================
    // 22. VALIDAR DECISIÓN INDIVIDUAL
    // =====================================================
    private ProductoCoincidencia validarDecision(
            DetalleImportacionProducto detalle,
            DecisionFilaImportacionDTO decision) {

        if (decision == null || decision.accion() == null) {

            throw new SolicitudImportacionException(
                    "Debe seleccionar una acción para la fila "
                    + detalle.getNumeroFila()
            );
        }

        if (decision.accion() == AccionImportacion.OMITIR) {

            if (decision.idProductoDestino() != null
                    || decision.actualizarPrecio()) {

                throw new SolicitudImportacionException(
                        "Una fila omitida no puede modificar productos."
                );
            }

            return null;
        }

        switch (detalle.getEstadoFila()) {

            case NUEVO -> {

                if (decision.accion() != AccionImportacion.CREAR
                        || decision.idProductoDestino() != null
                        || decision.actualizarPrecio()) {

                    throw new SolicitudImportacionException(
                            "Un producto nuevo solamente puede crearse u omitirse."
                    );
                }

                return null;
            }

            case COINCIDENCIA_EXACTA -> {

                if (decision.accion()
                        != AccionImportacion.INCREMENTAR_STOCK
                        || decision.idProductoDestino() != null) {

                    throw new SolicitudImportacionException(
                            "La coincidencia exacta no admite otro producto destino."
                    );
                }

                if (detalle.getIdProductoDetectado() == null) {

                    throw new ImportacionException(
                            "No se encontró el producto detectado durante el análisis."
                    );
                }

                return crearSnapshotExacto(detalle);
            }

            case COINCIDENCIA_POR_NOMBRE -> {

                if (decision.accion()
                        != AccionImportacion.INCREMENTAR_STOCK) {

                    throw new SolicitudImportacionException(
                            "La coincidencia por nombre solamente permite "
                            + "incrementar stock u omitir."
                    );
                }

                if (decision.idProductoDestino() == null) {

                    throw new SolicitudImportacionException(
                            "Debe seleccionar el producto existente."
                    );
                }

                if (!detalle.getIdsProductosCoincidentes()
                        .contains(decision.idProductoDestino())) {

                    throw new SolicitudImportacionException(
                            "El producto seleccionado no pertenece "
                            + "a las coincidencias del análisis."
                    );
                }

                return detalle.getCoincidenciasDetectadas()
                        .stream()
                        .filter(s -> s.getIdProducto().equals(
                                decision.idProductoDestino()
                        ))
                        .findFirst()
                        .orElseThrow(()
                                -> new ImportacionException(
                                        "No se encontró la fotografía del producto seleccionado."
                                )
                        );
            }

            default ->
                throw new SolicitudImportacionException(
                        "La fila no admite operaciones de importación."
                );
        }
    }

    // =====================================================
    // 23. CREAR SNAPSHOT PARA COINCIDENCIA EXACTA
    // =====================================================
    private ProductoCoincidencia crearSnapshotExacto(
            DetalleImportacionProducto detalle) {

        return ProductoCoincidencia.builder()
                .idProducto(detalle.getIdProductoDetectado())
                .codigoBarra(detalle.getCodigoBarraDetectado())
                .nombre(detalle.getNombreDetectado())
                .precioActual(detalle.getPrecioActualDetectado())
                .stockActual(detalle.getStockActualDetectado())
                .build();
    }

    // =====================================================
    // 24. RESULTADO DE FILA OMITIDA
    // =====================================================
    private DetalleResultadoImportacionDTO crearResultadoOmitido(
            DetalleImportacionProducto detalle) {

        return new DetalleResultadoImportacionDTO(
                detalle.getNumeroFila(),
                AccionImportacion.OMITIR,
                null,
                detalle.getNombre(),
                detalle.getCodigoBarra(),
                null,
                null,
                null,
                null,
                null,
                false
        );
    }

    // =====================================================
    // 25. UTILIDADES GENERALES
    // =====================================================
    private String normalizarNombre(String nombre) {

        if (nombre == null) {
            return "";
        }

        return nombre.trim().toLowerCase(Locale.ROOT);
    }

    private BigDecimal convertirPrecioABigDecimal(Double precio) {

        if (precio == null) {
            return null;
        }

        if (!Double.isFinite(precio)) {
            throw new ImportacionException(
                    "El precio almacenado no es un valor válido."
            );
        }

        return BigDecimal.valueOf(precio);
    }

    private String obtenerNombreArchivo(MultipartFile archivo) {

        String nombre = archivo.getOriginalFilename();

        if (nombre == null || nombre.isBlank()) {
            return "productos.xlsx";
        }

        nombre = nombre.replace("\\", "/");

        int ultimaBarra = nombre.lastIndexOf('/');

        if (ultimaBarra >= 0) {
            nombre = nombre.substring(ultimaBarra + 1);
        }

        return nombre.substring(
                0,
                Math.min(nombre.length(), 255)
        );
    }

    private void agregarError(
            List<ErrorFilaExcelDTO> errores,
            Integer numeroFila,
            String columna,
            String codigo,
            String valor,
            String mensaje) {

        errores.add(new ErrorFilaExcelDTO(
                numeroFila,
                columna,
                codigo,
                valor,
                mensaje
        ));
    }

    // =====================================================
    // 26. ESTRUCTURAS AUXILIARES INTERNAS
    // =====================================================
    private static class FilaLeida {

        private final Integer numeroFila;
        private final String codigoBarra;
        private final String nombre;
        private final BigDecimal precio;
        private final Integer stockAIngresar;
        private final String categoria;
        private final List<ErrorFilaExcelDTO> errores;

        private EstadoFilaImportacion estado;
        private Producto productoEncontrado;
        private List<Producto> coincidencias = new ArrayList<>();

        private FilaLeida(
                Integer numeroFila,
                String codigoBarra,
                String nombre,
                BigDecimal precio,
                Integer stockAIngresar,
                String categoria,
                List<ErrorFilaExcelDTO> errores) {

            this.numeroFila = numeroFila;
            this.codigoBarra = codigoBarra;
            this.nombre = nombre;
            this.precio = precio;
            this.stockAIngresar = stockAIngresar;
            this.categoria = categoria;
            this.errores = errores;
        }

        public Integer numeroFila() {
            return numeroFila;
        }

        public String codigoBarra() {
            return codigoBarra;
        }

        public String nombre() {
            return nombre;
        }

        public BigDecimal precio() {
            return precio;
        }

        public Integer stockAIngresar() {
            return stockAIngresar;
        }

        public String categoria() {
            return categoria;
        }

        public List<ErrorFilaExcelDTO> errores() {
            return errores;
        }
    }

    private record DecisionContext(
            DetalleImportacionProducto detalle,
            DecisionFilaImportacionDTO decision,
            Long idProductoDestino,
            ProductoCoincidencia snapshot
            ) {}
}
