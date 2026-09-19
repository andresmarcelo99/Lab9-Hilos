package paqueteria.hilos;

import paqueteria.concurrencia.ControlSimulacion;
import paqueteria.concurrencia.Registro;
import paqueteria.concurrencia.ZonaLogistica;
import paqueteria.modelo.EstadoPaquete;
import paqueteria.modelo.Paquete;

public class HiloAlmacenista extends HiloTrabajador {

    private static final int PAQUETES_POR_VIAJE = 3;

    private final ZonaLogistica recepcion;
    private final ZonaLogistica almacen;

    public HiloAlmacenista(ZonaLogistica recepcion, ZonaLogistica almacen,
                           ControlSimulacion control, Registro registro) {
        super("Almacenista", control, registro);
        this.recepcion = recepcion;
        this.almacen = almacen;
    }

    @Override
    protected void trabajar() throws InterruptedException {
        Paquete primero = recepcion.tomar();
        dormir(900);
        almacenar(primero);

        // Cada viaje sube hasta tres paquetes a las estanterias.
        for (int i = 1; i < PAQUETES_POR_VIAJE; i++) {
            Paquete siguiente = recepcion.tomarSiHay();
            if (siguiente == null) {
                return;
            }
            almacenar(siguiente);
        }
    }

    private void almacenar(Paquete paquete) throws InterruptedException {
        paquete.cambiarEstado(EstadoPaquete.ALMACENADO);
        almacen.poner(paquete);
        registro.anotar(paquete + " almacenado");
    }
}
