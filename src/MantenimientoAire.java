import java.util.LinkedHashMap;
import java.util.Map;

public class MantenimientoAire extends OrdenServicio {
    public static final int BTU_MINIMO = 9000;
    public static final int BTU_MAXIMO = 60000;
    private static final double COSTO_POR_HORA = 110.0;
    private static final double CARGO_GAS_REFRIGERANTE = 80.0;
    private int btu;

    public MantenimientoAire(String codigo, String cliente, String direccion, double horasEstimadas, EnumZona zona, int btu) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (zona != EnumZona.METROPOLITANA) {
            throw new OperacionInvalidaException(
                    "El mantenimiento de aire acondicionado solo se acepta en la zona metropolitana");
        }
        if (btu < BTU_MINIMO || btu > BTU_MAXIMO) {
            throw new OperacionInvalidaException(
                    "La capacidad del equipo debe estar entre " + BTU_MINIMO + " y " + BTU_MAXIMO + " BTU");
        }
        this.btu = btu;
    }

    public int getBtu() {
        return btu;
    }

    @Override
    public double calcularCostoBase() {
        return getTarifaBaseZona() + COSTO_POR_HORA * horasEstimadas + CARGO_GAS_REFRIGERANTE;
    }

    @Override
    public int calcularTiempoAtencion() {
        return 2;
    }

    @Override
    public int getLimiteVisitas() {
        return 1;
    }

    @Override
    public boolean esCompatibleConCuadrilla(Cuadrilla cuadrilla) {
        return cuadrilla.isCertificadaRefrigeracion();
    }

    @Override
    public String getRequisitoCuadrilla() {
        return "cuadrilla certificada en refrigeración";
    }

    @Override
    public String getCategoria() {
        return "Mantenimiento de aire acondicionado";
    }

    @Override
    public Map<String, Object> getDetallesEspecificos() {
        Map<String, Object> detalles = new LinkedHashMap<>();
        detalles.put("Capacidad del equipo (BTU)", btu);
        return detalles;
    }
}
