package edu.unal.ed.queue;

import edu.unal.ed.circular.CircularArray;

/**
 * {@link MyQueue} sobre un arreglo dinámico circular.
 *
 * <p>Se inserta por el final (addLast) y se retira por el frente (removeFirst). Gracias al buffer
 * circular el frente avanza módulo la capacidad y dequeue es O(1) sin desplazar elementos.
 *
 * <pre>
 * enqueue O(1) amortizado (O(n) peor caso) | dequeue O(1) | front O(1)
 * isEmpty O(1) | size O(1) | delete O(n)
 * </pre>
 */
public class ArrayQueue<T> implements MyQueue<T> {

    private final CircularArray<T> buffer;

    public ArrayQueue() {
        buffer = new CircularArray<>();
    }

    public ArrayQueue(int initialCapacity) {
        buffer = new CircularArray<>(initialCapacity);
    }

    @Override
    public void enqueue(T x) {
        buffer.addLast(x);
    }

    @Override
    public T dequeue() {
        return buffer.removeFirst();
    }

    @Override
    public T front() {
        return buffer.peekFirst();
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
        return buffer.removeFirstOccurrence(n); // desde el frente
    }

    /** Capacidad física actual (para pruebas y análisis). */
    public int capacity() {
        return buffer.capacity();
    }

    /** Contenido del frente al final. */
    @Override
    public String toString() {
        return buffer.toString();
    }
}
