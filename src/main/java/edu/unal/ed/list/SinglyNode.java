package edu.unal.ed.list;

import java.util.Objects;

/** Nodo de las listas simplemente enlazadas. */
final class SinglyNode<T> implements ListNode<T> {

    final T value;
    SinglyNode<T> next;

    SinglyNode(T value, SinglyNode<T> next) {
        this.value = value;
        this.next = next;
    }

    @Override
    public T getValue() {
        return value;
    }

    /** Convierte la referencia pública a nodo simple, validando que no sea null ni de otro tipo. */
    static <T> SinglyNode<T> from(ListNode<T> node) {
        Objects.requireNonNull(node, "node");
        if (!(node instanceof SinglyNode)) {
            throw new IllegalArgumentException("El nodo no pertenece a una lista simplemente enlazada");
        }
        return (SinglyNode<T>) node;
    }
}
