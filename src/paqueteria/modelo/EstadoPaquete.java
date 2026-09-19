package paqueteria.modelo;

public enum EstadoPaquete {

    RECIBIDO,
    ALMACENADO,
    CLASIFICANDO,
    CLASIFICADO,
    EMPAQUETANDO,
    EMPAQUETADO,
    EN_EXPEDICION,
    EN_REPARTO,
    NUEVO_INTENTO,
    ENTREGADO,
    DEVUELTO;

    private EstadoPaquete[] siguientes = {};

    static {
        RECIBIDO.siguientes = new EstadoPaquete[]{ALMACENADO};
        ALMACENADO.siguientes = new EstadoPaquete[]{CLASIFICANDO};
        CLASIFICANDO.siguientes = new EstadoPaquete[]{CLASIFICADO};
        CLASIFICADO.siguientes = new EstadoPaquete[]{EMPAQUETANDO};
        EMPAQUETANDO.siguientes = new EstadoPaquete[]{EMPAQUETADO};
        EMPAQUETADO.siguientes = new EstadoPaquete[]{EN_EXPEDICION};
        EN_EXPEDICION.siguientes = new EstadoPaquete[]{EN_REPARTO};
        EN_REPARTO.siguientes = new EstadoPaquete[]{ENTREGADO, NUEVO_INTENTO};
        NUEVO_INTENTO.siguientes = new EstadoPaquete[]{EN_REPARTO, DEVUELTO};
    }

    public boolean permiteIrA(EstadoPaquete destino) {
        for (EstadoPaquete candidato : siguientes) {
            if (candidato == destino) {
                return true;
            }
        }
        return false;
    }

    public boolean esFinal() {
        return siguientes.length == 0;
    }
}
