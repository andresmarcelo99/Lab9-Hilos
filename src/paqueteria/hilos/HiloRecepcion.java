package paqueteria.hilos;

import paqueteria.concurrencia.ControlSimulacion;
import paqueteria.concurrencia.Estadisticas;
import paqueteria.concurrencia.Registro;
import paqueteria.concurrencia.ZonaLogistica;
import paqueteria.modelo.Paquete;
import paqueteria.modelo.Prioridad;

import java.util.Random;

public class HiloRecepcion extends HiloTrabajador {

    private static final String[] CLIENTES = {
        "Carlos Lopez", "Ana Ruiz", "Marta Gil", "Javier Soto", "Laura Vega",
        "Diego Mora", "Elena Paz", "Hugo Serra", "Nuria Blanco", "Pablo Diaz"
    };
    private static final String[] CIUDADES = {
        "Barcelona Centro", "Eixample", "Gracia", "Sant Marti", "Badalona"
    };
    private static final String[] CALLES = {
        "Gran Via", "Calle Mayor", "Av. Diagonal", "Paseo de Gracia", "Calle Marina"
    };

    private final ZonaLogistica recepcion;
    private final Estadisticas estadisticas;
    private final Random azar = new Random();

    private int contador;

    public HiloRecepcion(ZonaLogistica recepcion, ControlSimulacion control,
                         Registro registro, Estadisticas estadisticas) {
        super("Recepcion", control, registro);
        this.recepcion = recepcion;
        this.estadisticas = estadisticas;
    }

    @Override
    protected void trabajar() throws InterruptedException {
        dormir(1200 + azar.nextInt(900));

        // Una entrega puede traer varios paquetes de golpe. Esa irregularidad es
        // la que hace que se formen colas en las zonas siguientes.
        int lote = azar.nextInt(4) == 0 ? 3 : 1;
        for (int i = 0; i < lote; i++) {
            Paquete paquete = generar();
            recepcion.poner(paquete);
            estadisticas.paqueteGenerado();
            registro.anotar(paquete + " recibido (" + paquete.getCiudad()
                    + ", " + paquete.getPeso() + " kg, " + paquete.getPrioridad() + ")");
        }
    }

    private Paquete generar() {
        contador++;
        String codigo = String.format("PKG-%05d", contador);
        String direccion = CALLES[azar.nextInt(CALLES.length)] + " " + (1 + azar.nextInt(120));
        double peso = Math.round((0.5 + azar.nextDouble() * 7.5) * 10) / 10.0;
        return new Paquete(codigo,
                CLIENTES[azar.nextInt(CLIENTES.length)],
                direccion,
                CIUDADES[azar.nextInt(CIUDADES.length)],
                peso,
                Prioridad.values()[azar.nextInt(Prioridad.values().length)]);
    }
}
