package edu.unal.ed.queue;

/**
 * Cola (FIFO). Métodos mínimos exigidos por la guía.
 *
 * @param <T> tipo de los elementos
 */
public interface MyQueue<T> {

    /** Inserta un elemento al final. */
    void enqueue(T x);

    /** Elimina y retorna el primer elemento. */
    T dequeue();

    /** Retorna el primer elemento sin eliminarlo. */
    T front();

    /** Verifica si la cola está vacía. */
    boolean isEmpty();

    /** Retorna el número de elementos. */
    int size();

    /**
     * Elimina el primer valor {@code n} que encuentra, buscando desde el FRENTE.
     *
     * @return true si encontró y eliminó el valor
     */
    boolean delete(T n);
}
