package org.dgl.commons.util;

public class Tuple <T1,T2> {

    private T1 object1;
    private T2 object2;

    public Tuple(T1 object1, T2 object2) {
        this.object1 = object1;
        this.object2 = object2;
    }

    public void setFirst(T1 object) {
        this.object1 = object1;
    }

    public void setSecond(T2 object) {
        this.object2 = object2;
    }

    public void set(T1 object1, T2 object2) {
        setFirst(object1);
        setSecond(object2);
    }

    public T1 getFirst() {
        return object1;
    }

    public T2 getSecond() {
        return object2;
    }
}
