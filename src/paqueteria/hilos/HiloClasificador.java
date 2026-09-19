package paqueteria.hilos;

import paqueteria.concurrencia.ControlSimulacion;
import paqueteria.concurrencia.Registro;
import paqueteria.concurrencia.ZonaLogistica;
import paqueteria.modelo.EstadoPaquete;
import paqueteria.modelo.Paquete;

public class HiloClasificador extends HiloTrabajador {

    private final ZonaLogistica almacen;
    private final ZonaLogistica clasificacion;

    private volatile Paquete paqueteActual;

    public HiloClasificador(int id, ZonaLogistica almacen, ZonaLogistica clasificacion,
                            ControlSimulacion control, Registro registro) {
        super("Clasificador-" + id, control, registro);
        this.almacen = almacen;
        this.clasificacion = clasificacion;
    }

    public static String rutaDe(String ciudad) {
        switch (ciudad) {
            case "Barcelona Centro":
            case "Eixample":
                return "R1";
            case "Gracia":
                return "R2";
            case "Sant Marti":
                return "R3";
            default:
                return "R4";
        }
    }

    @Override
    protected void trabajar() throws InterruptedException {
        Paquete paquete = almacen.tomar();
        paqueteActual = paquete;
        paquete.cambiarEstado(EstadoPaquete.CLASIFICANDO);
        registro.anotar(paquete + " tomado por " + getName());

        dormir(3000);

        paquete.setRuta(rutaDe(paquete.getCiudad()));
        paquete.cambiarEstado(EstadoPaquete.CLASIFICADO);
        paqueteActual = null;
        clasificacion.poner(paquete);
        registro.anotar(paquete + " clasificado -> " + paquete.getRuta());
    }

    public Paquete getPaqueteActual() {
        return paqueteActual;
    }
}
