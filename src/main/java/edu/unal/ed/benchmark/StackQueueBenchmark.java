package edu.unal.ed.benchmark;

import edu.unal.ed.queue.ArrayQueue;
import edu.unal.ed.queue.MyQueue;
import edu.unal.ed.stack.ArrayStack;
import edu.unal.ed.stack.MyStack;
import java.util.Random;

/**
 * Parte 2: mide cada método de MyStack y MyQueue cuando la estructura tiene n elementos, con la
 * misma metodología que {@link ListBenchmark} (una llamada cronometrada, deshacer sin cronometrar).
 */
public final class StackQueueBenchmark {

    public static final String[] STACK_METHODS = {"push", "pop", "peek", "isEmpty", "size", "delete"};
    public static final String[] QUEUE_METHODS = {"enqueue", "dequeue", "front", "isEmpty", "size", "delete"};

    private static final Integer MARK = -1;

    static long sink;

    private StackQueueBenchmark() {
    }

    public static ArrayStack<Integer> buildStack(Integer[] values) {
        ArrayStack<Integer> stack = new ArrayStack<>();
        for (Integer v : values) {
            stack.push(v);
        }
        return stack;
    }

    /**
     * Construye la cola y la "rota" un número aleatorio de posiciones para que el frente quede en
     * un punto arbitrario del arreglo y los datos den la vuelta (se ejercita la circularidad).
     */
    public static ArrayQueue<Integer> buildQueue(Integer[] values, Random rnd) {
        ArrayQueue<Integer> queue = new ArrayQueue<>();
        for (Integer v : values) {
            queue.enqueue(v);
        }
        int rotations = rnd.nextInt(values.length);
        for (int i = 0; i < rotations; i++) {
            queue.enqueue(queue.dequeue());
        }
        return queue;
    }

    public static long[] measureStack(MyStack<Integer> stack, String method, Integer[] values,
                                      int reps, Random rnd) {
        long[] samples = new long[reps];
        int n = values.length;
        for (int r = 0; r < reps; r++) {
            Integer key = values[rnd.nextInt(n)];
            long t0;
            long t1;
            switch (method) {
                case "push" -> {
                    t0 = System.nanoTime();
                    stack.push(MARK);
                    t1 = System.nanoTime();
                    stack.pop();
                }
                case "pop" -> {
                    t0 = System.nanoTime();
                    Integer x = stack.pop();
                    t1 = System.nanoTime();
                    stack.push(x);
                }
                case "peek" -> {
                    t0 = System.nanoTime();
                    Integer x = stack.peek();
                    t1 = System.nanoTime();
                    sink += x;
                }
                case "isEmpty" -> {
                    t0 = System.nanoTime();
                    boolean empty = stack.isEmpty();
                    t1 = System.nanoTime();
                    sink += empty ? 1 : 0;
                }
                case "size" -> {
                    t0 = System.nanoTime();
                    int size = stack.size();
                    t1 = System.nanoTime();
                    sink += size;
                }
                case "delete" -> {
                    t0 = System.nanoTime();
                    boolean deleted = stack.delete(key);
                    t1 = System.nanoTime();
                    if (!deleted) {
                        throw new IllegalStateException("delete no encontró " + key);
                    }
                    stack.push(key);
                }
                default -> throw new IllegalArgumentException("Método desconocido: " + method);
            }
            samples[r] = t1 - t0;
        }
        if (stack.size() != n) {
            throw new IllegalStateException(method + " no dejó la pila en tamaño " + n);
        }
        return samples;
    }

    public static long[] measureQueue(MyQueue<Integer> queue, String method, Integer[] values,
                                      int reps, Random rnd) {
        long[] samples = new long[reps];
        int n = values.length;
        for (int r = 0; r < reps; r++) {
            Integer key = values[rnd.nextInt(n)];
            long t0;
            long t1;
            switch (method) {
                case "enqueue" -> {
                    // Se re-encola el valor del frente y luego se saca: la cola "rota" una
                    // posición, conserva sus n valores y no hay que retirar por el final.
                    Integer f = queue.front();
                    t0 = System.nanoTime();
                    queue.enqueue(f);
                    t1 = System.nanoTime();
                    queue.dequeue();
                }
                case "dequeue" -> {
                    t0 = System.nanoTime();
                    Integer x = queue.dequeue();
                    t1 = System.nanoTime();
                    queue.enqueue(x);
                }
                case "front" -> {
                    t0 = System.nanoTime();
                    Integer x = queue.front();
                    t1 = System.nanoTime();
                    sink += x;
                }
                case "isEmpty" -> {
                    t0 = System.nanoTime();
                    boolean empty = queue.isEmpty();
                    t1 = System.nanoTime();
                    sink += empty ? 1 : 0;
                }
                case "size" -> {
                    t0 = System.nanoTime();
                    int size = queue.size();
                    t1 = System.nanoTime();
                    sink += size;
                }
                case "delete" -> {
                    t0 = System.nanoTime();
                    boolean deleted = queue.delete(key);
                    t1 = System.nanoTime();
                    if (!deleted) {
                        throw new IllegalStateException("delete no encontró " + key);
                    }
                    queue.enqueue(key);
                }
                default -> throw new IllegalArgumentException("Método desconocido: " + method);
            }
            samples[r] = t1 - t0;
        }
        if (queue.size() != n) {
            throw new IllegalStateException(method + " no dejó la cola en tamaño " + n);
        }
        return samples;
    }

    /**
     * Peor caso de push/enqueue: se llena un arreglo de capacidad exactamente n y se cronometra la
     * inserción n+1, que obliga a duplicar la capacidad y copiar los n elementos.
     */
    public static long[] measureResizeWorstCase(boolean queue, Integer[] values, int reps) {
        long[] samples = new long[reps];
        int n = values.length;
        for (int r = 0; r < reps; r++) {
            long t0;
            long t1;
            if (queue) {
                ArrayQueue<Integer> q = new ArrayQueue<>(n);
                for (Integer v : values) {
                    q.enqueue(v);
                }
                t0 = System.nanoTime();
                q.enqueue(MARK);
                t1 = System.nanoTime();
                sink += q.capacity();
            } else {
                ArrayStack<Integer> s = new ArrayStack<>(n);
                for (Integer v : values) {
                    s.push(v);
                }
                t0 = System.nanoTime();
                s.push(MARK);
                t1 = System.nanoTime();
                sink += s.capacity();
            }
            samples[r] = t1 - t0;
        }
        return samples;
    }
}
