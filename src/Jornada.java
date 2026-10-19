import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Jornada {
    private final String idJornada;
    private final Cuadrilla cuadrilla;
    private EnumEstadoJornada estado;
    private final List<OrdenServicio> ordenes = new ArrayList<>();
    private final Map<String, EnumResultadoVisita> resultados = new LinkedHashMap<>();

    public Jornada(String idJornada, Cuadrilla cuadrilla) {
        if (idJornada == null || idJornada.trim().isEmpty()) {
            throw new OperacionInvalidaException("El ID de la jornada no puede estar vacío");
        }
        if (cuadrilla == null) {
            throw new OperacionInvalidaException("La jornada necesita una cuadrilla");
        }
        if (cuadrilla.estaOcupada()) {
            throw new OperacionInvalidaException("La cuadrilla " + cuadrilla.getCodigo() + " ya tiene una jornada sin cerrar");
        }
        this.idJornada = idJornada.trim();
        this.cuadrilla = cuadrilla;
        this.estado = EnumEstadoJornada.PREPARACION;
        cuadrilla.setOcupada(true);
    }

    public boolean agregarOrden(OrdenServicio orden) {
        if (orden == null) {
            throw new OperacionInvalidaException("La orden no existe");
        }
        if (estado != EnumEstadoJornada.PREPARACION) {
            throw new OperacionInvalidaException("Solo se pueden asignar órdenes a una jornada en preparación " + "la jornada " + idJornada + " está " + estado);
        }
        if (orden.getEstado() != EnumEstadoOrden.PENDIENTE) {
            throw new OperacionInvalidaException("La orden " + orden.getCodigo() + " no está pendiente, estado: " + orden.getEstado());
        }
        if (orden.estaAsignada()) {
            throw new OperacionInvalidaException("La orden " + orden.getCodigo()+ " ya está asignada a la jornada " + orden.getIdJornadaAsignada());
        }
        if (!orden.esCompatibleConCuadrilla(cuadrilla)) {
            throw new OperacionInvalidaException("La orden " + orden.getCodigo() + " "+ orden.getCategoria() + " requiere " + orden.getRequisitoCuadrilla());
        }
        double nuevasHoras = getHorasAsignadas() + orden.getHorasEstimadas();
        if (nuevasHoras > cuadrilla.getCapacidadHoras()) {
            throw new OperacionInvalidaException("Capacidad excedida: la jornada tiene: "+ getHorasAsignadas() + " h asignadas, la orden requiere: " +orden.getHorasEstimadas()+" h y la cuadrilla : "+cuadrilla.getCodigo()+" solo admite: "+cuadrilla.getCapacidadHoras()+" h");
        }
        ordenes.add(orden);
        orden.setIdJornadaAsignada(idJornada);
        return true;
    }

    public boolean retirarOrden(OrdenServicio orden) {
        if (estado != EnumEstadoJornada.PREPARACION) {
            throw new OperacionInvalidaException("Solo se pueden retirar órdenes de una jornada en preparación ");
        }
        if (orden == null || !ordenes.contains(orden)) {
            throw new OperacionInvalidaException("La orden no pertenece a la jornada " + idJornada);
        }
        ordenes.remove(orden);
        orden.setIdJornadaAsignada(null);  
        return true;
    }

    public void iniciarJornada() {
        if (estado != EnumEstadoJornada.PREPARACION) {
            throw new OperacionInvalidaException("La jornada " + idJornada+ " no se puede iniciar porque está " + estado);
        }
        if (ordenes.isEmpty()) {
            throw new OperacionInvalidaException("No se puede iniciar una jornada vacía");
        }
        estado = EnumEstadoJornada.INICIADA;
        for (OrdenServicio orden : ordenes) {
            orden.incrementarVisita();      
            orden.setEstado(EnumEstadoOrden.EN_ATENCION);
            orden.setResultadoUltimaVisita(null);
        }
    }

    public void registrarResultado(OrdenServicio orden, EnumResultadoVisita resultado) {
        if (resultado == null) {
            throw new OperacionInvalidaException("El resultado de la visita es obligatorio");
        }
        if (estado != EnumEstadoJornada.INICIADA) {
            throw new OperacionInvalidaException("Solo se registran resultados en una jornada iniciada ");
        }
        if (orden == null || !ordenes.contains(orden)) {
            throw new OperacionInvalidaException("La orden no pertenece a la jornada " + idJornada);
        }
        if (resultados.containsKey(orden.getCodigo())) {
            throw new OperacionInvalidaException("La orden " + orden.getCodigo()+ " ya tiene resultado registrado en esta jornada");
        }
        if (orden.getEstado() != EnumEstadoOrden.EN_ATENCION) {
            throw new OperacionInvalidaException("La orden " + orden.getCodigo()+ " no está en atención, estado: " + orden.getEstado());
        }
        resultados.put(orden.getCodigo(), resultado);
        orden.setResultadoUltimaVisita(resultado);
        orden.setIdJornadaAsignada(null);
        if (resultado == EnumResultadoVisita.COMPLETADA) {
            orden.setEstado(EnumEstadoOrden.COMPLETADA);
        } else if (orden.visitasAgotadas()) {
            orden.setEstado(EnumEstadoOrden.CANCELADA);
        } else {
            orden.setEstado(EnumEstadoOrden.PENDIENTE);
        }
        if (resultados.size() == ordenes.size()) {
            cerrarJornada();
        }
    }

    public void cerrarJornada() {
        if (estado != EnumEstadoJornada.INICIADA) {
            throw new OperacionInvalidaException("La jornada " + idJornada+ " no se puede cerrar porque está " + estado);
        }
        if (resultados.size() != ordenes.size()) {
            throw new OperacionInvalidaException("Aún hay órdenes sin resultado en la jornada " + idJornada);
        }
        estado = EnumEstadoJornada.CERRADA;
        cuadrilla.setOcupada(false);
    }

    public double getHorasAsignadas() {
        double total = 0;
        for (OrdenServicio orden : ordenes) {
            total += orden.getHorasEstimadas();
        }
        return total;
    }

    public double getCapacidadDisponible() {
        return cuadrilla.getCapacidadHoras() - getHorasAsignadas();
    }

    public String getIdJornada() { 
        return idJornada; 
    }

    public Cuadrilla getCuadrilla() { 
        return cuadrilla; 
    }

    public EnumEstadoJornada getEstado() { 
        return estado; 
    }

    public List<OrdenServicio> getOrdenes() {
        return Collections.unmodifiableList(ordenes);
    }

    public EnumResultadoVisita getResultado(OrdenServicio orden) {
        return resultados.get(orden.getCodigo());
    }

    @Override
    public String toString() {
        return idJornada + " " + cuadrilla.getCodigo() + ", " + estado;
    }
}
