public class Cuadrilla {
    private String codigo;
    private double capacidadHoras;
    private boolean certificadaRefrigeracion;
    private boolean ocupada;

    public Cuadrilla(String codigo, double capacidadHoras, boolean certificadaRefrigeracion) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new OperacionInvalidaException("El código de la cuadrilla no puede estar vacío");
        }
        if (capacidadHoras <= 0) {
            throw new OperacionInvalidaException("La capacidad de la cuadrilla debe ser un número positivo");
        }
        this.codigo = codigo.trim();
        this.capacidadHoras = capacidadHoras;
        this.certificadaRefrigeracion = certificadaRefrigeracion;
        this.ocupada = false;
    }

    public String getCodigo() { 
        return codigo; 
    }

    public double getCapacidadHoras() { 
        return capacidadHoras; 
    }

    public boolean isCertificadaRefrigeracion() { 
        return certificadaRefrigeracion; 
    }

    public boolean estaOcupada() { 
        return ocupada; 
    }

    public void setOcupada(boolean ocupada) { 
        this.ocupada = ocupada; 
    }

    @Override
    public String toString() {
        return codigo;
    }
}
