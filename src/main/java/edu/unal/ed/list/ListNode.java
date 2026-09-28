package edu.unal.ed.list;

/**
 * Referencia opaca a un nodo de una lista. Es lo que retorna {@link MyList#find(Object)}
 * y lo que reciben erase / addBefore / addAfter.
 *
 * @param <T> tipo del valor almacenado
 */
public interface ListNode<T> {

    /** Valor almacenado en el nodo. O(1). */
    T getValue();
}
