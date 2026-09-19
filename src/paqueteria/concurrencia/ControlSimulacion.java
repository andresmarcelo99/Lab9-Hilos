package paqueteria.concurrencia;

public class ControlSimulacion {

    private volatile boolean activa;
    private boolean pausada;

    public synchronized void iniciar() {
        activa = true;
        pausada = false;
        notifyAll();
    }

    public synchronized void pausar() {
        pausada = true;
    }

    public synchronized void reanudar() {
        pausada = false;
        notifyAll();
    }

    public synchronized void detener() {
        activa = false;
        pausada = false;
        notifyAll();
    }

    // Se llama entre operaciones, nunca dentro del candado de una zona.
    public synchronized void esperarSiPausada() throws InterruptedException {
        while (pausada && activa) {
            wait();
        }
    }

    public boolean estaActiva() {
        return activa;
    }

    public synchronized boolean estaPausada() {
        return pausada;
    }
}
