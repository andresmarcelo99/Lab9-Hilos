package paqueteria.concurrencia;

import paqueteria.estructuras.ListaEnlazada;
import paqueteria.modelo.Paquete;

public class ZonaLogistica {

    public static final int SIN_LIMITE = Integer.MAX_VALUE;

    private final String nombre;
    private final int capacidad;
    private final ListaEnlazada<Paquete> paquetes = new ListaEnlazada<>();

    public ZonaLogistica(String nombre, int capacidad) {
        this.nombre = nombre;
        this.capacidad = capacidad;
    }

    public synchronized void poner(Paquete paquete) throws InterruptedException {
        while (paquetes.tamano() >= capacidad) {
            wait();
        }
        paquetes.agregar(paquete);
        notifyAll();
    }

    public synchronized Paquete tomar() throws InterruptedException {
        while (paquetes.estaVacia()) {
            wait();
        }
        return extraerMasUrgente();
    }

    public synchronized Paquete tomarSiHay() {
        if (paquetes.estaVacia()) {
            return null;
        }
        return extraerMasUrgente();
    }

    private Paquete extraerMasUrgente() {
        Paquete elegido = null;
        for (Paquete candidato : paquetes) {
            if (elegido == null || candidato.getPrioridad().esMasUrgenteQue(elegido.getPrioridad())) {
                elegido = candidato;
            }
        }
        paquetes.eliminar(elegido);
        notifyAll();
        return elegido;
    }

    public synchronized ListaEnlazada<Paquete> copiaContenido() {
        ListaEnlazada<Paquete> copia = new ListaEnlazada<>();
        for (Paquete paquete : paquetes) {
            copia.agregar(paquete);
        }
        return copia;
    }

    public synchronized int ocupacion() {
        return paquetes.tamano();
    }

    public synchronized void limpiar() {
        paquetes.limpiar();
        notifyAll();
    }

    public String getNombre() {
        return nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }
}
