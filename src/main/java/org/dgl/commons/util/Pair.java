package org.dgl.commons.util;

import java.io.Serializable;

public class Pair<T1, T2> implements Serializable {

    private static final long serialVersionUID = 20180219002L;
    private T1 object1;
    private T2 object2;

    public Pair(T1 object1, T2 object2) {
        this.object1 = object1;
        this.object2 = object2;
    }

    public void set(T1 object1, T2 object2) {
        setFirst(object1);
        setSecond(object2);
    }

    public T1 getFirst() {
        return object1;
    }

    public void setFirst(T1 object) {
        this.object1 = object1;
    }

    public T2 getSecond() {
        return object2;
    }

    public void setSecond(T2 object) {
        this.object2 = object2;
    }
}
