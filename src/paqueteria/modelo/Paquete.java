package paqueteria.modelo;

public class Paquete {

    public static final int MAX_INTENTOS = 3;

    private final String codigo;
    private final String cliente;
    private final String direccion;
    private final String ciudad;
    private final double peso;
    private final Prioridad prioridad;
    private final long creadoEn;

    // La GUI lee estos campos desde el EDT mientras los hilos los modifican.
    private volatile EstadoPaquete estado;
    private volatile String ruta;
    private volatile int intentos;
    private volatile long entregadoEn;

    public Paquete(String codigo, String cliente, String direccion, String ciudad,
                   double peso, Prioridad prioridad) {
        this.codigo = codigo;
        this.cliente = cliente;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.peso = peso;
        this.prioridad = prioridad;
        this.estado = EstadoPaquete.RECIBIDO;
        this.creadoEn = System.currentTimeMillis();
    }

    public void cambiarEstado(EstadoPaquete nuevo) {
        if (!estado.permiteIrA(nuevo)) {
            throw new IllegalStateException(codigo + ": transicion invalida " + estado + " -> " + nuevo);
        }
        estado = nuevo;
        if (nuevo == EstadoPaquete.ENTREGADO) {
            entregadoEn = System.currentTimeMillis();
        }
    }

    public void registrarIntento() {
        intentos++;
    }

    public boolean agotoIntentos() {
        return intentos >= MAX_INTENTOS;
    }

    public long tiempoEnSistema() {
        long fin = entregadoEn > 0 ? entregadoEn : System.currentTimeMillis();
        return fin - creadoEn;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCliente() {
        return cliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public double getPeso() {
        return peso;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public EstadoPaquete getEstado() {
        return estado;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public int getIntentos() {
        return intentos;
    }

    @Override
    public String toString() {
        return codigo;
    }
}
