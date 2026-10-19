import java.util.List;
import java.util.function.Function;

public class Controlador {
    private static final String CODIGO_COTIZACION = "COTIZACION";
    private static final String ERROR_ID_JORNADA = "El id de la jornada no puede estar vacío";
    private static final String ERROR_CODIGO_ORDEN = "El código de la orden no puede estar vacío";
    private final Gestor modelo;
    private final Vista vista;

    public Controlador(Gestor modelo, Vista vista) {
        this.modelo = modelo;
        this.vista = vista;
    }

    public void ejecutar() {
        boolean salir = false;
        try {
            while (!salir) {
                vista.mostrarMenuPrincipal();
                try {
                    int opcion = vista.leerEntero("Seleccione una opción: ");
                    vista.mostrar("");
                    switch (opcion) {
                        case 1 -> cotizarYRegistrar();
                        case 2 -> registrarCuadrilla();
                        case 3 -> crearJornada();
                        case 4 -> asignarOrden();
                        case 5 -> retirarOrden();
                        case 6 -> iniciarJornada();
                        case 7 -> registrarResultado();
                        case 8 -> consultarOrdenes();
                        case 9 -> consultarCuadrillas();
                        case 10 -> consultarJornadas();
                        case 11 -> reportes();
                        case 0 -> salir = true;
                        default -> throw new OperacionInvalidaException(
                                "Opción inexistente: " + opcion + ". Elija un número del menú");
                    }
                } catch (OperacionInvalidaException e) {
                    vista.mostrarRechazo(e.getMessage());
                }
            }
            vista.mostrar("Cerrando el programa");
        } catch (Vista.FinDeEntradaException e) {
            vista.mostrar("");
            vista.mostrar("Fin de la entrada");
        }
    }

    private void cotizarYRegistrar() {
        vista.mostrar("Cotizar y registrar orden");
        vista.mostrarMenuCategorias();
        int categoria = vista.leerEntero("Categoría: ");
        if (categoria < 1 || categoria > 4) {
            throw new OperacionInvalidaException("Categoría inexistente: " + categoria);
        }

        String cliente = vista.leerTextoObligatorio("Nombre del cliente: ", "El nombre del cliente no puede estar vacío");
        String direccion = vista.leerTextoObligatorio("Dirección: ", "La dirección no puede estar vacía");
        EnumZona zona = vista.leerZona();
        double horas = vista.leerDecimalPositivo("Horas estimadas de trabajo: ", "Las horas estimadas deben ser un número positivo.");
        Function<String, OrdenServicio> fabrica = crearFabrica(categoria, cliente, direccion, zona, horas);
        OrdenServicio cotizacion = fabrica.apply(CODIGO_COTIZACION); 
        vista.mostrarCotizacion(cotizacion);
        if (!vista.leerSiNo("¿El cliente acepta la cotización? (s/n): ")) {
            vista.mostrar("Cotización descartada. No se registró nada ni se cobró nada");
            return;
        }

        String codigo = vista.leerTextoObligatorio("Código de la orden: ", ERROR_CODIGO_ORDEN);
        OrdenServicio orden = fabrica.apply(codigo);
        modelo.registrarOrden(orden);
        vista.mostrarOrdenRegistrada(orden);
    }

    private Function<String, OrdenServicio> crearFabrica(int categoria, String cliente, String direccion,EnumZona zona, double horas) {
        switch (categoria) {
            case 1:
                return codigo -> new Diagnostico(codigo, cliente, direccion, horas, zona);
            case 2: {
                int puntos = vista.leerEnteroPositivo("Cantidad de puntos a instalar: ","La cantidad de puntos debe ser un entero positivo");
                return codigo -> new Instalacion(codigo, cliente, direccion, horas, zona, puntos);
            }
            case 3: {
                int puntos = vista.leerEnteroPositivo("Cantidad de puntos a instalar: ", "La cantidad de puntos debe ser un entero positivo");
                double materiales = vista.leerDecimalPositivo("Valor declarado de los materiales (Q): ","El valor de los materiales debe ser un número positivo");
                return codigo -> new InstalacionCertificada(codigo, cliente, direccion, horas, zona,
                        puntos, materiales);
            }
            default: {
                int btu = vista.leerEntero("Capacidad del equipo en BTU: " + MantenimientoAire.BTU_MINIMO+ " a " + MantenimientoAire.BTU_MAXIMO);
                return codigo -> new MantenimientoAire(codigo, cliente, direccion, horas, zona, btu);
            }
        }
    }

    private void registrarCuadrilla() {
        vista.mostrar("Registrar cuadrilla");
        String codigo = vista.leerTextoObligatorio("Código de la cuadrilla: ","El código de la cuadrilla no puede estar vacío");
        double capacidad = vista.leerDecimalPositivo("Capacidad máxima (horas por jornada): ","La capacidad de la cuadrilla debe ser un número positivo");
        boolean certificada = vista.leerSiNo("¿Certificada en refrigeración? (s/n): ");
        Cuadrilla cuadrilla = new Cuadrilla(codigo, capacidad, certificada);
        modelo.registrarCuadrilla(cuadrilla);
        vista.mostrarCuadrillaRegistrada(cuadrilla);
    }

