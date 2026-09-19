package paqueteria.concurrencia;

import java.util.Arrays;

public class Estadisticas {

    private final int[] entregasPorRepartidor;

    private int generados;
    private int entregados;
    private int devueltos;
    private long tiempoTotalEntrega;

    public Estadisticas(int numeroRepartidores) {
        this.entregasPorRepartidor = new int[numeroRepartidores];
    }

    public synchronized void paqueteGenerado() {
        generados++;
    }

    public synchronized void paqueteEntregado(int idRepartidor, long milisEnSistema) {
        entregados++;
        tiempoTotalEntrega += milisEnSistema;
        entregasPorRepartidor[idRepartidor - 1]++;
    }

    public synchronized void paqueteDevuelto() {
        devueltos++;
    }

    public synchronized int getGenerados() {
        return generados;
    }

    public synchronized int getEntregados() {
        return entregados;
    }

    public synchronized int getDevueltos() {
        return devueltos;
    }

    public synchronized int enProceso() {
        return generados - entregados - devueltos;
    }

    public synchronized double tiempoPromedioSegundos() {
        return entregados == 0 ? 0 : tiempoTotalEntrega / 1000.0 / entregados;
    }

    public synchronized int[] copiaEntregasPorRepartidor() {
        return entregasPorRepartidor.clone();
    }

    public synchronized void reiniciar() {
        generados = 0;
        entregados = 0;
        devueltos = 0;
        tiempoTotalEntrega = 0;
        Arrays.fill(entregasPorRepartidor, 0);
    }
}
