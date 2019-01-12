package org.dgl.commons.util;

/**
 * Optimized ArrayList at the expense of no remove(i) functionality; hence it's "shiftless"
 */
public class ShiftlessArrayList<T> {
    private int capacity;
    private T[] data;
    private int size = 0;
    private int baseIndex = 0;

    public ShiftlessArrayList(int capacity) {
        this.capacity = capacity;
        data = (T[]) new Object[capacity];
    }

    public ShiftlessArrayList() {
        this(10000); //Default capacity
    }

    public void addLast(T object) {
        if (size >= capacity) {
            doubleCapacity();
        }
        data[size] = object;
        size++;
    }

    public void addFirst(T object) {
        if (size >= capacity) {
            doubleCapacity();
        }
        decrementBaseIndex();
        data[baseIndex] = object;
        size++;
    }

    public T get(int index) {
        return data[getRealIndex(index)];
    }

    public void put(T object, int index) {
        data[getRealIndex(index)] = object;
    }

    public int size() {
        return size;
    }

    public void removeFirst() {
        if (size > 0) {
            incrementBaseIndex();
            size--;
        }
    }

    public void removeLast() {
        if (size > 0) {
            size--;
        }
    }

    public void clear() {
        size = 0;
        baseIndex = 0;
    }

    private int getRealIndex(int virtualIndex) {
        if (virtualIndex < 0 || virtualIndex >= size) {
            throw new IndexOutOfBoundsException();
        }
        int realIndex = baseIndex + virtualIndex;
        if (realIndex >= capacity) {
            realIndex -= capacity;
        }
        return realIndex;
    }

    private void incrementBaseIndex() {
        if (baseIndex < capacity-1) {
            baseIndex++;
        } else {
            baseIndex = 0;
        }
    }

    private void decrementBaseIndex() {
        if (baseIndex > 0) {
            baseIndex--;
        } else {
            baseIndex = capacity-1;
        }
    }

    private void doubleCapacity() {
        capacity *= 2;
        T[] dataTemp = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            dataTemp[i] = get(i);
        }
        baseIndex = 0;
        data = dataTemp;
    }
}