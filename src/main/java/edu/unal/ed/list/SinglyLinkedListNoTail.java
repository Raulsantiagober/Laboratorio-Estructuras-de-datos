package edu.unal.ed.list;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Lista simplemente enlazada SIN puntero a la cola (solo {@code head}).
 *
 * <pre>
 * pushFront O(1) | pushBack O(n) | popFront O(1) | popBack O(n) | find O(n)
 * erase O(n)     | addBefore O(n) | addAfter O(1) | topFront O(1) | topBack O(n)
 * </pre>
 * erase y addBefore son O(n) aunque se reciba el nodo: sin {@code prev} hay que recorrer
 * desde {@code head} para hallar su predecesor.
 */
public class SinglyLinkedListNoTail<T> implements MyList<T> {

    private SinglyNode<T> head;
    private int size;

    @Override
    public void pushFront(T value) { // O(1)
        head = new SinglyNode<>(value, head);
        size++;
    }

    @Override
    public void pushBack(T value) { // O(n): hay que llegar al último nodo
        if (head == null) {
            pushFront(value);
            return;
        }
        lastNode().next = new SinglyNode<>(value, null);
        size++;
    }

    @Override
    public T popFront() { // O(1)
        requireNotEmpty();
        T value = head.value;
        head = head.next;
        size--;
        return value;
    }

    @Override
    public T popBack() { // O(n): hay que llegar al penúltimo nodo
        requireNotEmpty();
        if (head.next == null) {
            return popFront();
        }
        SinglyNode<T> beforeLast = head;
        while (beforeLast.next.next != null) {
            beforeLast = beforeLast.next;
        }
        T value = beforeLast.next.value;
        beforeLast.next = null;
        size--;
        return value;
    }

    @Override
    public ListNode<T> find(T value) { // O(n)
        for (SinglyNode<T> n = head; n != null; n = n.next) {
            if (Objects.equals(n.value, value)) {
                return n;
            }
        }
        return null;
    }

    @Override
    public void erase(ListNode<T> node) { // O(n): buscar el predecesor
        SinglyNode<T> target = SinglyNode.from(node);
        if (target == head) {
            head = head.next;
        } else {
            predecessorOf(target).next = target.next;
        }
        size--;
    }

    @Override
    public void addBefore(ListNode<T> node, T value) { // O(n): buscar el predecesor
        SinglyNode<T> target = SinglyNode.from(node);
        if (target == head) {
            pushFront(value);
            return;
        }
        predecessorOf(target).next = new SinglyNode<>(value, target);
        size++;
    }

    @Override
    public void addAfter(ListNode<T> node, T value) { // O(1)
        SinglyNode<T> target = SinglyNode.from(node);
        target.next = new SinglyNode<>(value, target.next);
        size++;
    }

    @Override
    public boolean isEmpty() { // O(1)
        return size == 0;
    }

    @Override
    public int size() { // O(1)
        return size;
    }

    @Override
    public T topFront() { // O(1)
        requireNotEmpty();
        return head.value;
    }

    @Override
    public T topBack() { // O(n)
        requireNotEmpty();
        return lastNode().value;
    }

    private SinglyNode<T> lastNode() {
        SinglyNode<T> n = head;
        while (n.next != null) {
            n = n.next;
        }
        return n;
    }

    private SinglyNode<T> predecessorOf(SinglyNode<T> target) {
        SinglyNode<T> p = head;
        while (p != null && p.next != target) {
            p = p.next;
        }
        if (p == null) {
            throw new NoSuchElementException("El nodo no pertenece a la lista");
        }
        return p;
    }

    private void requireNotEmpty() {
        if (head == null) {
            throw new NoSuchElementException("La lista está vacía");
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (SinglyNode<T> n = head; n != null; n = n.next) {
            if (n != head) {
                sb.append(", ");
            }
            sb.append(n.value);
        }
        return sb.append(']').toString();
    }
}
