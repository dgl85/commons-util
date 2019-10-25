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
        this(100); //Default capacity
    }

    public void addLast(T object) {
        if (size >= capacity) {
            doubleCapacity();
        }
        data[getTopIndex()] = object;
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
        return data[getInternalArrayIndex(index)];
    }

    public void set(int index, T object) {
        data[getInternalArrayIndex(index)] = object;
    }

    public int size() {
        return size;
    }

    public void removeFirst() {
        if (size > 0) {
            set(0, null); // Clear for GC
            incrementBaseIndex();
            size--;
        }
    }

    public void removeLast() {
        if (size > 0) {
            set(size - 1, null); // Clear for GC
            size--;
        }
    }

    public void clear() {
        size = 0;
        baseIndex = 0;
        // Clear for GC
        for (int i = 0; i < data.length; i++) {
            data[i] = null;
        }
    }

    private int getTopIndex() {
        int topIndex = baseIndex + size;
        if (topIndex >= capacity) {
            topIndex -= capacity;
        }
        return topIndex;
    }

    private int getInternalArrayIndex(int listIndex) {
        if (listIndex < 0 || listIndex >= size) {
            throw new IndexOutOfBoundsException();
        }
        int arrayIndex = baseIndex + listIndex;
        if (arrayIndex >= capacity) {
            arrayIndex -= capacity;
        }
        return arrayIndex;
    }

    private void incrementBaseIndex() {
        if (baseIndex < capacity - 1) {
            baseIndex++;
        } else {
            baseIndex = 0;
        }
    }

    private void decrementBaseIndex() {
        if (baseIndex > 0) {
            baseIndex--;
        } else {
            baseIndex = capacity - 1;
        }
    }

    private void doubleCapacity() {
        T[] dataTemp = (T[]) new Object[capacity * 2];
        for (int i = 0; i < size; i++) {
            dataTemp[i] = get(i);
        }
        capacity *= 2;
        baseIndex = 0;
        data = dataTemp;
    }
}