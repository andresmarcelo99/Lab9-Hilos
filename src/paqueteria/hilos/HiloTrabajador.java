package paqueteria.hilos;

import paqueteria.concurrencia.ControlSimulacion;
import paqueteria.concurrencia.Registro;

public abstract class HiloTrabajador extends Thread {

    protected final ControlSimulacion control;
    protected final Registro registro;

    protected HiloTrabajador(String nombre, ControlSimulacion control, Registro registro) {
        super(nombre);
        this.control = control;
        this.registro = registro;
    }

    protected abstract void trabajar() throws InterruptedException;

    // Comprobar la pausa tras cada espera permite que PAUSAR surta efecto
    // dentro de una tarea larga, no solo al terminarla.
    protected void dormir(long millis) throws InterruptedException {
        Thread.sleep(millis);
        control.esperarSiPausada();
    }

    @Override
    public void run() {
        try {
            while (control.estaActiva()) {
                control.esperarSiPausada();
                trabajar();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
