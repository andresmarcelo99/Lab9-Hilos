package paqueteria;

import paqueteria.concurrencia.ControlSimulacion;
import paqueteria.concurrencia.Estadisticas;
import paqueteria.concurrencia.Registro;
import paqueteria.concurrencia.ZonaLogistica;
import paqueteria.estructuras.ListaEnlazada;
import paqueteria.hilos.HiloAlmacenista;
import paqueteria.hilos.HiloClasificador;
import paqueteria.hilos.HiloEmpaquetador;
import paqueteria.hilos.HiloRecepcion;
import paqueteria.hilos.HiloRepartidor;
import paqueteria.hilos.HiloTrabajador;

public class Simulacion {

    public static final String[] RUTAS = {"R1", "R2", "R3", "R4"};
    private static final int[] CAPACIDAD_VEHICULO = {5, 4, 6, 5};
    private static final int CLASIFICADORES = 3;
    private static final int EMPAQUETADORES = 2;

    private final ControlSimulacion control = new ControlSimulacion();
    private final Registro registro = new Registro();
    private final Estadisticas estadisticas = new Estadisticas(RUTAS.length);

    private final ZonaLogistica recepcion = new ZonaLogistica("RECEPCION", 10);
    private final ZonaLogistica almacen = new ZonaLogistica("ALMACEN", 20);
    private final ZonaLogistica clasificacion = new ZonaLogistica("CLASIFICACION", 10);
    private final ZonaLogistica entregados = new ZonaLogistica("ENTREGADOS", ZonaLogistica.SIN_LIMITE);
    private final ZonaLogistica devueltos = new ZonaLogistica("DEVUELTOS", ZonaLogistica.SIN_LIMITE);
    private final ZonaLogistica[] expedicion = new ZonaLogistica[RUTAS.length];

    private ListaEnlazada<HiloTrabajador> hilos = new ListaEnlazada<>();
    private HiloClasificador[] clasificadores = new HiloClasificador[0];
    private HiloEmpaquetador[] empaquetadores = new HiloEmpaquetador[0];
    private HiloRepartidor[] repartidores = new HiloRepartidor[0];

    public Simulacion() {
        for (int i = 0; i < RUTAS.length; i++) {
            expedicion[i] = new ZonaLogistica("EXPEDICION " + RUTAS[i], 15);
        }
    }

    public synchronized void iniciar() {
        if (control.estaActiva()) {
            return;
        }
        control.iniciar();
        crearHilos();
        for (HiloTrabajador hilo : hilos) {
            hilo.start();
        }
        registro.anotar("Simulacion iniciada");
    }

    public void pausar() {
        control.pausar();
        registro.anotar("Simulacion pausada");
    }

    public void reanudar() {
        control.reanudar();
        registro.anotar("Simulacion reanudada");
    }

    public synchronized void detener() {
        if (!control.estaActiva()) {
            return;
        }
        control.detener();
        for (HiloTrabajador hilo : hilos) {
            hilo.interrupt();
        }
        for (HiloTrabajador hilo : hilos) {
            try {
                hilo.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        registro.anotar("Simulacion detenida");
    }

    public synchronized void reiniciar() {
        detener();
        recepcion.limpiar();
        almacen.limpiar();
        clasificacion.limpiar();
        entregados.limpiar();
        devueltos.limpiar();
        for (ZonaLogistica zona : expedicion) {
            zona.limpiar();
        }
        estadisticas.reiniciar();
        registro.limpiar();
        hilos = new ListaEnlazada<>();
        registro.anotar("Simulacion reiniciada");
    }

    private void crearHilos() {
        hilos = new ListaEnlazada<>();
        clasificadores = new HiloClasificador[CLASIFICADORES];
        empaquetadores = new HiloEmpaquetador[EMPAQUETADORES];
        repartidores = new HiloRepartidor[RUTAS.length];

        hilos.agregar(new HiloRecepcion(recepcion, control, registro, estadisticas));
        hilos.agregar(new HiloAlmacenista(recepcion, almacen, control, registro));

        for (int i = 0; i < CLASIFICADORES; i++) {
            clasificadores[i] = new HiloClasificador(i + 1, almacen, clasificacion, control, registro);
            hilos.agregar(clasificadores[i]);
        }
        for (int i = 0; i < EMPAQUETADORES; i++) {
            empaquetadores[i] = new HiloEmpaquetador(i + 1, clasificacion, expedicion, RUTAS, control, registro);
            hilos.agregar(empaquetadores[i]);
        }
        for (int i = 0; i < RUTAS.length; i++) {
            repartidores[i] = new HiloRepartidor(i + 1, CAPACIDAD_VEHICULO[i], RUTAS[i],
                    expedicion[i], entregados, devueltos, control, registro, estadisticas);
            hilos.agregar(repartidores[i]);
        }
    }

    public ControlSimulacion getControl() {
        return control;
    }

    public Registro getRegistro() {
        return registro;
    }

    public Estadisticas getEstadisticas() {
        return estadisticas;
    }

    public ZonaLogistica getRecepcion() {
        return recepcion;
    }

    public ZonaLogistica getAlmacen() {
        return almacen;
    }

    public ZonaLogistica getClasificacion() {
        return clasificacion;
    }

    public ZonaLogistica getEntregados() {
        return entregados;
    }

    public ZonaLogistica getDevueltos() {
        return devueltos;
    }

    public ZonaLogistica getExpedicion(int indiceRuta) {
        return expedicion[indiceRuta];
    }

    public HiloClasificador[] getClasificadores() {
        return clasificadores;
    }

    public HiloEmpaquetador[] getEmpaquetadores() {
        return empaquetadores;
    }

    public HiloRepartidor[] getRepartidores() {
        return repartidores;
    }
}
