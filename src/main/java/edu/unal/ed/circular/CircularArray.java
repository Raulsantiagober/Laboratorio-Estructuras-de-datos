package edu.unal.ed.circular;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Arreglo dinámico circular (buffer circular) implementado a mano. Es la base común de
 * {@code ArrayStack} y {@code ArrayQueue}.
 *
 * <p>Los elementos lógicos 0..size-1 viven en las posiciones físicas
 * {@code (head + i) % data.length}, de modo que agregar al final y retirar del frente o del
 * final no desplaza ningún elemento.
 *
 * <p><b>Estrategia de crecimiento:</b> capacidad inicial {@value #DEFAULT_CAPACITY}; cuando el
 * arreglo se llena se crea uno del doble de capacidad y se copian los elementos
 * "desenrollados" (el frente queda en la posición 0).
 * <ul>
 *   <li>Peor caso de una inserción: O(n) (la que dispara la copia).</li>
 *   <li>Costo amortizado: en N inserciones desde vacío las copias suman como máximo
 *       16 + 32 + ... + N &lt; 2N, así que el costo total es O(N) y el amortizado O(1).</li>
 * </ul>
 * No se reduce la capacidad al eliminar (la guía no lo pide).
 *
 * @param <T> tipo de los elementos
 */
public class CircularArray<T> {

    /** Capacidad inicial por defecto. */
    public static final int DEFAULT_CAPACITY = 16;

    private Object[] data;
    private int head; // posición física del primer elemento lógico
    private int size;

    public CircularArray() {
        this(DEFAULT_CAPACITY);
    }

    /** Permite fijar la capacidad inicial (usado para medir el peor caso del resize). */
    public CircularArray(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("La capacidad inicial debe ser >= 1");
        }
        data = new Object[initialCapacity];
    }

    /** Número de elementos. O(1). */
    public int size() {
        return size;
    }

    /** Indica si está vacío. O(1). */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Capacidad física actual (para pruebas y análisis). O(1). */
    public int capacity() {
        return data.length;
    }

    /** Agrega al final. O(1) amortizado, O(n) en el peor caso (resize). */
    public void addLast(T value) {
        if (size == data.length) {
            grow();
        }
        data[physical(size)] = value;
        size++;
    }

    /** Retira y retorna el primer elemento. O(1). */
    public T removeFirst() {
        requireNotEmpty();
        T value = elementAt(head);
        data[head] = null; // evita retener la referencia (ayuda al GC)
        head = (head + 1 == data.length) ? 0 : head + 1;
        size--;
        return value;
    }

    /** Retira y retorna el último elemento. O(1). */
    public T removeLast() {
        requireNotEmpty();
        int last = physical(size - 1);
        T value = elementAt(last);
        data[last] = null;
        size--;
        return value;
    }

    /** Retorna el primer elemento sin retirarlo. O(1). */
    public T peekFirst() {
        requireNotEmpty();
        return elementAt(head);
    }

    /** Retorna el último elemento sin retirarlo. O(1). */
    public T peekLast() {
        requireNotEmpty();
        return elementAt(physical(size - 1));
    }

    /**
     * Elimina la primera ocurrencia de {@code value} recorriendo desde el FRENTE (índice lógico 0).
     * O(n): búsqueda lineal + desplazamiento para cerrar el hueco.
     *
     * @return true si se eliminó algo
     */
    public boolean removeFirstOccurrence(T value) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(elementAt(physical(i)), value)) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina la primera ocurrencia de {@code value} recorriendo desde el FINAL (índice size-1).
     * O(n): búsqueda lineal + desplazamiento para cerrar el hueco.
     *
     * @return true si se eliminó algo
     */
    public boolean removeLastOccurrence(T value) {
        for (int i = size - 1; i >= 0; i--) {
            if (Objects.equals(elementAt(physical(i)), value)) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }


    /**
     * Elimina el índice lógico {@code index} desplazando una posición hacia el hueco los
     * elementos que están después de él. O(n - index).
     */
    private void removeAt(int index) {
        for (int i = index; i < size - 1; i++) {
            data[physical(i)] = data[physical(i + 1)];
        }
        data[physical(size - 1)] = null;
        size--;
    }

    /** Duplica la capacidad copiando los elementos en orden lógico. O(n). */
    private void grow() {
        Object[] bigger = new Object[data.length * 2];
        for (int i = 0; i < size; i++) {
            bigger[i] = data[physical(i)];
        }
        data = bigger;
        head = 0;
    }

    /**
     * Índice lógico → índice físico, equivalente a {@code (head + logicalIndex) % data.length}.
     * Como head &lt; capacidad y logicalIndex &lt; capacidad, la suma es menor que 2·capacidad y
     * basta restar una vez, que es más barato que la división entera del operador {@code %}.
     */
    private int physical(int logicalIndex) {
        int p = head + logicalIndex;
        return p >= data.length ? p - data.length : p;
    }

    @SuppressWarnings("unchecked")
    private T elementAt(int physicalIndex) {
        return (T) data[physicalIndex];
    }

    private void requireNotEmpty() {
        if (size == 0) {
            throw new NoSuchElementException("La estructura está vacía");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(elementAt(physical(i)));
        }
        return sb.append(']').toString();
    }
}
