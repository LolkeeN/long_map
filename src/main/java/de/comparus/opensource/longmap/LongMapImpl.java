package de.comparus.opensource.longmap;

import de.comparus.opensource.longmap.model.Node;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LongMapImpl<V> implements LongMap<V> {

    private final V[] valueArray;
    private final List<Node<V>> nodes = new ArrayList<>();

    public LongMapImpl(Class<V> clazz) {
        @SuppressWarnings("unchecked") final V[] a = (V[]) Array.newInstance(clazz, 0);
        this.valueArray = a;
    }

    public V put(long key, V value) {
        Node<V> node = new Node<>();
        node.setKey(key);
        node.setValue(value);
        nodes.remove(node);
        nodes.add(node);
        return node.getValue();
    }

    public V get(long key) {
        Node<V> node = nodes.stream()
                .filter(x -> x.getKey() == key)
                .findFirst().orElse(null);
        return node == null ? null : node.getValue();
    }

    public V remove(long key) {
        V value = get(key);
        Node<V> node = new Node<>();
        node.setKey(key);
        nodes.remove(node);
        return value;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public boolean containsKey(long key) {
        Node<V> node = new Node<>();
        node.setKey(key);
        return nodes.contains(node);
    }

    public boolean containsValue(V value) {
        Node<V> node = nodes.stream()
                .filter(x -> x.getValue().equals(value))
                .findAny().orElse(null);
        return node != null;
    }

    public long[] keys() {
        return nodes.stream()
                .mapToLong(Node::getKey)
                .toArray();
    }

    public V[] values() {
        List<V> collect = nodes.stream()
                .map(Node::getValue)
                .collect(Collectors.toList());
        return toArray(collect);
    }

    public long size() {
        return nodes.size();
    }

    public void clear() {
        nodes.clear();
    }

    private V[] toArray(List<V> list) {
        V[] result = Arrays.copyOf(valueArray, list.size());
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }
}
