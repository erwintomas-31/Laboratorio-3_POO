import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Vista {
     private final Scanner scanner = new Scanner(System.in);

    public static class FinDeEntradaException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    public String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (!scanner.hasNextLine()) {
            throw new FinDeEntradaException();
        }
        return scanner.nextLine().trim();
    }

    public String leerTextoObligatorio(String mensaje, String errorSiVacio) {
        String texto = leerTexto(mensaje);
        if (texto.isEmpty()) {
            throw new OperacionInvalidaException(errorSiVacio);
        }
        return texto;
    }

    public int leerEntero(String mensaje) {
        String texto = leerTexto(mensaje);
        if (!texto.matches("[-+]?\\d+")) {
            throw new OperacionInvalidaException("Entrada inválida: '" + texto + "' no es un número entero");
        }
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new OperacionInvalidaException("El número '" + texto + "' está fuera de rango");
        }
    }

    public int leerEnteroPositivo(String mensaje, String errorSiNoPositivo) {
        int valor = leerEntero(mensaje);
        if (valor <= 0) {
            throw new OperacionInvalidaException(errorSiNoPositivo);
        }
        return valor;
    }

    public double leerDecimal(String mensaje) {
        String texto = leerTexto(mensaje);
        if (!texto.matches("[-+]?\\d+([.,]\\d+)?")) {
            throw new OperacionInvalidaException("Entrada inválida: '" + texto + "' no es un número");
        }
        double valor = Double.parseDouble(texto.replace(',', '.'));
        if (!Double.isFinite(valor)) {
            throw new OperacionInvalidaException("El número '" + texto + "' está fuera de rango");
        }
        return valor;
    }

    public double leerDecimalPositivo(String mensaje, String errorSiNoPositivo) {
        double valor = leerDecimal(mensaje);
        if (valor <= 0) {
            throw new OperacionInvalidaException(errorSiNoPositivo);
        }
        return valor;
    }

    public boolean leerSiNo(String mensaje) {
        String texto = leerTexto(mensaje).toLowerCase();
        if (texto.equals("s") || texto.equals("si") || texto.equals("sí")) {
            return true;
        }
        if (texto.equals("n") || texto.equals("no")) {
            return false;
        }
        throw new OperacionInvalidaException("Respuesta inválida: escriba 's' para sí o 'n' para no");
    }

    public EnumZona leerZona() {
        int opcion = leerEntero("Zona (1 = Metropolitana, 2 = Periférica): ");
        if (opcion == 1) {
            return EnumZona.METROPOLITANA;
        }
        if (opcion == 2) {
            return EnumZona.PERIFERICA;
        }
        throw new OperacionInvalidaException("Zona inválida: elija 1 (Metropolitana) o 2 (Periférica)");
    }

    public EnumResultadoVisita leerResultadoVisita() {
        int opcion = leerEntero("Resultado (1 = Completada, 2 = Visita fallida): ");
        if (opcion == 1) {
            return EnumResultadoVisita.COMPLETADA;
        }
        if (opcion == 2) {
            return EnumResultadoVisita.FALLIDA;
        }
        throw new OperacionInvalidaException("Resultado inválido: elija 1 (Completada) o 2 (Visita fallida)");
    }

    public void mostrar(String texto) {
        System.out.println(texto);
    }

    public void mostrarOk(String texto) {
        System.out.println("OK " + texto);
    }

    public void mostrarRechazo(String motivo) {
        System.out.println("RECHAZADO " + motivo + " No se realizó ningún cambio");
    }

    public void mostrarMenuPrincipal() {
        System.out.println();
        System.out.println("TECNIHOGAR");
        System.out.println(" 1. Cotizar y registrar una orden");
        System.out.println(" 2. Registrar una cuadrilla");
        System.out.println(" 3. Crear una jornada");
        System.out.println(" 4. Asignar una orden a una jornada");
        System.out.println(" 5. Retirar una orden de una jornada");
        System.out.println(" 6. Iniciar una jornada");
        System.out.println(" 7. Registrar el resultado de una visita");
        System.out.println(" 8. Consultar órdenes");
        System.out.println(" 9. Consultar cuadrillas");
        System.out.println("10. Consultar jornadas");
        System.out.println("11. Reportes");
        System.out.println(" 0. Salir");
    }

    public void mostrarMenuCategorias() {
        System.out.println("Categorías:");
        System.out.println("  1. Diagnóstico");
        System.out.println("  2. Instalación");
        System.out.println("  3. Instalación eléctrica certificada");
        System.out.println("  4. Mantenimiento de aire acondicionado");
    }

    public void mostrarMenuReportes() {
        System.out.println("Reportes");
        System.out.println("  1. Órdenes por categoría y estado");
        System.out.println("  2. Ingresos");
        System.out.println("  3. Jornadas sin cerrar");
    }

    public void mostrarCotizacion(OrdenServicio c) {
        System.out.println();
        System.out.println("Cotización");
        System.out.println("Categoría:  " + c.getCategoria());
        System.out.println("Zona:  " + c.getZona());
        System.out.printf("Horas estimadas:   %.2f%n", c.getHorasEstimadas());
        mostrarDetallesEspecificos(c, "  ");
        System.out.printf("Costo:   Q%.2f%n", c.calcularCostoBase());
        System.out.println("Tiempo estimado:  " + c.calcularTiempoAtencion() + " día(s) hábil(es)");
        System.out.println();
    }

    public void mostrarOrdenRegistrada(OrdenServicio o) {
        mostrarOk(String.format("Orden: ", o.getCodigo()+" registrada y pendiente. Monto cobrado: "+ o.getIngresoPorRegistro()));
    }

    public void mostrarTablaOrdenes(List<OrdenServicio> ordenes) {
        if (ordenes.isEmpty()) {
            System.out.println("no hay órdenes que mostrar");
            return;
        }
        System.out.printf("%-10s %-35s %-14s %-13s %6s %-12s %7s %12s%n",
                "Código", "Categoría", "Cliente", "Zona", "Horas", "Estado", "Visitas", "Costo total");
        for (OrdenServicio o : ordenes) {
            System.out.printf("%-10s %-35s %-14s %-13s %6.2f %-12s %7s %12s%n",
                    o.getCodigo(), o.getCategoria(), cortar(o.getCliente(), 14), o.getZona(),
                    o.getHorasEstimadas(), o.getEstado(),
                    o.getContadorVisitas() + "/" + o.getLimiteVisitas(),
                    String.format("Q%.2f", o.getCostoTotal()));
        }
    }

    public void mostrarDetalleOrden(OrdenServicio o) {
        System.out.println();
        System.out.println("Orden " + o.getCodigo());
        System.out.println("Categoría:                  " + o.getCategoria());
        System.out.println("Cliente:                    " + o.getCliente());
        System.out.println("Dirección:                  " + o.getDireccion());
        System.out.println("Zona:                       " + o.getZona());
        System.out.printf("Horas estimadas:            %.2f%n", o.getHorasEstimadas());
        mostrarDetallesEspecificos(o, "    ");
        System.out.println("Estado:                     " + o.getEstado());
        System.out.println("Visitas realizadas:         " + o.getContadorVisitas() + " de "+ o.getLimiteVisitas() + " permitidas");
        System.out.println("Resultado última visita:    "+ (o.getResultadoUltimaVisita() == null ? "-" : o.getResultadoUltimaVisita()));
        System.out.println("Jornada asignada:           "+ (o.getIdJornadaAsignada() == null ? "-" : o.getIdJornadaAsignada()));
        System.out.printf("Costo base:                 Q%.2f%n", o.calcularCostoBase());
        System.out.printf("Cargos por visitas extra:   Q%.2f%n", o.getIngresosPorVisitasExtra());
        System.out.printf("Costo total:                Q%.2f%n", o.getCostoTotal());
        System.out.printf("Cobrado por registro:       Q%.2f%n", o.getIngresoPorRegistro());
        System.out.println("Tiempo estimado de atención: " + o.calcularTiempoAtencion() + " día(s) hábil(es)");
    }

    private void mostrarDetallesEspecificos(OrdenServicio orden, String sangria) {
        for (Map.Entry<String, Object> detalle : orden.getDetallesEspecificos().entrySet()) {
            Object valor = detalle.getValue();
            String texto = valor instanceof Double ? String.format("%.2f", valor) : String.valueOf(valor);
            System.out.println(sangria + detalle.getKey() + ": " + texto);
        }
    }

    public void mostrarCuadrillaRegistrada(Cuadrilla c) {
        mostrarOk("Cuadrilla " + c.getCodigo() + " registrada");
    }

    public void mostrarCuadrillas(List<Cuadrilla> cuadrillas) {
        if (cuadrillas.isEmpty()) {
            System.out.println("(no hay cuadrillas registradas)");
            return;
        }
        System.out.printf("%-10s %13s %-22s %-10s%n", "Código", "Capacidad (h)", "Cert. refrigeración", "Estado");
        for (Cuadrilla c : cuadrillas) {
            System.out.printf("%-10s %13.2f %-22s %-10s%n", c.getCodigo(), c.getCapacidadHoras(),c.isCertificadaRefrigeracion() ? "Sí" : "No", c.estaOcupada() ? "Ocupada" : "Libre");
        }
    }

    public void mostrarJornadaCreada(Jornada j) {
        mostrarOk(String.format("Jornada %s creada para la cuadrilla %s (capacidad %.2f h). Estado: %s.",j.getIdJornada(), j.getCuadrilla().getCodigo(), j.getCuadrilla().getCapacidadHoras(), j.getEstado()));
    }

    public void mostrarJornadas(List<Jornada> jornadas) {
        for (Jornada j : jornadas) {
            mostrarJornada(j);
        }
    }

    public void mostrarJornada(Jornada j) {
        System.out.printf("%s | Cuadrilla %s | %s%n", j.getIdJornada(), j.getCuadrilla().getCodigo(), j.getEstado());
        System.out.printf("     Horas asignadas: %.2f | Capacidad: %.2f | Disponible: %.2f%n",j.getHorasAsignadas(), j.getCuadrilla().getCapacidadHoras(), j.getCapacidadDisponible());
        if (j.getOrdenes().isEmpty()) {
            System.out.println("   Órdenes: (ninguna)");
            return;
        }
        for (OrdenServicio o : j.getOrdenes()) {
            EnumResultadoVisita r = j.getResultado(o);
            String situacion = r != null ? "resultado: " + r: (j.getEstado() == EnumEstadoJornada.INICIADA ? "sin resultado" : "");
            System.out.printf("     - %-10s %-35s %6.2f h  %s%n", o.getCodigo(), o.getCategoria(),o.getHorasEstimadas(), situacion);
        }
    }

    public void mostrarOrdenAsignada(Jornada j, OrdenServicio o) {
        mostrarOk(String.format("Orden %s asignada a %s. Horas asignadas: %.2f de %.2f (disponible: %.2f)",
                o.getCodigo(), j.getIdJornada(), j.getHorasAsignadas(),
                j.getCuadrilla().getCapacidadHoras(), j.getCapacidadDisponible()));
    }

    public void mostrarOrdenRetirada(Jornada j, OrdenServicio o) {
        mostrarOk("Orden " + o.getCodigo() + " retirada de " + j.getIdJornada() + "; vuelve a estar pendiente");
    }

    public void mostrarJornadaIniciada(Jornada j) {
        mostrarOk("Jornada " + j.getIdJornada() + " iniciada. Las órdenes pasan a estar en atención:");
        for (OrdenServicio o : j.getOrdenes()) {
            String cargo = o.getContadorVisitas() > 1
                    ? String.format(" cargo por visita adicional: Q%.2f", OrdenServicio.CARGO_VISITA_ADICIONAL): "";
            System.out.println("   - " + o.getCodigo() + ": visita " + o.getContadorVisitas() + " de "
                    + o.getLimiteVisitas() + cargo);
        }
    }

    public void mostrarResultadoRegistrado(Jornada j, OrdenServicio o) {
        mostrarOk("Resultado registrado. La orden " + o.getCodigo() + " queda " + o.getEstado()+ " (visitas: " + o.getContadorVisitas() + " de " + o.getLimiteVisitas() );
        if (j.getEstado() == EnumEstadoJornada.CERRADA) {
            mostrarOk("Todas las órdenes tienen resultado: la jornada " + j.getIdJornada()+ " se cerró y la cuadrilla " + j.getCuadrilla().getCodigo() + " quedó libre");
        }
    }

    public void mostrarReporteOrdenes(Map<String, Map<EnumEstadoOrden, Integer>> datos) {
        System.out.println("Órdenes por categoría y estado");
        if (datos.isEmpty()) {
            System.out.println("no hay órdenes");
            return;
        }
        System.out.printf("%-35s %10s %12s %11s %10s %7s%n", "Categoría", "PENDIENTE", "EN_ATENCION","COMPLETADA", "CANCELADA", "TOTAL");
        int[] totalesColumna = new int[EnumEstadoOrden.values().length];
        int granTotal = 0;
        for (Map.Entry<String, Map<EnumEstadoOrden, Integer>> fila : datos.entrySet()) {
            int totalFila = 0;
            List<Integer> valores = new ArrayList<>();
            for (EnumEstadoOrden estado : EnumEstadoOrden.values()) {
                int cantidad = fila.getValue().getOrDefault(estado, 0);
                valores.add(cantidad);
                totalesColumna[estado.ordinal()] += cantidad;
                totalFila += cantidad;
            }
            granTotal += totalFila;
            System.out.printf("%-35s %10d %12d %11d %10d %7d%n", fila.getKey(), valores.get(0), valores.get(1),valores.get(2), valores.get(3), totalFila);
        }
        System.out.printf("%-35s %10d %12d %11d %10d %7d%n", "TOTAL", totalesColumna[0], totalesColumna[1],
                totalesColumna[2], totalesColumna[3], granTotal);
    }

    public void mostrarReporteIngresos(double porRegistro, double porVisitas, double total,Map<String, double[]> porCategoria) {
        System.out.println("Ingresos");
        System.out.printf("Ingreso por registro:            %12s%n", String.format("Q%.2f", porRegistro));
        System.out.printf("Ingreso por visitas adicionales: %12s%n", String.format("Q%.2f", porVisitas));
        System.out.printf("INGRESO TOTAL:                   %12s%n", String.format("Q%.2f", total));
        System.out.println();
        System.out.println("Ingreso por categoría:");
        System.out.printf("%-35s %14s %14s %14s%n", "Categoría", "Por registro", "Visitas extra", "Total");
        for (Map.Entry<String, double[]> fila : porCategoria.entrySet()) {
            double registro = fila.getValue()[Gestor.POS_REGISTRO];
            double visitas = fila.getValue()[Gestor.POS_VISITAS];
            System.out.printf("%-35s %14s %14s %14s%n", fila.getKey(), String.format("Q%.2f", registro),
                    String.format("Q%.2f", visitas), String.format("Q%.2f", registro + visitas));
        }
    }

    public void mostrarReporteJornadasSinCerrar(List<Jornada> jornadas) {
        System.out.println("Jornadas sin cerrar");
        if (jornadas.isEmpty()) {
            System.out.println("no hay jornadas sin cerrar");
            return;
        }
        mostrarJornadas(jornadas);
    }

    private static String cortar(String texto, int max) {
        return texto.length() <= max ? texto : texto.substring(0, max - 1) + ".";
    }
}
