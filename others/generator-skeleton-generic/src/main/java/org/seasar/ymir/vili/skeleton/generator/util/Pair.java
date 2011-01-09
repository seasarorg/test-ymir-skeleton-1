package org.seasar.ymir.vili.skeleton.generator.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pair<T> implements Comparable<Pair<T>> {
    private int order;

    private T object;

    public static <T> List<T> toOrderedObjectList(List<Pair<T>> pairs) {
        Collections.sort(pairs);

        List<T> list = new ArrayList<T>();
        for (Pair<T> pair : pairs) {
            list.add(pair.getObject());
        }
        return list;
    }

    public Pair(int order, T object) {
        this.order = order;
        this.object = object;
    }

    public int getOrder() {
        return order;
    }

    public T getObject() {
        return object;
    }

    public int compareTo(Pair<T> o) {
        if (order > o.order) {
            return 1;
        } else if (order < o.order) {
            return -1;
        } else {
            return 0;
        }
    }
}
