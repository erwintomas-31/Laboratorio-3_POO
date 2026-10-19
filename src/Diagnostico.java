public class Diagnostico extends OrdenServicio {
    public static final double HORAS_MAXIMAS = 1.5;
    private static final double COSTO_METROPOLITANA = 150.0;
    private static final double COSTO_PERIFERICA = 225.0;

    public Diagnostico(String codigo, String cliente, String direccion, double horasEstimadas, EnumZona zona) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (horasEstimadas > HORAS_MAXIMAS) {
            throw new OperacionInvalidaException("Un diagnóstico no puede superar " + HORAS_MAXIMAS + " horas estimadas");
        }
    }

    @Override
    public double calcularCostoBase() {
        return zona == EnumZona.METROPOLITANA ? COSTO_METROPOLITANA : COSTO_PERIFERICA;
    }

    @Override
    public int calcularTiempoAtencion() {
        return zona == EnumZona.METROPOLITANA ? 1 : 2;
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
        return "Diagnóstico";
    }
}
