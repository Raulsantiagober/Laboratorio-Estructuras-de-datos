package edu.unal.ed.benchmark;

import edu.unal.ed.list.ListNode;
import edu.unal.ed.list.MyList;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Parte 1: mide cada método de una lista cuando la lista tiene exactamente n elementos.
 *
 * <p>Metodología de cada repetición:
 * <ol>
 *   <li>(sin cronometrar) se elige al azar un valor de la lista y, si el método lo necesita,
 *       se busca su nodo;</li>
 *   <li>se cronometra UNA llamada con {@link System#nanoTime()};</li>
 *   <li>(sin cronometrar) se deshace el efecto para que la lista vuelva a tener n elementos.</li>
 * </ol>
 * Así el tiempo registrado es "cuánto cuesta una operación cuando la estructura tiene n
 * elementos", que es lo que describe la notación Big-O, y la lista se construye una sola vez por
 * tamaño en vez de reconstruirse en cada repetición.
 */
public final class ListBenchmark {

    /** Métodos medidos. FindErase se usa en la Parte 3 (equivalente a delete(n)). */
    public static final String[] METHODS = {
        "PushFront", "PushBack", "PopFront", "PopBack", "Find", "Erase", "AddBefore",
        "AddAfter", "TopFront", "TopBack", "Empty", "Size", "FindErase"
    };

    /** Valor marcador que nunca está en la lista (los valores son 0..n-1). */
    private static final Integer MARK = -1;

    /** Consume resultados para que el JIT no elimine llamadas "sin efecto". */
    static long sink;

    private ListBenchmark() {
    }

    /** Construye la lista con los valores en el orden dado usando solo pushFront (O(1) en las 4). */
    public static MyList<Integer> build(Supplier<MyList<Integer>> factory, Integer[] values) {
        MyList<Integer> list = factory.get();
        for (int i = values.length - 1; i >= 0; i--) {
            list.pushFront(values[i]);
        }
        return list;
    }

    /** Devuelve una muestra (ns) por repetición. La lista queda con el mismo tamaño n. */
    public static long[] measure(MyList<Integer> list, String method, Integer[] values,
                                 int reps, Random rnd) {
        long[] samples = new long[reps];
        int n = values.length;
        for (int r = 0; r < reps; r++) {
            Integer key = values[rnd.nextInt(n)]; // posición aleatoria uniforme
            long t0;
            long t1;
            switch (method) {
                case "PushFront" -> {
                    t0 = System.nanoTime();
                    list.pushFront(MARK);
                    t1 = System.nanoTime();
                    list.popFront();
                }
                case "PushBack" -> {
                    t0 = System.nanoTime();
                    list.pushBack(MARK);
                    t1 = System.nanoTime();
                    list.popBack();
                }
                case "PopFront" -> {
                    t0 = System.nanoTime();
                    Integer x = list.popFront();
                    t1 = System.nanoTime();
                    list.pushFront(x);
                }
                case "PopBack" -> {
                    t0 = System.nanoTime();
                    Integer x = list.popBack();
                    t1 = System.nanoTime();
                    list.pushBack(x);
                }
                case "Find" -> {
                    t0 = System.nanoTime();
                    ListNode<Integer> node = list.find(key);
                    t1 = System.nanoTime();
                    sink += node.getValue();
                }
                case "Erase" -> {
                    ListNode<Integer> node = list.find(key);
                    t0 = System.nanoTime();
                    list.erase(node);
                    t1 = System.nanoTime();
                    list.pushFront(key);
                }
                case "AddBefore" -> {
                    ListNode<Integer> node = list.find(key);
                    t0 = System.nanoTime();
                    list.addBefore(node, MARK);
                    t1 = System.nanoTime();
                    list.erase(list.find(MARK));
                }
                case "AddAfter" -> {
                    ListNode<Integer> node = list.find(key);
                    t0 = System.nanoTime();
                    list.addAfter(node, MARK);
                    t1 = System.nanoTime();
                    list.erase(list.find(MARK));
                }
                case "TopFront" -> {
                    t0 = System.nanoTime();
                    Integer x = list.topFront();
                    t1 = System.nanoTime();
                    sink += x;
                }
                case "TopBack" -> {
                    t0 = System.nanoTime();
                    Integer x = list.topBack();
                    t1 = System.nanoTime();
                    sink += x;
                }
                case "Empty" -> {
                    t0 = System.nanoTime();
                    boolean empty = list.isEmpty();
                    t1 = System.nanoTime();
                    sink += empty ? 1 : 0;
                }
                case "Size" -> {
                    t0 = System.nanoTime();
                    int size = list.size();
                    t1 = System.nanoTime();
                    sink += size;
                }
                case "FindErase" -> {
                    t0 = System.nanoTime();
                    list.erase(list.find(key));
                    t1 = System.nanoTime();
                    list.pushFront(key);
                }
                default -> throw new IllegalArgumentException("Método desconocido: " + method);
            }
            samples[r] = t1 - t0;
        }
        if (list.size() != n) {
            throw new IllegalStateException(method + " no dejó la lista en tamaño " + n);
        }
        return samples;
    }
}
