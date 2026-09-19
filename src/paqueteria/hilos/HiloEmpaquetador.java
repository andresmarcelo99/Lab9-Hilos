package paqueteria.hilos;

import paqueteria.concurrencia.ControlSimulacion;
import paqueteria.concurrencia.Registro;
import paqueteria.concurrencia.ZonaLogistica;
import paqueteria.modelo.EstadoPaquete;
import paqueteria.modelo.Paquete;

public class HiloEmpaquetador extends HiloTrabajador {

    private final ZonaLogistica clasificacion;
    private final ZonaLogistica[] expedicionPorRuta;
    private final String[] rutas;

    private volatile Paquete paqueteActual;

    public HiloEmpaquetador(int id, ZonaLogistica clasificacion, ZonaLogistica[] expedicionPorRuta,
                            String[] rutas, ControlSimulacion control, Registro registro) {
        super("Empaquetador-" + id, control, registro);
        this.clasificacion = clasificacion;
        this.expedicionPorRuta = expedicionPorRuta;
        this.rutas = rutas;
    }

    public static long tiempoSegunPeso(double peso) {
        if (peso <= 2.0) {
            return 1000;
        }
        return peso <= 5.0 ? 2000 : 3000;
    }

    @Override
    protected void trabajar() throws InterruptedException {
        Paquete paquete = clasificacion.tomar();
        paqueteActual = paquete;
        paquete.cambiarEstado(EstadoPaquete.EMPAQUETANDO);

        dormir(tiempoSegunPeso(paquete.getPeso()));

        paquete.cambiarEstado(EstadoPaquete.EMPAQUETADO);
        paqueteActual = null;
        registro.anotar(paquete + " empaquetado por " + getName());

        paquete.cambiarEstado(EstadoPaquete.EN_EXPEDICION);
        expedicion(paquete.getRuta()).poner(paquete);
    }

    private ZonaLogistica expedicion(String ruta) {
        for (int i = 0; i < rutas.length; i++) {
            if (rutas[i].equals(ruta)) {
                return expedicionPorRuta[i];
            }
        }
        throw new IllegalStateException("Ruta desconocida: " + ruta);
    }

    public Paquete getPaqueteActual() {
        return paqueteActual;
    }
}