    private void crearJornada() {
        vista.mostrar("Crear jornada");
        vista.mostrarCuadrillas(modelo.getCuadrillas());
        String codigo = vista.leerTextoObligatorio("Código de la cuadrilla: ", "El código de la cuadrilla no puede estar vacío");
        vista.mostrarJornadaCreada(modelo.crearJornada(codigo));
    }

    private void asignarOrden() {
        vista.mostrar("Asignar orden a una jornada");
        List<Jornada> enPreparacion = jornadasEnPreparacion();
        vista.mostrar("Jornadas en preparación:");
        vista.mostrarJornadas(enPreparacion);
        vista.mostrar("");
        vista.mostrar("Órdenes pendientes disponibles: ");
        vista.mostrarTablaOrdenes(modelo.getOrdenesDisponibles());
        vista.mostrar("");
        String idJornada = vista.leerTextoObligatorio("Id de la jornada: ", ERROR_ID_JORNADA);
        String codigoOrden = vista.leerTextoObligatorio("Código de la orden: ", ERROR_CODIGO_ORDEN);
        modelo.asignarOrden(idJornada, codigoOrden);
        vista.mostrarOrdenAsignada(modelo.buscarJornada(idJornada), modelo.buscarOrden(codigoOrden));
    }

    private void retirarOrden() {
        vista.mostrar("Retirar orden de una jornada");
        vista.mostrarJornadas(jornadasEnPreparacion());
        vista.mostrar("");
        String idJornada = vista.leerTextoObligatorio("Id de la jornada: ", ERROR_ID_JORNADA);
        String codigoOrden = vista.leerTextoObligatorio("Código de la orden a retirar: ", ERROR_CODIGO_ORDEN);
        modelo.retirarOrden(idJornada, codigoOrden);
        vista.mostrarOrdenRetirada(modelo.buscarJornada(idJornada), modelo.buscarOrden(codigoOrden));
    }

    private void iniciarJornada() {
        vista.mostrar("Iniciar jornada");
        vista.mostrarJornadas(jornadasEnPreparacion());
        vista.mostrar("");
        String idJornada = vista.leerTextoObligatorio("Id de la jornada: ", ERROR_ID_JORNADA);
        vista.mostrarJornadaIniciada(modelo.iniciarJornada(idJornada));
    }

    private void registrarResultado() {
        vista.mostrar("Registrar resultado de una visita");
        List<Jornada> iniciadas = modelo.getJornadasPorEstado(EnumEstadoJornada.INICIADA);
        if (iniciadas.isEmpty()) {
            throw new OperacionInvalidaException("No hay jornadas iniciadas");
        }
        vista.mostrarJornadas(iniciadas);
        vista.mostrar("");
        String idJornada = vista.leerTextoObligatorio("Id de la jornada: ", ERROR_ID_JORNADA);
        String codigoOrden = vista.leerTextoObligatorio("Código de la orden: ", ERROR_CODIGO_ORDEN);
        EnumResultadoVisita resultado = vista.leerResultadoVisita();
        Jornada jornada = modelo.registrarResultado(idJornada, codigoOrden, resultado);
        vista.mostrarResultadoRegistrado(jornada, modelo.buscarOrden(codigoOrden));
    }

    private List<Jornada> jornadasEnPreparacion() {
        List<Jornada> enPreparacion = modelo.getJornadasPorEstado(EnumEstadoJornada.PREPARACION);
        if (enPreparacion.isEmpty()) {
            throw new OperacionInvalidaException("No hay jornadas en preparación");
        }
        return enPreparacion;
    }

    private void consultarOrdenes() {
        vista.mostrar("Órdenes");
        vista.mostrarTablaOrdenes(modelo.getOrdenes());
        vista.mostrar("");
        String codigo = vista.leerTexto("Código de una orden para ver su detalle (Enter para volver): ");
        if (!codigo.isEmpty()) {
            vista.mostrarDetalleOrden(modelo.buscarOrden(codigo));
        }
    }

    private void consultarCuadrillas() {
        vista.mostrar("Cuadrillas");
        vista.mostrarCuadrillas(modelo.getCuadrillas());
    }

    private void consultarJornadas() {
        vista.mostrar("Jornadas");
        if (modelo.getJornadas().isEmpty()) {
            vista.mostrar("todavía no hay jornadas");
            return;
        }
        vista.mostrarJornadas(modelo.getJornadas());
    }

    private void reportes() {
        vista.mostrarMenuReportes();
        int opcion = vista.leerEntero("Reporte: ");
        vista.mostrar("");
        switch (opcion) {
            case 1 -> vista.mostrarReporteOrdenes(modelo.contarPorCategoriaYEstado());
            case 2 -> vista.mostrarReporteIngresos(modelo.getIngresoPorRegistro(),
                    modelo.getIngresoPorVisitasAdicionales(), modelo.getIngresoTotal(),
                    modelo.getIngresosPorCategoria());
            case 3 -> vista.mostrarReporteJornadasSinCerrar(modelo.getJornadasNoCerradas());
            default -> throw new OperacionInvalidaException("Reporte inexistente: " + opcion);
        }
    }
}
