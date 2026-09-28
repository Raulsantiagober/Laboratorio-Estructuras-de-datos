package edu.unal.ed.stack;

/**
 * Pila (LIFO). Métodos mínimos exigidos por la guía.
 *
 * @param <T> tipo de los elementos
 */
public interface MyStack<T> {

    /** Inserta un elemento en la cima. */
    void push(T x);

    /** Elimina y retorna el elemento en la cima. */
    T pop();

    /** Retorna el elemento en la cima sin eliminarlo. */
    T peek();

    /** Verifica si la pila está vacía. */
    boolean isEmpty();

    /** Retorna el número de elementos. */
    int size();

    /**
     * Elimina el primer valor {@code n} que encuentra. Decisión de diseño: la búsqueda empieza
     * en la CIMA (el orden natural en que se "ve" una pila).
     *
     * @return true si encontró y eliminó el valor
     */
    boolean delete(T n);
}
