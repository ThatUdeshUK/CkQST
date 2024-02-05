package org.example.base;

import java.util.Comparator;
import java.util.PriorityQueue;

public class BoundedPriorityQueue<E> extends PriorityQueue<E> {
    private final int capacity;

    public BoundedPriorityQueue(int capacity, Comparator<? super E> comparator) {
        super(capacity, comparator);
        this.capacity = capacity;
    }

    @Override
    public boolean add(E e) {
        boolean changed = super.add(e);

        if (size() > capacity) {
            poll();
            changed = true;
        }

        return changed;
    }

    public boolean isFull() {
        return size() >= capacity;
    }
}
