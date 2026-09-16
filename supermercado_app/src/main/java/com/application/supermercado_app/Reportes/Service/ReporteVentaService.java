package com.application.supermercado_app.Reportes.Service;

import com.application.supermercado_app.Reportes.DTO.ProductosMasVendidosDTO;
import com.application.supermercado_app.Reportes.DTO.ReporteResumenDTO;
import com.application.supermercado_app.Reportes.DTO.ReporteVentaDTO;
import com.application.supermercado_app.Reportes.DTO.SemanaDelMesDTO;
import com.application.supermercado_app.Reportes.DTO.VentaDiaDTO;
import com.application.supermercado_app.Reportes.DTO.VentaMesDTO;
import com.application.supermercado_app.Reportes.DTO.VentaSemanaDTO;
import com.application.supermercado_app.Reportes.Mapper.ReporteVentaMapper;
import com.application.supermercado_app.Venta.Model.EstadoVenta;
import com.application.supermercado_app.Venta.Model.Venta;
import com.application.supermercado_app.Venta.Repository.DetalleVentasRepository;
import com.application.supermercado_app.Venta.Repository.VentaRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReporteVentaService
        implements IReporteVentaService {

    private final VentaRepository ventaRepo;
    private final DetalleVentasRepository detalleVentasRepository;

    public ReporteVentaService(
            VentaRepository ventaRepo,
            DetalleVentasRepository detalleVentasRepository) {

        this.ventaRepo = ventaRepo;
        this.detalleVentasRepository
                = detalleVentasRepository;
    }

    @Override
    public List<ProductosMasVendidosDTO> productosMasVendidos() {

        Pageable topProductos
                = PageRequest.of(0, 10);

        return detalleVentasRepository
                .topProductosMasVendidos(
                        EstadoVenta.APROBADA,
                        topProductos
                );
    }

    @Override
    public ReporteVentaDTO<VentaDiaDTO> obtenerReporteDia() {

        LocalDate hoy = LocalDate.now();

        LocalDateTime inicio
                = hoy.atStartOfDay();

        LocalDateTime fin
                = hoy.atTime(LocalTime.MAX);

        ReporteResumenDTO resumen
                = obtenerResumen(inicio, fin);

        List<VentaDiaDTO> tabla
                = obtenerVentas(inicio, fin)
                        .stream()
                        .map(ReporteVentaMapper::toVentaDiaDTO)
                        .toList();

        return ReporteVentaDTO
                .<VentaDiaDTO>builder()
                .resumen(resumen)
                .tabla(tabla)
                .build();
    }

    @Override
    public ReporteVentaDTO<VentaSemanaDTO> obtenerReporteSemana(
            int anio,
            int mes,
            int semana) {

        RangoFechas rango
                = calcularRangoSemana(
                        anio,
                        mes,
                        semana
                );

        ReporteResumenDTO resumen
                = obtenerResumen(
                        rango.inicio(),
                        rango.fin()
                );

        List<Venta> ventas
                = obtenerVentas(
                        rango.inicio(),
                        rango.fin()
                );

        List<VentaSemanaDTO> tabla
                = agruparVentasPorDia(ventas);

        return ReporteVentaDTO
                .<VentaSemanaDTO>builder()
                .resumen(resumen)
                .tabla(tabla)
                .build();
    }

    @Override
    public ReporteVentaDTO<VentaMesDTO> obtenerReporteMes(
            int anio,
            int mes) {

        validarMes(mes);

        YearMonth yearMonth
                = YearMonth.of(anio, mes);

        LocalDateTime inicio
                = yearMonth
                        .atDay(1)
                        .atStartOfDay();

        LocalDateTime fin
                = yearMonth
                        .atEndOfMonth()
                        .atTime(LocalTime.MAX);

        ReporteResumenDTO resumen
                = obtenerResumen(
                        inicio,
                        fin
                );

        List<Venta> ventas
                = obtenerVentas(
                        inicio,
                        fin
                );

        List<VentaMesDTO> tabla
                = agruparVentasPorSemana(ventas);

        return ReporteVentaDTO
                .<VentaMesDTO>builder()
                .tabla(tabla)
                .resumen(resumen)
                .build();
    }

    /**
     * Todas las consultas de ventas del módulo Reportes trabajan exclusivamente
     * con ventas APROBADAS.
     */
    private List<Venta> obtenerVentas(
            LocalDateTime inicio,
            LocalDateTime fin) {

        return ventaRepo.findVentasParaReporte(
                EstadoVenta.APROBADA,
                inicio,
                fin
        );
    }

    private ReporteResumenDTO obtenerResumen(
            LocalDateTime inicio,
            LocalDateTime fin) {

        return ventaRepo.obtenerResumenGeneral(
                EstadoVenta.APROBADA,
                inicio,
                fin
        );
    }

    /**
     * Definición central de semana.
     *
     * Semana 1: desde el día 1 hasta el primer domingo.
     *
     * Semanas siguientes: lunes a domingo.
     */
    private RangoFechas calcularRangoSemana(
            int anio,
            int mes,
            int semana) {

        validarMes(mes);

        if (semana < 1) {

            throw new IllegalArgumentException(
                    "La semana debe ser mayor o igual a 1"
            );
        }

        YearMonth yearMonth
                = YearMonth.of(anio, mes);

        LocalDate primerDiaMes
                = yearMonth.atDay(1);

        LocalDate ultimoDiaMes
                = yearMonth.atEndOfMonth();

        LocalDate inicioSemana;

        if (semana == 1) {

            inicioSemana
                    = primerDiaMes;

        } else {

            LocalDate primerLunes
                    = primerDiaMes.with(
                            TemporalAdjusters.next(
                                    DayOfWeek.MONDAY
                            )
                    );

            inicioSemana
                    = primerLunes.plusWeeks(
                            semana - 2
                    );
        }

        if (inicioSemana.isAfter(
                ultimoDiaMes)) {

            throw new IllegalArgumentException(
                    "La semana seleccionada "
                    + "no existe para el mes seleccionado"
            );
        }

        LocalDate finSemana;

        if (semana == 1) {

            finSemana
                    = primerDiaMes.with(
                            TemporalAdjusters.nextOrSame(
                                    DayOfWeek.SUNDAY
                            )
                    );

        } else {

            finSemana
                    = inicioSemana.with(
                            TemporalAdjusters.nextOrSame(
                                    DayOfWeek.SUNDAY
                            )
                    );
        }

        if (finSemana.isAfter(
                ultimoDiaMes)) {

            finSemana
                    = ultimoDiaMes;
        }

        return new RangoFechas(
                inicioSemana.atStartOfDay(),
                finSemana.atTime(LocalTime.MAX)
        );
    }

    /**
     * Determina la semana de una fecha utilizando la misma definición que
     * calcularRangoSemana().
     *
     * Esto elimina la posibilidad de que el reporte mensual y el reporte
     * semanal utilicen reglas diferentes.
     */
    private int obtenerSemanaDelMes(
            LocalDate fecha) {

        YearMonth yearMonth
                = YearMonth.from(fecha);

        for (int semana = 1;; semana++) {

            RangoFechas rango;

            try {

                rango = calcularRangoSemana(
                        yearMonth.getYear(),
                        yearMonth.getMonthValue(),
                        semana
                );

            } catch (IllegalArgumentException ex) {

                throw new IllegalStateException(
                        "No se pudo determinar "
                        + "la semana de la fecha: "
                        + fecha,
                        ex
                );
            }

            LocalDate desde
                    = rango.inicio().toLocalDate();

            LocalDate hasta
                    = rango.fin().toLocalDate();

            if (!fecha.isBefore(desde)
                    && !fecha.isAfter(hasta)) {

                return semana;
            }
        }
    }

    private List<VentaSemanaDTO> agruparVentasPorDia(
            List<Venta> ventas) {

        Map<LocalDate, List<Venta>> ventasAgrupadas
                = ventas.stream()
                        .collect(
                                Collectors.groupingBy(
                                        venta
                                        -> venta.getFechaVenta()
                                                .toLocalDate(),
                                        TreeMap::new,
                                        Collectors.toList()
                                )
                        );

        return ventasAgrupadas.entrySet()
                .stream()
                .map(entry -> {

                    LocalDate dia
                            = entry.getKey();

                    List<Venta> ventasDia
                            = entry.getValue();

                    long cantidadVentas
                            = ventasDia.size();

                    double importeTotal
                            = ventasDia.stream()
                                    .mapToDouble(
                                            Venta::getTotal
                                    )
                                    .sum();

                    return ReporteVentaMapper
                            .toVentaSemanaDTO(
                                    dia,
                                    cantidadVentas,
                                    importeTotal
                            );
                })
                .toList();
    }

    private List<VentaMesDTO> agruparVentasPorSemana(
            List<Venta> ventas) {

        Map<Integer, List<Venta>> ventasAgrupadas
                = ventas.stream()
                        .collect(
                                Collectors.groupingBy(
                                        venta
                                        -> obtenerSemanaDelMes(
                                                venta.getFechaVenta()
                                                        .toLocalDate()
                                        ),
                                        TreeMap::new,
                                        Collectors.toList()
                                )
                        );

        return ventasAgrupadas.entrySet()
                .stream()
                .map(entry -> {

                    Integer semana
                            = entry.getKey();

                    List<Venta> ventasSemana
                            = entry.getValue();

                    long cantidadVentas
                            = ventasSemana.size();

                    double importeTotal
                            = ventasSemana.stream()
                                    .mapToDouble(
                                            Venta::getTotal
                                    )
                                    .sum();

                    return ReporteVentaMapper
                            .toVentaMesDTO(
                                    semana,
                                    cantidadVentas,
                                    importeTotal
                            );
                })
                .toList();
    }

    private void validarMes(int mes) {

        if (mes < 1 || mes > 12) {

            throw new IllegalArgumentException(
                    "El mes debe estar entre 1 y 12"
            );
        }
    }

    @Override
    public List<SemanaDelMesDTO> obtenerSemanaDelMes(int anio, int mes) {
        
        validarMes(mes);

        List<SemanaDelMesDTO> semanas
                = new ArrayList<>();

        int numeroSemana = 1;

        while (true) {

            try {

                RangoFechas rango
                        = calcularRangoSemana(
                                anio,
                                mes,
                                numeroSemana
                        );

                semanas.add(
                        new SemanaDelMesDTO(
                                numeroSemana,
                                rango.inicio()
                                        .toLocalDate(),
                                rango.fin()
                                        .toLocalDate()
                        )
                );

                numeroSemana++;

            } catch (IllegalArgumentException ex) {

                /*
                 * La excepción significa que ya no existe
                 * otra semana para ese mes.
                 */
                if (ex.getMessage() != null
                        && ex.getMessage().contains(
                                "no existe para el mes"
                        )) {

                    break;
                }

                throw ex;
            }
        }

        return semanas;
    }

    private record RangoFechas(
            LocalDateTime inicio,
            LocalDateTime fin) {

    }
}
