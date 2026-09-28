package edu.unal.ed.list;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Lista doblemente enlazada CON puntero a la cola ({@code head} y {@code tail}).
 *
 * <pre>
 * pushFront O(1) | pushBack O(1) | popFront O(1) | popBack O(1) | find O(n)
 * erase O(1)     | addBefore O(1) | addAfter O(1) | topFront O(1) | topBack O(1)
 * </pre>
 * Es la implementación más completa: todo es O(1) salvo find.
 */
public class DoublyLinkedListWithTail<T> implements MyList<T> {

    private DoublyNode<T> head;
    private DoublyNode<T> tail;
    private int size;

    @Override
    public void pushFront(T value) { // O(1)
        DoublyNode<T> node = new DoublyNode<>(value, null, head);
        if (head == null) {
            tail = node;
        } else {
            head.prev = node;
        }
        head = node;
        size++;
    }

    @Override
    public void pushBack(T value) { // O(1)
        DoublyNode<T> node = new DoublyNode<>(value, tail, null);
        if (tail == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
        size++;
    }

    @Override
    public T popFront() { // O(1)
        requireNotEmpty();
        T value = head.value;
        head = head.next;
        if (head == null) {
            tail = null;
        } else {
            head.prev = null;
        }
        size--;
        return value;
    }

    @Override
    public T popBack() { // O(1)
        requireNotEmpty();
        T value = tail.value;
        tail = tail.prev;
        if (tail == null) {
            head = null;
        } else {
            tail.next = null;
        }
        size--;
        return value;
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
        if (target.next == null) {
            tail = target.prev;
        } else {
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
        if (target.next == null) {
            tail = inserted;
        } else {
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
    public T topBack() { // O(1)
        requireNotEmpty();
        return tail.value;
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
