package paqueteria.hilos;

import paqueteria.concurrencia.ControlSimulacion;
import paqueteria.concurrencia.Estadisticas;
import paqueteria.concurrencia.Registro;
import paqueteria.concurrencia.ZonaLogistica;
import paqueteria.estructuras.ListaEnlazada;
import paqueteria.modelo.EstadoPaquete;
import paqueteria.modelo.EstadoRepartidor;
import paqueteria.modelo.Paquete;

import java.util.Random;

public class HiloRepartidor extends HiloTrabajador {

    private static final int INTENTOS_DE_CARGA = 8;
    private static final long ESPERA_ENTRE_CARGAS = 250;
    private static final int PROBABILIDAD_AUSENTE = 30;

    private final int id;
    private final int capacidad;
    private final String ruta;
    private final ZonaLogistica expedicion;
    private final ZonaLogistica entregados;
    private final ZonaLogistica devueltos;
    private final Estadisticas estadisticas;
    private final Random azar = new Random();

    // La carga la modifica solo este hilo, pero la GUI la lee desde el EDT.
    private final Object candadoCarga = new Object();
    private final ListaEnlazada<Paquete> carga = new ListaEnlazada<>();

    private volatile EstadoRepartidor estado = EstadoRepartidor.DISPONIBLE;
    private volatile int entregasRealizadas;

    public HiloRepartidor(int id, int capacidad, String ruta, ZonaLogistica expedicion,
                          ZonaLogistica entregados, ZonaLogistica devueltos, ControlSimulacion control,
                          Registro registro, Estadisticas estadisticas) {
        super("Repartidor-" + id, control, registro);
        this.id = id;
        this.capacidad = capacidad;
        this.ruta = ruta;
        this.expedicion = expedicion;
        this.entregados = entregados;
        this.devueltos = devueltos;
        this.estadisticas = estadisticas;
    }

    @Override
    protected void trabajar() throws InterruptedException {
        cargarVehiculo();
        salirARuta();
        entregar();
        regresar();
    }

    private void cargarVehiculo() throws InterruptedException {
        estado = EstadoRepartidor.CARGANDO;
        subir(expedicion.tomar());

        for (int intento = 0; intento < INTENTOS_DE_CARGA && ocupacionCarga() < capacidad; intento++) {
            Paquete siguiente = expedicion.tomarSiHay();
            if (siguiente == null) {
                dormir(ESPERA_ENTRE_CARGAS);
            } else {
                subir(siguiente);
            }
        }
        registro.anotar(getName() + " sale con " + ocupacionCarga() + "/" + capacidad + " paquetes por " + ruta);
    }

    private void subir(Paquete paquete) {
        paquete.cambiarEstado(EstadoPaquete.EN_REPARTO);
        synchronized (candadoCarga) {
            carga.agregar(paquete);
        }
    }

    private void salirARuta() throws InterruptedException {
        estado = EstadoRepartidor.EN_RUTA;
        dormir(2000 + azar.nextInt(2000));
    }

    private void entregar() throws InterruptedException {
        estado = EstadoRepartidor.ENTREGANDO;
        while (ocupacionCarga() > 0) {
            Paquete paquete;
            synchronized (candadoCarga) {
                paquete = carga.eliminarPrimero();
            }
            dormir(500 + azar.nextInt(500));

            if (azar.nextInt(100) < PROBABILIDAD_AUSENTE) {
                gestionarAusencia(paquete);
            } else {
                completarEntrega(paquete);
            }
        }
    }

    private void completarEntrega(Paquete paquete) throws InterruptedException {
        paquete.cambiarEstado(EstadoPaquete.ENTREGADO);
        entregados.poner(paquete);
        entregasRealizadas++;
        estadisticas.paqueteEntregado(id, paquete.tiempoEnSistema());
        registro.anotar(paquete + " entregado por " + getName());
    }

    private void gestionarAusencia(Paquete paquete) throws InterruptedException {
        paquete.cambiarEstado(EstadoPaquete.NUEVO_INTENTO);
        paquete.registrarIntento();
        registro.anotar(paquete + " intento " + paquete.getIntentos() + ": cliente ausente");

        if (paquete.agotoIntentos()) {
            paquete.cambiarEstado(EstadoPaquete.DEVUELTO);
            devueltos.poner(paquete);
            estadisticas.paqueteDevuelto();
            registro.anotar(paquete + " DEVUELTO tras " + paquete.getIntentos() + " intentos");
            return;
        }
        // Vuelve al final de la carga: se reintenta tras el resto del reparto.
        paquete.cambiarEstado(EstadoPaquete.EN_REPARTO);
        synchronized (candadoCarga) {
            carga.agregar(paquete);
        }
    }

    private void regresar() throws InterruptedException {
        estado = EstadoRepartidor.REGRESANDO;
        dormir(1000 + azar.nextInt(1000));
        estado = EstadoRepartidor.DISPONIBLE;
    }

    public int ocupacionCarga() {
        synchronized (candadoCarga) {
            return carga.tamano();
        }
    }

    public ListaEnlazada<Paquete> copiaCarga() {
        ListaEnlazada<Paquete> copia = new ListaEnlazada<>();
        synchronized (candadoCarga) {
            for (Paquete paquete : carga) {
                copia.agregar(paquete);
            }
        }
        return copia;
    }

    public int getIdRepartidor() {
        return id;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public String getRuta() {
        return ruta;
    }

    public EstadoRepartidor getEstadoRepartidor() {
        return estado;
    }

    public int getEntregasRealizadas() {
        return entregasRealizadas;
    }
}
