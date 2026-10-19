import java.util.LinkedHashMap;
import java.util.Map;

public class InstalacionCertificada extends Instalacion {
    private static final double CARGO_CERTIFICADO = 120.0;
    private static final double PORCENTAJE_GARANTIA = 0.05;
    private double valorMateriales;

    public InstalacionCertificada(String codigo, String cliente, String direccion, double horasEstimadas, EnumZona zona, int puntos, double valorMateriales) {
        super(codigo, cliente, direccion, horasEstimadas, zona, puntos);
        if (!Double.isFinite(valorMateriales) || valorMateriales <= 0) {
            throw new OperacionInvalidaException("El valor de los materiales debe ser un número positivo");
        }
        this.valorMateriales = valorMateriales;
    }

    public double getValorMateriales() {
        return valorMateriales;
    }

    public double calcularGarantia() {
        return valorMateriales * PORCENTAJE_GARANTIA;
    }

    @Override
    public double calcularCostoBase() {
        return super.calcularCostoBase() + CARGO_CERTIFICADO + calcularGarantia();
    }

    @Override
    public int calcularTiempoAtencion() {
        return super.calcularTiempoAtencion() + 2;
    }

    @Override
    public int getLimiteVisitas() {
        return 2;
    }

    @Override
    public String getCategoria() {
        return "Instalación eléctrica certificada";
    }

    @Override
    public Map<String, Object> getDetallesEspecificos() {
        Map<String, Object> detalles = new LinkedHashMap<>(super.getDetallesEspecificos());
        detalles.put("Valor de materiales (Q)", valorMateriales);
        detalles.put("Garantía 5% (Q)", calcularGarantia());
        detalles.put("Certificado (Q)", CARGO_CERTIFICADO);
        return detalles;
    }
}
