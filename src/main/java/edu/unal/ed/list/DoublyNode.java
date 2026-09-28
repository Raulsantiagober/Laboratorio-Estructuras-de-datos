package edu.unal.ed.list;

import java.util.Objects;

/** Nodo de las listas doblemente enlazadas. */
final class DoublyNode<T> implements ListNode<T> {

    final T value;
    DoublyNode<T> prev;
    DoublyNode<T> next;

    DoublyNode(T value, DoublyNode<T> prev, DoublyNode<T> next) {
        this.value = value;
        this.prev = prev;
        this.next = next;
    }

    @Override
    public T getValue() {
        return value;
    }

    /** Convierte la referencia pública a nodo doble, validando que no sea null ni de otro tipo. */
    static <T> DoublyNode<T> from(ListNode<T> node) {
        Objects.requireNonNull(node, "node");
        if (!(node instanceof DoublyNode)) {
            throw new IllegalArgumentException("El nodo no pertenece a una lista doblemente enlazada");
        }
        return (DoublyNode<T>) node;
    }
}
