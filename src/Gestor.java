import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Gestor{
    public static final int POS_REGISTRO = 0;
    public static final int POS_VISITAS = 1;
    private final Map<String, OrdenServicio> ordenes = new LinkedHashMap<>();
    private final Map<String, Cuadrilla> cuadrillas = new LinkedHashMap<>();
    private final Map<String, Jornada> jornadas = new LinkedHashMap<>();
    private int secuenciaJornada = 0;

    private static String clave(String codigo) {
        return codigo == null ? "" : codigo.trim().toUpperCase();
    }

    public void registrarOrden(OrdenServicio orden) {
        validarOrdenNueva(orden);
        ordenes.put(clave(orden.getCodigo()), orden);
        orden.cobrarRegistro();
    }

    public void cargarOrdenInicial(OrdenServicio orden) {
        validarOrdenNueva(orden);
        ordenes.put(clave(orden.getCodigo()), orden);
    }

    private void validarOrdenNueva(OrdenServicio orden) {
        if (orden == null) {
            throw new OperacionInvalidaException("La orden no puede estar vacía");
        }
        if (ordenes.containsKey(clave(orden.getCodigo()))) {
            throw new OperacionInvalidaException("Ya existe una orden con el código '" + orden.getCodigo());
        }
    }

    public OrdenServicio buscarOrden(String codigo) {
        OrdenServicio orden = ordenes.get(clave(codigo));
        if (orden == null) {
            throw new OperacionInvalidaException("No existe una orden con el código '" + codigo);
        }
        return orden;
    }

    public List<OrdenServicio> getOrdenes() {
        return Collections.unmodifiableList(new ArrayList<>(ordenes.values()));
    }

    public List<OrdenServicio> getOrdenesDisponibles() {
        List<OrdenServicio> resultado = new ArrayList<>();
        for (OrdenServicio orden : ordenes.values()) {
            if (orden.getEstado() == EnumEstadoOrden.PENDIENTE && !orden.estaAsignada()) {
                resultado.add(orden);
            }
        }
        return resultado;
    }

    public void registrarCuadrilla(Cuadrilla cuadrilla) {
        if (cuadrilla == null) {
            throw new OperacionInvalidaException("La cuadrilla no puede ser nula");
        }
        if (cuadrillas.containsKey(clave(cuadrilla.getCodigo()))) {
            throw new OperacionInvalidaException("Ya existe una cuadrilla con el código: '"+ cuadrilla.getCodigo() );
        }
        cuadrillas.put(clave(cuadrilla.getCodigo()), cuadrilla);
    }

    public Cuadrilla buscarCuadrilla(String codigo) {
        Cuadrilla cuadrilla = cuadrillas.get(clave(codigo));
        if (cuadrilla == null) {
            throw new OperacionInvalidaException("No existe una cuadrilla con el código: " + codigo);
        }
        return cuadrilla;
    }

    public List<Cuadrilla> getCuadrillas() {
        return Collections.unmodifiableList(new ArrayList<>(cuadrillas.values()));
    }

    public Jornada crearJornada(String codigoCuadrilla) {
        Cuadrilla cuadrilla = buscarCuadrilla(codigoCuadrilla);
        Jornada jornada = new Jornada(String.valueOf(secuenciaJornada + 1), cuadrilla);
        secuenciaJornada++;
        jornadas.put(clave(jornada.getIdJornada()), jornada);
        return jornada;
    }

    public Jornada buscarJornada(String idJornada) {
        Jornada jornada = jornadas.get(clave(idJornada));
        if (jornada == null) {
            throw new OperacionInvalidaException("No existe una jornada con el id '" + idJornada);
        }
        return jornada;
    }

    public List<Jornada> getJornadas() {
        return Collections.unmodifiableList(new ArrayList<>(jornadas.values()));
    }

    public List<Jornada> getJornadasPorEstado(EnumEstadoJornada estado) {
        List<Jornada> resultado = new ArrayList<>();
        for (Jornada jornada : jornadas.values()) {
            if (jornada.getEstado() == estado) {
                resultado.add(jornada);
            }
        }
        return resultado;
    }

    public List<Jornada> getJornadasNoCerradas() {
        List<Jornada> resultado = new ArrayList<>();
        for (Jornada jornada : jornadas.values()) {
            if (jornada.getEstado() != EnumEstadoJornada.CERRADA) {
                resultado.add(jornada);
            }
        }
        return resultado;
    }

    public void asignarOrden(String idJornada, String codigoOrden) {
        Jornada jornada = buscarJornada(idJornada);
        OrdenServicio orden = buscarOrden(codigoOrden);
        jornada.agregarOrden(orden);
    }

    public void retirarOrden(String idJornada, String codigoOrden) {
        Jornada jornada = buscarJornada(idJornada);
        OrdenServicio orden = buscarOrden(codigoOrden);
        jornada.retirarOrden(orden);
    }

    public Jornada iniciarJornada(String idJornada) {
        Jornada jornada = buscarJornada(idJornada);
        jornada.iniciarJornada();
        return jornada;
    }

    public Jornada registrarResultado(String idJornada, String codigoOrden, EnumResultadoVisita resultado) {
        Jornada jornada = buscarJornada(idJornada);
        OrdenServicio orden = buscarOrden(codigoOrden);
        jornada.registrarResultado(orden, resultado);
        return jornada;
    }

    public Map<String, Map<EnumEstadoOrden, Integer>> contarPorCategoriaYEstado() {
        Map<String, Map<EnumEstadoOrden, Integer>> resultado = new LinkedHashMap<>();
        for (OrdenServicio orden : ordenes.values()) {
            resultado.computeIfAbsent(orden.getCategoria(), k -> new EnumMap<>(EnumEstadoOrden.class)).merge(orden.getEstado(), 1, Integer::sum);
        }
        return resultado;
    }

    public double getIngresoPorRegistro() {
        double total = 0;
        for (OrdenServicio orden : ordenes.values()) {
            total += orden.getIngresoPorRegistro();
        }
        return total;
    }

    public double getIngresoPorVisitasAdicionales() {
        double total = 0;
        for (OrdenServicio orden : ordenes.values()) {
            total += orden.getIngresosPorVisitasExtra();
        }
        return total;
    }

    public double getIngresoTotal() {
        return getIngresoPorRegistro() + getIngresoPorVisitasAdicionales();
    }

    public Map<String, double[]> getIngresosPorCategoria() {
        Map<String, double[]> resultado = new LinkedHashMap<>();
        for (OrdenServicio orden : ordenes.values()) {
            double[] valores = resultado.computeIfAbsent(orden.getCategoria(), k -> new double[2]);
            valores[POS_REGISTRO] += orden.getIngresoPorRegistro();
            valores[POS_VISITAS] += orden.getIngresosPorVisitasExtra();
        }
        return resultado;
    }
}
