package edu.unal.ed.list;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Lista doblemente enlazada SIN puntero a la cola (solo {@code head}).
 *
 * <pre>
 * pushFront O(1) | pushBack O(n) | popFront O(1) | popBack O(n) | find O(n)
 * erase O(1)     | addBefore O(1) | addAfter O(1) | topFront O(1) | topBack O(n)
 * </pre>
 * Gracias a {@code prev}, erase / addBefore / addAfter son O(1) dado el nodo.
 */
public class DoublyLinkedListNoTail<T> implements MyList<T> {

    private DoublyNode<T> head;
    private int size;

    @Override
    public void pushFront(T value) { // O(1)
        DoublyNode<T> node = new DoublyNode<>(value, null, head);
        if (head != null) {
            head.prev = node;
        }
        head = node;
        size++;
    }

    @Override
    public void pushBack(T value) { // O(n): hay que llegar al último nodo
        if (head == null) {
            pushFront(value);
            return;
        }
        DoublyNode<T> last = lastNode();
        last.next = new DoublyNode<>(value, last, null);
        size++;
    }

    @Override
    public T popFront() { // O(1)
        requireNotEmpty();
        T value = head.value;
        head = head.next;
        if (head != null) {
            head.prev = null;
        }
        size--;
        return value;
    }

    @Override
    public T popBack() { // O(n): hay que llegar al último nodo
        requireNotEmpty();
        DoublyNode<T> last = lastNode();
        if (last.prev == null) {
            head = null;
        } else {
            last.prev.next = null;
        }
        size--;
        return last.value;
    }

    @Override
    public ListNode<T> find(T value) { // O(n)
        for (DoublyNode<T> n = head; n != null; n = n.next) {
            if (Objects.equals(n.value, value)) {
                return n;
            }
        }
        return null;
    }

    @Override
    public void erase(ListNode<T> node) { // O(1)
        DoublyNode<T> target = DoublyNode.from(node);
        if (target.prev == null) {
            head = target.next;
        } else {
            target.prev.next = target.next;
        }
        if (target.next != null) {
            target.next.prev = target.prev;
        }
        size--;
    }

    @Override
    public void addBefore(ListNode<T> node, T value) { // O(1)
        DoublyNode<T> target = DoublyNode.from(node);
        DoublyNode<T> inserted = new DoublyNode<>(value, target.prev, target);
        if (target.prev == null) {
            head = inserted;
        } else {
            target.prev.next = inserted;
        }
        target.prev = inserted;
        size++;
    }

    @Override
    public void addAfter(ListNode<T> node, T value) { // O(1)
        DoublyNode<T> target = DoublyNode.from(node);
        DoublyNode<T> inserted = new DoublyNode<>(value, target, target.next);
        if (target.next != null) {
            target.next.prev = inserted;
        }
        target.next = inserted;
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

    private DoublyNode<T> lastNode() {
        DoublyNode<T> n = head;
        while (n.next != null) {
            n = n.next;
        }
        return n;
    }

    private void requireNotEmpty() {
        if (head == null) {
            throw new NoSuchElementException("La lista está vacía");
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (DoublyNode<T> n = head; n != null; n = n.next) {
            if (n != head) {
                sb.append(", ");
            }
            sb.append(n.value);
        }
        return sb.append(']').toString();
    }
}
