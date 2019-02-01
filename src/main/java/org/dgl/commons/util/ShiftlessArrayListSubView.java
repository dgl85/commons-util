package org.dgl.commons.util;

public class ShiftlessArrayListSubView<T> {

    private final ShiftlessArrayList<T> parent;
    private final int startIndex;
    private final int endIndex;
    private final int size;

    /**
     *
     * @param parent
     * @param startIndex inclusive
     * @param endIndex exclusive
     */
    public ShiftlessArrayListSubView(ShiftlessArrayList<T> parent, int startIndex, int endIndex) {
        if (endIndex <= startIndex || startIndex < 0 || endIndex > parent.size()) {
            throw new IllegalArgumentException();
        }
        this.parent = parent;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        size = endIndex - startIndex;
    }

    public int size() {
        return size;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return parent.get(startIndex+index);
    }

}
