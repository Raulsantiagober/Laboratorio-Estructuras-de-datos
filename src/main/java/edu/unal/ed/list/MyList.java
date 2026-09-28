package edu.unal.ed.list;

/**
 * Contrato común de las cuatro listas enlazadas del laboratorio.
 *
 * <p>Decisiones de diseño (no especificadas en el enunciado):
 * <ul>
 *   <li>{@code find} retorna la referencia al nodo ({@link ListNode}), o {@code null} si el
 *       valor no está. {@code erase}, {@code addBefore} y {@code addAfter} reciben esa referencia.</li>
 *   <li>Los métodos que leen o eliminan de una lista vacía lanzan
 *       {@link java.util.NoSuchElementException}.</li>
 *   <li>El nodo recibido por erase / addBefore / addAfter debe pertenecer a esta lista y no haber
 *       sido eliminado; en las listas dobles esto no se puede verificar en O(1).</li>
 * </ul>
 *
 * @param <T> tipo de los elementos
 */
public interface MyList<T> {

    /** Inserta al inicio. */
    void pushFront(T value);

    /** Inserta al final. */
    void pushBack(T value);

    /** Elimina y retorna el primer elemento. */
    T popFront();

    /** Elimina y retorna el último elemento. */
    T popBack();

    /** Retorna el nodo del primer elemento igual a {@code value}, o {@code null} si no existe. */
    ListNode<T> find(T value);

    /** Elimina el nodo indicado. */
    void erase(ListNode<T> node);

    /** Inserta {@code value} justo antes del nodo indicado. */
    void addBefore(ListNode<T> node, T value);

    /** Inserta {@code value} justo después del nodo indicado. */
    void addAfter(ListNode<T> node, T value);

    /** Indica si la lista no tiene elementos. */
    boolean isEmpty();

    /** Número de elementos. */
    int size();

    /** Retorna (sin eliminar) el primer elemento. */
    T topFront();

    /** Retorna (sin eliminar) el último elemento. */
    T topBack();
}
