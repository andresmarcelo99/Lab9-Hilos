package paqueteria.modelo;

public enum Prioridad {

    URGENTE,
    ALTA,
    NORMAL,
    BAJA;

    public boolean esMasUrgenteQue(Prioridad otra) {
        return ordinal() < otra.ordinal();
    }
}
