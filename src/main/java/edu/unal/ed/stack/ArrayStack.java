package edu.unal.ed.stack;

import edu.unal.ed.circular.CircularArray;

/**
 * {@link MyStack} sobre un arreglo dinámico circular.
 *
 * <p>La cima es el ÚLTIMO elemento lógico del buffer (push = addLast, pop = removeLast).
 * En una pila el frente nunca se mueve, así que la circularidad no se aprovecha, pero se usa
 * el mismo buffer que la cola para unificar la estrategia de crecimiento.
 *
 * <pre>
 * push O(1) amortizado (O(n) peor caso) | pop O(1) | peek O(1)
 * isEmpty O(1) | size O(1) | delete O(n)
 * </pre>
 */
public class ArrayStack<T> implements MyStack<T> {

    private final CircularArray<T> buffer;

    public ArrayStack() {
        buffer = new CircularArray<>();
    }

    public ArrayStack(int initialCapacity) {
        buffer = new CircularArray<>(initialCapacity);
    }

    @Override
    public void push(T x) {
        buffer.addLast(x);
    }

    @Override
    public T pop() {
        return buffer.removeLast();
    }

    @Override
    public T peek() {
        return buffer.peekLast();
    }

    @Override
    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    @Override
    public int size() {
        return buffer.size();
    }

    @Override
    public boolean delete(T n) {
        return buffer.removeLastOccurrence(n); // desde la cima
    }

    /** Capacidad física actual (para pruebas y análisis). */
    public int capacity() {
        return buffer.capacity();
    }

    /** Contenido de la base a la cima. */
    @Override
    public String toString() {
        return buffer.toString();
    }
}
