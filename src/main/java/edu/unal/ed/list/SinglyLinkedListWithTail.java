package edu.unal.ed.list;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Lista simplemente enlazada CON puntero a la cola ({@code head} y {@code tail}).
 *
 * <pre>
 * pushFront O(1) | pushBack O(1) | popFront O(1) | popBack O(n) | find O(n)
 * erase O(n)     | addBefore O(n) | addAfter O(1) | topFront O(1) | topBack O(1)
 * </pre>
 * popBack sigue siendo O(n): conocer {@code tail} no da acceso al penúltimo nodo sin {@code prev}.
 */
public class SinglyLinkedListWithTail<T> implements MyList<T> {

    private SinglyNode<T> head;
    private SinglyNode<T> tail;
    private int size;

    @Override
    public void pushFront(T value) { // O(1)
        SinglyNode<T> node = new SinglyNode<>(value, head);
        head = node;
        if (tail == null) {
            tail = node;
        }
        size++;
    }

    @Override
    public void pushBack(T value) { // O(1)
        SinglyNode<T> node = new SinglyNode<>(value, null);
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
        }
        size--;
        return value;
    }

    @Override
    public T popBack() { // O(n): hay que llegar al penúltimo nodo
        requireNotEmpty();
        T value = tail.value;
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            SinglyNode<T> beforeLast = head;
            while (beforeLast.next != tail) {
                beforeLast = beforeLast.next;
            }
            beforeLast.next = null;
            tail = beforeLast;
        }
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
            if (head == null) {
                tail = null;
            }
        } else {
            SinglyNode<T> pred = predecessorOf(target);
            pred.next = target.next;
            if (target == tail) {
                tail = pred;
            }
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
        SinglyNode<T> inserted = new SinglyNode<>(value, target.next);
        target.next = inserted;
        if (target == tail) {
            tail = inserted;
        }
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
