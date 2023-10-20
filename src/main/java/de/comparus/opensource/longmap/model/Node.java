package de.comparus.opensource.longmap.model;

import java.util.HashMap;
import java.util.Objects;

public class Node<V> {

    private long key;
    private V value;

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Node<?> node = (Node<?>) o;

        return key == node.key;
    }

    @Override
    public int hashCode() {
        return (int) (key ^ (key >>> 32));
    }
}
