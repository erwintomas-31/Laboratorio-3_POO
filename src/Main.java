public class Main {
    public static void main(String[] args) {
        Gestor modelo = new Gestor();
        cargarDatosDePrueba(modelo);
        Vista vista = new Vista();
        Controlador controlador = new Controlador(modelo, vista);
        controlador.ejecutar();
    }

    private static void cargarDatosDePrueba(Gestor modelo) {
        modelo.cargarOrdenInicial(new Diagnostico("DIA-001", "Ana López",
                "6a. Avenida 10-20, zona 1", 1.0, EnumZona.METROPOLITANA));
        modelo.cargarOrdenInicial(new Diagnostico("DIA-002", "Carlos Méndez",
                "Km 15 Carretera a El Salvador", 1.5, EnumZona.PERIFERICA));

        modelo.cargarOrdenInicial(new Instalacion("INS-001", "Marta Pérez",
                "12 calle 5-30, zona 10", 2.5, EnumZona.METROPOLITANA, 5));
        modelo.cargarOrdenInicial(new Instalacion("INS-002", "Luis Ramírez",
                "Colonia Molino de las Flores, Mixco", 5.0, EnumZona.PERIFERICA, 2));
        modelo.cargarOrdenInicial(new Instalacion("INS-003", "Rosa Cifuentes",
                "Calzada Roosevelt 20-15, zona 11", 1.5, EnumZona.METROPOLITANA, 4));

        modelo.cargarOrdenInicial(new InstalacionCertificada("CER-001", "Sofía Castillo",
                "5a. Avenida 14-40, zona 9", 2.5, EnumZona.METROPOLITANA, 5, 2000.0));
        modelo.cargarOrdenInicial(new InstalacionCertificada("CER-002", "Jorge Morales",
                "Aldea Santa Catarina Pinula", 3.0, EnumZona.PERIFERICA, 3, 1500.0));

        modelo.cargarOrdenInicial(new MantenimientoAire("MAN-001", "Inés Girón",
                "Avenida Reforma 8-10, zona 9", 2.0, EnumZona.METROPOLITANA, 18000));
        modelo.cargarOrdenInicial(new MantenimientoAire("MAN-002", "Pedro Alvarado",
                "3a. Calle 4-15, zona 4", 1.5, EnumZona.METROPOLITANA, 36000));

        modelo.registrarCuadrilla(new Cuadrilla("CUA-01", 8.0, true));
        modelo.registrarCuadrilla(new Cuadrilla("CUA-02", 6.0, false));
        modelo.registrarCuadrilla(new Cuadrilla("CUA-03", 10.0, false));
    }
}
