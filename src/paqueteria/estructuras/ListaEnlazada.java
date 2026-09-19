package paqueteria.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class ListaEnlazada<T> implements Iterable<T> {

    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamano;

    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            cola.setSiguiente(nuevo);
        }
        cola = nuevo;
        tamano++;
    }

    public T eliminarPrimero() {
        if (cabeza == null) {
            return null;
        }
        T dato = cabeza.getDato();
        cabeza = cabeza.getSiguiente();
        if (cabeza == null) {
            cola = null;
        }
        tamano--;
        return dato;
    }

    public boolean eliminar(T dato) {
        Nodo<T> anterior = null;
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (actual.getDato().equals(dato)) {
                if (anterior == null) {
                    cabeza = actual.getSiguiente();
                } else {
                    anterior.setSiguiente(actual.getSiguiente());
                }
                if (actual == cola) {
                    cola = anterior;
                }
                tamano--;
                return true;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
        return false;
    }

    public T buscar(Predicate<T> criterio) {
        for (Nodo<T> actual = cabeza; actual != null; actual = actual.getSiguiente()) {
            if (criterio.test(actual.getDato())) {
                return actual.getDato();
            }
        }
        return null;
    }

    public T obtener(int indice) {
        if (indice < 0 || indice >= tamano) {
            throw new IndexOutOfBoundsException("Indice " + indice + " en lista de tamano " + tamano);
        }
        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }

    public int tamano() {
        return tamano;
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    public void limpiar() {
        cabeza = null;
        cola = null;
        tamano = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private Nodo<T> actual = cabeza;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (actual == null) {
                    throw new NoSuchElementException();
                }
                T dato = actual.getDato();
                actual = actual.getSiguiente();
                return dato;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Nodo<T> actual = cabeza; actual != null; actual = actual.getSiguiente()) {
            sb.append(actual.getDato());
            if (actual.getSiguiente() != null) {
                sb.append(" -> ");
            }
        }
        return sb.append("]").toString();
    }
}
