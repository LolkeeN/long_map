package de.comparus.opensource.longmap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LongMapImpl<V> implements LongMap<V> {

    private final V[] valueArray;
    private final List<List<Node<V>>> buckets = new ArrayList<>();

    public LongMapImpl(Class<V> clazz) {
        @SuppressWarnings("unchecked") final V[] a = (V[]) Array.newInstance(clazz, 0);
        this.valueArray = a;
    }

    public V put(long key, V value) {
        Node<V> node = new Node<>();
        node.setKey(key);
        node.setValue(value);

        if (buckets.isEmpty()) {
            addBuckets();
            int bucketIndex = getBucketIndex(node);
            List<Node<V>> bucket = buckets.get(bucketIndex);
            bucket.add(node);
            return node.getValue();
        }

        if (buckets.stream()
                .filter(x -> !x.isEmpty())
                .count() / (float) buckets.size() > 0.75) {
            addBuckets();
        }

        int bucketIndex = getBucketIndex(node);
        List<Node<V>> bucket = buckets.get(bucketIndex);
        if (!buckets.get(bucketIndex).isEmpty()) {
            bucket.add(node);
            bucket.get(bucket.size() - 1).setNext(node);
            return node.getValue();
        } else {
            bucket.add(node);
            return node.getValue();
        }
    }

    private int getBucketIndex(Node<V> node) {
        return node.hashCode() % buckets.size();
    }

    private void addBuckets() {
        for (int i = 0; i < 5; i++) {
            buckets.add(new ArrayList<>());
        }
    }

    public V get(long key) {
        Node<V> node = new Node<>();
        node.setKey(key);
        List<Node<V>> nodes = buckets.get(getBucketIndex(node));
        if (nodes.isEmpty()) {
            return null;
        }
        return nodes.get(0).getValue();
    }

    public V remove(long key) {
        Node<V> node = new Node<>();
        node.setKey(key);
        List<Node<V>> bucket = buckets.get(getBucketIndex(node));
        Node<V> resultNode;
        if (!bucket.isEmpty()) {
            resultNode = bucket.get(0);
        } else {
            resultNode = null;
        }
        bucket.clear();
        return resultNode == null ? null : resultNode.getValue();
    }

    public boolean isEmpty() {
        return buckets.isEmpty() || buckets.stream()
                .allMatch(List::isEmpty);
    }

    public boolean containsKey(long key) {
        boolean contains;
        for (List<Node<V>> bucket : buckets) {
            if (!bucket.isEmpty()) {
                contains = bucket.get(0).getKey() == key;
                if (contains) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsValue(V value) {
        boolean contains;
        for (List<Node<V>> bucket : buckets) {
            if (!bucket.isEmpty()) {
                contains = bucket.get(0).getValue().equals(value);
                if (contains) {
                    return true;
                }
            }
        }
        return false;
    }

    public long[] keys() {
        return buckets.stream()
                .mapToLong(x -> x.stream()
                        .map(Node::getKey)
                        .findFirst().orElse(Long.MIN_VALUE))
                .filter(x -> x != Long.MIN_VALUE)
                .toArray();
    }

    public V[] values() {
        List<V> collect = buckets.stream()
                .flatMap(x -> x.stream()
                        .map(Node::getValue))
                .collect(Collectors.toList());
        return toArray(collect);
    }

    public long size() {
        return buckets.stream()
                .filter(x -> !x.isEmpty())
                .count();
    }

    public void clear() {
        buckets.clear();
    }

    private V[] toArray(List<V> list) {
        V[] result = Arrays.copyOf(valueArray, list.size());
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    private static class Node<V> {

        private long key;
        private V value;
        private Node<V> next;

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

        public Node<V> getNext() {
            return next;
        }

        public void setNext(Node<V> next) {
            this.next = next;
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
}
