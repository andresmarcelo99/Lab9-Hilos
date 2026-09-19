package paqueteria.concurrencia;

import paqueteria.estructuras.ListaEnlazada;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Registro {

    private static final int MAX_LINEAS = 200;
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final ListaEnlazada<String> lineas = new ListaEnlazada<>();

    public synchronized void anotar(String mensaje) {
        String linea = LocalTime.now().format(HORA) + " | " + mensaje;
        lineas.agregar(linea);
        if (lineas.tamano() > MAX_LINEAS) {
            lineas.eliminarPrimero();
        }
        System.out.println(linea);
    }

    public synchronized ListaEnlazada<String> copiaLineas() {
        ListaEnlazada<String> copia = new ListaEnlazada<>();
        for (String linea : lineas) {
            copia.agregar(linea);
        }
        return copia;
    }

    public synchronized void limpiar() {
        lineas.limpiar();
    }
}
