import java.util.LinkedHashMap;
import java.util.Map;

public class Instalacion extends OrdenServicio {
    public static final double HORAS_POR_PUNTO = 0.75;
    private static final double COSTO_POR_HORA = 90.0;
    public int puntos;

    public Instalacion(String codigo, String cliente, String direccion,
                       double horasEstimadas, EnumZona zona, int puntos) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (puntos <= 0) {
            throw new OperacionInvalidaException("La cantidad de puntos debe ser un entero positivo");
        }
        this.puntos = puntos;
    }

    public int getPuntos() {
        return puntos;
    }

    public double calcularHorasPorPuntos() {
        return puntos * HORAS_POR_PUNTO;
    }

    public double calcularHorasFacturables() {
        return Math.max(horasEstimadas, calcularHorasPorPuntos());
    }

    @Override
    public double calcularCostoBase() {
        return getTarifaBaseZona() + COSTO_POR_HORA * Math.ceil(calcularHorasFacturables());
    }

    @Override
    public int calcularTiempoAtencion() {
        return zona == EnumZona.METROPOLITANA ? 3 : 5;
    }

    @Override
    public int getLimiteVisitas() {
        return 3;
    }

    @Override
    public boolean esCompatibleConCuadrilla(Cuadrilla cuadrilla) {
        return true;
    }

    @Override
    public String getCategoria() {
        return "Instalación";
    }

    @Override
    public Map<String, Object> getDetallesEspecificos() {
        Map<String, Object> detalles = new LinkedHashMap<>();
        detalles.put("Puntos a instalar", puntos);
        detalles.put("Horas por puntos (h)", calcularHorasPorPuntos());
        detalles.put("Horas facturables (h)", calcularHorasFacturables());
        detalles.put("Horas que se cobran (h)", (int) Math.ceil(calcularHorasFacturables()));
        return detalles;
    }
}
