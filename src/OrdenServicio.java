import java.util.Collections;
import java.util.Map;

public abstract class OrdenServicio {
    public static final double TARIFA_BASE_METROPOLITANA = 100.0;
    public static final double TARIFA_BASE_PERIFERICA = 175.0;
    public static final double CARGO_VISITA_ADICIONAL = 40.0;
    protected String codigo;
    protected String cliente;
    protected String direccion;
    protected double horasEstimadas;
    protected EnumZona zona;
    protected EnumEstadoOrden estado;
    protected int contadorVisitas;
    protected double ingresosPorVisitasExtra;
    protected EnumResultadoVisita resultadoUltimaVisita;
    protected double ingresoPorRegistro;
    protected String idJornadaAsignada;

    public OrdenServicio(String codigo, String cliente, String direccion, double horasEstimadas, EnumZona zona) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new OperacionInvalidaException("El código de la orden no puede estar vacío");
        }
        if (cliente == null || cliente.trim().isEmpty()) {
            throw new OperacionInvalidaException("El nombre del cliente no puede estar vacío");
        }
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new OperacionInvalidaException("La dirección no puede estar vacía");
        }
        if (!Double.isFinite(horasEstimadas) || horasEstimadas <= 0) {
            throw new OperacionInvalidaException("Las horas estimadas deben ser un número positivo");
        }
        if (zona == null) {
            throw new OperacionInvalidaException("La zona es obligatoria");
        }
        this.codigo = codigo.trim();
        this.cliente = cliente.trim();
        this.direccion = direccion.trim();
        this.horasEstimadas = horasEstimadas;
        this.zona = zona;
        this.estado = EnumEstadoOrden.PENDIENTE;
        this.contadorVisitas = 0;
        this.ingresosPorVisitasExtra = 0.0;
        this.resultadoUltimaVisita = null;
        this.ingresoPorRegistro = 0.0;
        this.idJornadaAsignada = null;
    }

    public abstract double calcularCostoBase();

    public abstract int calcularTiempoAtencion();

    public abstract int getLimiteVisitas();

    public abstract boolean esCompatibleConCuadrilla(Cuadrilla cuadrilla);

    public abstract String getCategoria();

    public String getRequisitoCuadrilla() {
        return "ninguno";
    }

    public Map<String, Object> getDetallesEspecificos() {
        return Collections.emptyMap();
    }

    protected double getTarifaBaseZona() {
        return zona == EnumZona.METROPOLITANA ? TARIFA_BASE_METROPOLITANA : TARIFA_BASE_PERIFERICA;
    }

    public void incrementarVisita() {
        contadorVisitas++;
        if (contadorVisitas > 1) {
            ingresosPorVisitasExtra += CARGO_VISITA_ADICIONAL;
        }
    }

    public void cobrarRegistro() {
        if (ingresoPorRegistro > 0) {
            throw new OperacionInvalidaException("La orden " + codigo + " ya fue cobrada");
        }
        ingresoPorRegistro = calcularCostoBase();
    }

    public double getCostoTotal() {
        return calcularCostoBase() + ingresosPorVisitasExtra;
    }

    public double getIngresoCobrado() {
        return ingresoPorRegistro + ingresosPorVisitasExtra;
    }

    public boolean visitasAgotadas() {
        return contadorVisitas >= getLimiteVisitas();
    }

    public boolean estaAsignada() {
        return idJornadaAsignada != null;
    }

    public String getCodigo() { 
        return codigo; 
    }

    public String getCliente() { 
        return cliente; 
    }

    public String getDireccion() { 
        return direccion; 
    }

    public double getHorasEstimadas() { 
        return horasEstimadas; 
    
    }
    public EnumZona getZona() { 
        return zona; 
    
}

    public int getContadorVisitas() { 
        return contadorVisitas; 
    }

    public double getIngresosPorVisitasExtra() { 
        return ingresosPorVisitasExtra; 
    }

    public double getIngresoPorRegistro() { 
        return ingresoPorRegistro; 
    }

    public String getIdJornadaAsignada() { 
        return idJornadaAsignada; 
    }

    public void setIdJornadaAsignada(String idJornada) { 
        this.idJornadaAsignada = idJornada; 
    }

    public EnumEstadoOrden getEstado() { 
        return estado; 
    }

    public void setEstado(EnumEstadoOrden estado) {
        if (estado == null) {
            throw new OperacionInvalidaException("El estado de la orden no puede ser nulo");
        }
        this.estado = estado;
    }

    public EnumResultadoVisita getResultadoUltimaVisita() { 
        return resultadoUltimaVisita; 
    }

    public void setResultadoUltimaVisita(EnumResultadoVisita resultado) {
        this.resultadoUltimaVisita = resultado;
    }

    @Override
    public String toString() {
        return codigo + " (" + getCategoria() + ")";
    }
}
