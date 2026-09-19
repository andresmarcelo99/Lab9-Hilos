package paqueteria.hilos;

import paqueteria.concurrencia.ControlSimulacion;
import paqueteria.concurrencia.Registro;
import paqueteria.concurrencia.ZonaLogistica;
import paqueteria.modelo.EstadoPaquete;
import paqueteria.modelo.Paquete;

public class HiloAlmacenista extends HiloTrabajador {

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
        Paquete paquete = recepcion.tomar();
        dormir(500);
        paquete.cambiarEstado(EstadoPaquete.ALMACENADO);
        almacen.poner(paquete);
        registro.anotar(paquete + " almacenado");
    }
}
